import sqlite3


class Net:
    def __init__(self, dbpath):
        self.conn = sqlite3.connect(dbpath)

    def __del__(self):
        self.conn.close()

    def safe_execute(self, cur, query):
        cur = self.conn.cursor()
        try:
            cur.execute(query)
            print('Executed query: %s' % query)
        except sqlite3.OperationalError:
            cur.close()

        self.conn.commit()
        cur.close()

    def create_db(self, dbpath):
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
        self.safe_execute("ALTER TABLE node ADD COLUMN ts REAL")

    def load_net(self, min_ts=-1, max_ts=-1):
        cur = self.conn.cursor()
    
        net = create_net()

        cur.execute("SELECT id FROM node")
        
        nodes = {}
        for row in cur:
            nodes[row[0]] = add_node(net, 0)

        if min_ts >= 0:
            if max_ts >= 0:
                cur.execute("SELECT orig, targ FROM node WHERE ts>=%f AND ts<%f" % (min_ts, max_ts))
            else:
                cur.execute("SELECT orig, targ FROM node WHERE ts>=%f" % (min_ts))
        else:
            cur.execute("SELECT orig, targ, ts FROM node")

        for row in cur:
            add_edge_to_net(net, nodes[row[0]], nodes[row[1]], row[2])

        cur.close()

        return net
