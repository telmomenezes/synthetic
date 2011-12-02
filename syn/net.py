#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jun 2011"


import sqlite3
from syn.core import *


class Net:
    def __init__(self, dbpath, verbose=False):
        self.verbose = verbose
        self.conn = sqlite3.connect(dbpath)
        self.cur = self.conn.cursor()
        self.create_db()
        self.load_params()

    def __del__(self):
        self.conn.commit()
        self.cur.close()
        self.conn.close()

    def log(self, message):
        if self.verbose:
            print message

    def safe_execute(self, query):
        try:
            self.cur.execute(query)
            self.log('Executed query: %s' % query)
        except sqlite3.OperationalError:
            self.log('Failed query: %s' % query)

    def create_db(self):
        #create params table
        self.safe_execute("CREATE TABLE params (id INTEGER PRIMARY KEY)")
        self.safe_execute("ALTER TABLE params ADD COLUMN perm_edges INTEGER")

        #create interval table
        self.safe_execute("CREATE TABLE interval (id INTEGER PRIMARY KEY)")
        self.safe_execute("ALTER TABLE interval ADD COLUMN pos INTEGER")
        self.safe_execute("ALTER TABLE interval ADD COLUMN ts_start INTEGER")
        self.safe_execute("ALTER TABLE interval ADD COLUMN ts_end INTEGER")
        
        # create node table
        self.safe_execute("CREATE TABLE node (id INTEGER PRIMARY KEY)")
        self.safe_execute("ALTER TABLE node ADD COLUMN label TEXT")
        self.safe_execute("ALTER TABLE node ADD COLUMN ts_start INTEGER DEFAULT -1")
        self.safe_execute("ALTER TABLE node ADD COLUMN ts_end INTEGER DEFAULT -1")
        self.safe_execute("ALTER TABLE node ADD COLUMN selection INTEGER DEFAULT -1")

        # create edge table
        self.safe_execute("CREATE TABLE edge (id INTEGER PRIMARY KEY)")
        self.safe_execute("ALTER TABLE edge ADD COLUMN orig INTEGER")
        self.safe_execute("ALTER TABLE edge ADD COLUMN targ INTEGER")
        self.safe_execute("ALTER TABLE edge ADD COLUMN ts_start INTEGER DEFAULT -1")
        self.safe_execute("ALTER TABLE edge ADD COLUMN ts_end INTEGER DEFAULT -1")

        # create node_metrics table
        self.safe_execute("CREATE TABLE node_metrics (id INTEGER PRIMARY KEY)")
        self.safe_execute("ALTER TABLE node_metrics ADD COLUMN node_id INTEGER")
        self.safe_execute("ALTER TABLE node_metrics ADD COLUMN interval INTEGER DEFAULT -1")
        self.safe_execute("ALTER TABLE node_metrics ADD COLUMN in_degree INTEGER DEFAULT 0")
        self.safe_execute("ALTER TABLE node_metrics ADD COLUMN out_degree INTEGER DEFAULT 0")
        self.safe_execute("ALTER TABLE node_metrics ADD COLUMN in_pr REAL DEFAULT 0")
        self.safe_execute("ALTER TABLE node_metrics ADD COLUMN out_pr REAL DEFAULT 0")

        # create indexes
        self.safe_execute("CREATE INDEX node_id ON node (id)")
        self.safe_execute("CREATE INDEX node_super_node ON node (super_node)")
        self.safe_execute("CREATE INDEX node_super_node_int ON node (super_node, interval)")
        self.safe_execute("CREATE INDEX edge_id ON edge (id)")
        self.safe_execute("CREATE INDEX edge_orig ON edge (orig)")
        self.safe_execute("CREATE INDEX edge_orig_targ ON edge (orig, targ)")
        self.safe_execute("CREATE INDEX edge_ts_start ON edge (ts_start)")
        self.safe_execute("CREATE INDEX node_metrics_node_id_interval ON node_metrics (node_id, interval)")

    def load_params(self):
        self.cur.execute("SELECT perm_edges FROM params")
        row = self.cur.fetchone()
        
        # no params, create default
        self.perm_edges = True
        if row is None:
            self.cur.execute("INSERT INTO params (perm_edges) VALUES (1)")
            return

        if row[0] == 0:
            self.perm_edges = False

    def set_perm_edges(self, perm_edges):
        if perm_edges == self.perm_edges:
            return

        self.perm_edges = perm_edges
        if self.perm_edges:
            self.cur.execute("UPDATE params SET perm_edges=1")
        else:
            self.cur.execute("UPDATE params SET perm_edges=0")

    def save(self, syn_net):
        nodes = {}

        # add nodes
        node = net_first_node(syn_net)
        while node != 0:
            nid = node_id(node)
            nodes[nid] = self.add_node(nid)
            node = node_next_node(node)

        # add edges
        orig = net_first_node(syn_net)
        while orig != 0:
            orig_id = node_id(orig)
            edge = node_first_targ(orig)
            while edge != 0:
                targ = edge_targ(edge)
                targ_id = node_id(targ)
                self.add_edge(nodes[orig_id], nodes[targ_id])
                edge = edge_next_targ(edge)
            orig = node_next_node(orig)

    def load_net(self, min_ts=-1, max_ts=-1):
        net = create_net()

        self.cur.execute("SELECT id FROM node")
        
        nodes = {}
        for row in self.cur:
            nid = row[0]
            nodes[nid] = add_node_with_id(net, nid, 0)

        if self.perm_edges:
            if min_ts >= 0:
                if max_ts >= 0:
                    self.cur.execute("SELECT orig, targ, ts_start FROM edge WHERE ts_start>=%f AND ts_start<%f" % (min_ts, max_ts))
                else:
                    self.cur.execute("SELECT orig, targ, ts_start FROM edge WHERE ts_start>=%f" % (min_ts))
            else:
                self.cur.execute("SELECT orig, targ, ts_start FROM edge")
        else:
            self.cur.execute("SELECT orig, targ, ts_start FROM edge WHERE ts_end=-1")

        for row in self.cur:
            add_edge_to_net(net, nodes[row[0]], nodes[row[1]], int(row[2]))

        return net

    def load_interval_net(self, int_number):
        net = create_net()

        self.cur.execute("SELECT id, ts_start, ts_end FROM interval WHERE pos=?", (int_number,))
        row = self.cur.fetchone()
        int_id = row[0]
        min_ts = row[1]
        max_ts = row[2]
        
        nodes = {}
        self.cur.execute("SELECT id FROM node")
        for row in self.cur:
            nid = row[0]
            nodes[nid] = add_node_with_id(net, nid, 0)

        if max_ts == 0:
            self.cur.execute("SELECT orig, targ, ts_start FROM edge")
        else:
            self.cur.execute("SELECT orig, targ, ts_start FROM edge WHERE ts_start>=? AND ts_start<?", (min_ts, max_ts))

        for row in self.cur:
            add_edge_to_net(net, nodes[row[0]], nodes[row[1]], row[2])

        return net

    def add_node(self, label=''):
        self.cur.execute("INSERT INTO node (label) VALUES (?)", (label,))    
        return self.cur.lastrowid

    def add_edge(self, orig, targ, timestamp=-1, ts_end=-1):
        self.cur.execute("INSERT INTO edge (orig, targ, ts_start, ts_end) VALUES (?, ?, ?, ?)", (orig, targ, timestamp, ts_end))    
        return self.cur.lastrowid

    def min_edge_ts(self):
        self.cur.execute("SELECT min(ts_start) FROM edge WHERE ts_start > 0")
        row = self.cur.fetchone()
        if row[0] == None:
            return 0
        return row[0]

    def max_edge_ts(self):
        self.cur.execute("SELECT max(ts_start) FROM edge WHERE ts_start > 0")
        row = self.cur.fetchone()
        if row[0] == None:
            return 0
        return row[0]

    def divide_in_intervals(self, n_intvls, min_time=0):
        self.remove_intervals()

        self.log('Dividing network in intervals.')
        min_ts = max(self.min_edge_ts(), min_time)
        max_ts = self.max_edge_ts()
        interval = (max_ts - min_ts) / n_intvls
        cur_ts = min_ts + interval

        self.log('min ts: %d; max ts: %d; interval: %d; number of intervals: %d' % (min_ts, max_ts, interval, n_intvls))
        for i in range(n_intvls):
            self.log('generating interval %d' % i)
            self.cur.execute("INSERT INTO interval (pos, ts_start, ts_end) VALUES (?, ?, ?)", (i, min_ts, cur_ts))
            int_id = self.cur.lastrowid
            syn_net = 0
            if n_intvls > 1:
                syn_net = self.load_net(min_ts, cur_ts)
            else:
                syn_net = self.load_net()

            node = net_first_node(syn_net)
            while node != 0:
                nid = node_id(node)
                in_degree = node_in_degree(node)
                out_degree = node_out_degree(node)

                self.cur.execute("INSERT INTO node_metrics (node_id, interval, in_degree, out_degree) VALUES (?, ?, ?, ?)", (nid, int_id, in_degree, out_degree))

                node = node_next_node(node)

            destroy_net(syn_net)

            cur_ts += interval

        self.conn.commit()

    def remove_intervals(self):
        self.log('Removing existing intervals.')
        self.cur.execute("DELETE FROM interval")
        self.cur.execute("DELETE FROM node_metrics")
        self.conn.commit()

    def get_number_intervals(self):
        self.cur.execute("SELECT count(id) FROM interval")
        return self.cur.fetchone()[0]

    def compute_page_ranks(self):
        n_intvls = self.get_number_intervals()
    
        # compute interval page ranks
        for i in range(n_intvls):
	    print 'computing page ranks for interval %d' % i
            self.cur.execute("SELECT id FROM interval WHERE pos=?", (i,))
            int_id = self.cur.fetchone()[0]
            syn_net = self.load_interval_net(i)
            compute_pageranks(syn_net)

            node = net_first_node(syn_net)
            while node != 0:
                nid = node_id(node)
                in_pr = node_pr_in(node)
                out_pr = node_pr_out(node)

                if in_pr < -7.0:
                    in_pr = -7.0
                if in_pr > 7.0:
                    in_pr = 7.0
                if out_pr < -7.0:
                    out_pr = -7.0
                if out_pr > 7.0:
                    out_pr = 7.0

                self.cur.execute("UPDATE node_metrics SET in_pr=?, out_pr=? WHERE node_id=? AND interval=?", (in_pr, out_pr, nid, int_id))

                node = node_next_node(node)

            destroy_net(syn_net)