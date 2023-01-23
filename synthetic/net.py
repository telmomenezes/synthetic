import igraph

from synthetic.randomwalkers import RandomWalkers


def create_net(nodes, directed):
    graph = igraph.Graph(n=nodes, directed=directed)
    return Net(graph)


def create_random_net(nodes, edges, directed):
    graph = igraph.Graph.Erdos_Renyi(n=nodes, m=edges, directed=directed)
    return Net(graph)


def load_net(file_path, directed):
    graph = igraph.Graph.Load(file_path)

    # force directed / undirected
    if graph.is_directed() and not directed:
        graph = graph.as_undirected()
    if not graph.is_directed() and directed:
        graph = graph.as_directed()

    graph = graph.simplify()

    assert (graph.is_directed() == directed)

    return Net(graph)


class Net:
    def __init__(self, graph):
        self.graph = graph

        self.u_random_walkers = RandomWalkers(self, False)
        if graph.is_directed():
            self.d_random_walkers = RandomWalkers(self, True)
        else:
            self.d_random_walkers = None

    def degree(self, node):
        return self.graph.degree(node, mode=igraph.ALL)

    def in_degree(self, node):
        return self.graph.degree(node, mode=igraph.IN)

    def out_degree(self, node):
        return self.graph.degree(node, mode=igraph.OUT)

    def neighbors(self, node):
        return self.graph.neighbors(node, mode='all')

    def in_neighbors(self, node):
        return self.graph.neighbors(node, mode='in')

    def out_neighbors(self, node):
        return self.graph.neighbors(node, mode='out')
