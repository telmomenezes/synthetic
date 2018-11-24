import igraph


def load_net(file_path, directed):
    net = igraph.Graph.Load(file_path)

    # force directed / undirected
    if net.is_directed() and not directed:
        net = net.as_undirected()
    if not net.is_directed() and directed:
        net = net.as_directed()

    net = net.simplify()

    assert (net.is_directed() == directed)

    return net
