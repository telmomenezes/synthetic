from igraph import Graph


def full_graph(directed):
    return Graph.Full(100, directed=directed)


def star_graph():
    return Graph.Star(100, 'undirected')


def in_star_graph():
    return Graph.Star(100, 'in')


def out_star_graph():
    return Graph.Star(100, 'out')


def ring_graph(directed):
    return Graph.Ring(100, directed=directed)


def random_graph_sparse(directed):
    # noinspection PyArgumentList
    return Graph.Erdos_Renyi(n=100, m=100, directed=directed)


def random_graph_medium(directed):
    # noinspection PyArgumentList
    return Graph.Erdos_Renyi(n=100, m=1000, directed=directed)


def random_graph_dense(directed):
    # noinspection PyArgumentList
    return Graph.Erdos_Renyi(n=100, m=3000, directed=directed)
