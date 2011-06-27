import sqlite3
from syn.core import *


class Net:
    def __init__(self, dbpath):
        self.conn = sqlite3.connect(dbpath)
        self.cur = self.conn.cursor()

    def __del__(self):
        self.conn.commit()
        self.cur.close()
        self.conn.close()

    def safe_execute(self, query):
        try:
            self.cur.execute(query)
            print('Executed query: %s' % query)
        except sqlite3.OperationalError:
            print('Failed query: %s' % query)

    def create_db(self):
        # create node table
        self.safe_execute("CREATE TABLE node (id INTEGER PRIMARY KEY)")
        self.safe_execute("ALTER TABLE node ADD COLUMN label TEXT")
        self.safe_execute("ALTER TABLE node ADD COLUMN super_node INTEGER")

        self.safe_execute("ALTER TABLE node ADD COLUMN ts_start REAL")
        self.safe_execute("ALTER TABLE node ADD COLUMN ts_end REAL")
        
        self.safe_execute("ALTER TABLE node ADD COLUMN in_degree INTEGER")
        self.safe_execute("ALTER TABLE node ADD COLUMN out_degree INTEGER")

        self.safe_execute("ALTER TABLE node ADD COLUMN in_evc REAL")
        self.safe_execute("ALTER TABLE node ADD COLUMN out_evc REAL")

        # create edge table
        self.safe_execute("CREATE TABLE edge (id INTEGER PRIMARY KEY)")
        self.safe_execute("ALTER TABLE edge ADD COLUMN orig INTEGER")
        self.safe_execute("ALTER TABLE edge ADD COLUMN targ INTEGER")
        self.safe_execute("ALTER TABLE edge ADD COLUMN ts REAL")

        # create indexes
        self.safe_execute("CREATE INDEX node_id ON node (id)")
        self.safe_execute("CREATE INDEX node_super_node ON node (super_node)")
        self.safe_execute("CREATE INDEX edge_id ON edge (id)")
        self.safe_execute("CREATE INDEX edge_orig ON edge (orig)")
        self.safe_execute("CREATE INDEX edge_orig_targ ON edge (orig, targ)")
        self.safe_execute("CREATE INDEX edge_ts ON edge (ts)")

    def load_net(self, min_ts=-1, max_ts=-1):
        net = create_net()

        self.cur.execute("SELECT id FROM node")
        
        nodes = {}
        for row in self.cur:
            nodes[row[0]] = add_node(net, 0)

        if min_ts >= 0:
            if max_ts >= 0:
                self.cur.execute("SELECT orig, targ FROM edge WHERE ts>=%f AND ts<%f" % (min_ts, max_ts))
            else:
                self.cur.execute("SELECT orig, targ FROM edge WHERE ts>=%f" % (min_ts))
        else:
            self.cur.execute("SELECT orig, targ, ts FROM edge")

        for row in cur:
            add_edge_to_net(net, nodes[row[0]], nodes[row[1]], row[2])

        cur.close()

        return net

    def add_node(self, super_node=-1, label=''):
        self.cur.execute("INSERT INTO node (super_node, label) VALUES (%d, '%s')" % (super_node, label))    
        return self.cur.lastrowid

    def add_edge(self, orig, targ, timestamp):
        self.cur.execute("INSERT INTO edge (orig, targ, ts) VALUES (%d, %d, %f)" % (orig, targ, timestamp))    
        return self.cur.lastrowid

    def min_edge_ts(self):
        self.cur.execute("SELECT min(ts) FROM edge WHERE ts > 0")
        row = self.cur.fetchone()
        return row[0]

    def max_edge_ts(self):
        self.cur.execute("SELECT max(ts) FROM edge WHERE ts > 0")
        row = self.cur.fetchone()
        return row[0]
