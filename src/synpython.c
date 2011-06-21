#include <Python.h>
#include "edge.h"
#include "node.h"
#include "network.h"
#include "drmap.h"
#include "mgenerator.h"


// NET API

static PyObject *pysyn_create_net(PyObject *self, PyObject *args)
{
    syn_net *net = syn_create_net();
    PyObject *result = Py_BuildValue("l", (long)net);
    
    return result;
}

static PyObject *pysyn_destroy_net(PyObject *self, PyObject *args)
{
    long p;
    syn_net *net;

    if (PyArg_ParseTuple(args, "l", &p)) {
        net = (syn_net *)p;
        syn_destroy_net(net);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_add_node(PyObject *self, PyObject *args)
{
    long p;
    int type;
    syn_net *net;
    syn_node *node = NULL;

    if (PyArg_ParseTuple(args, "li", &p, &type)) {
        net = (syn_net *)p;
        node = syn_add_node(net, type);
    }

    PyObject *result = Py_BuildValue("l", (long)node);
    return result;
}

static PyObject *pysyn_add_edge_to_net(PyObject *self, PyObject *args)
{
    long p1, p2, p3;
    unsigned long ts = 0;
    syn_net *net;
    syn_node *orig, *targ;
    int res = 0;

    if (PyArg_ParseTuple(args, "llll", &p1, &p2, &p3, &ts)) {
      net = (syn_net *)p1;
      orig = (syn_node *)p2;
      targ = (syn_node *)p3;
      res = syn_add_edge_to_net(net, orig, targ, ts);
    }

    PyObject *result = Py_BuildValue("i", res);

    return result;
}


static PyObject *pysyn_compute_evc(PyObject *self, PyObject *args)
{
    long p;
    syn_net *net;

    if (PyArg_ParseTuple(args, "l", &p)) {
      net = (syn_net *)p;
      syn_compute_evc(net);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_write_evc(PyObject *self, PyObject *args)
{
    long p;
    char *file_path;
    syn_net *net;

    if (PyArg_ParseTuple(args, "ls", &p, &file_path)) {
      net = (syn_net *)p;
      syn_write_evc(net, file_path);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_print_net_info(PyObject *self, PyObject *args)
{
    long p;
    syn_net *net;

    if (PyArg_ParseTuple(args, "l", &p)) {
      net = (syn_net *)p;
      syn_print_net_info(net);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_net_node_count(PyObject *self, PyObject *args)
{
    long p;
    syn_net *net;
    unsigned int node_count = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
      net = (syn_net *)p;
      node_count = net->node_count;
    }
    
    PyObject *result = Py_BuildValue("i", node_count);
    return result;
}

static PyObject *pysyn_net_edge_count(PyObject *self, PyObject *args)
{
    long p;
    syn_net *net;
    unsigned int edge_count = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
      net = (syn_net *)p;
      edge_count = net->edge_count;
    }
    
    PyObject *result = Py_BuildValue("i", edge_count);
    return result;
}

// DRMAP API

static PyObject *pysyn_destroy_drmap(PyObject *self, PyObject *args)
{
    long p;
    syn_drmap *map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (syn_drmap *)p;
        syn_drmap_destroy(map);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_get_drmap(PyObject *self, PyObject *args)
{
    long p;
    int bin_number;
    syn_net *net;
    syn_drmap *map = NULL;

    if (PyArg_ParseTuple(args, "li", &p, &bin_number)) {
        net = (syn_net *)p;
        map = syn_get_drmap(net, bin_number);
    }
    
    PyObject *result = Py_BuildValue("l", (long)map);
    return result;
}

static PyObject *pysyn_get_drmap_with_limits(PyObject *self, PyObject *args)
{
    long p;
    int bin_number;
    syn_net *net;
    syn_drmap *map = NULL;
    double min_val_hor;
    double max_val_hor;
    double min_val_ver;
    double max_val_ver;

    if (PyArg_ParseTuple(args, "lidddd", &p, &bin_number, &min_val_hor, &max_val_hor, &min_val_ver, &max_val_ver)) {
        net = (syn_net *)p;
        map = syn_get_drmap_with_limits(net, bin_number, min_val_hor, max_val_hor, min_val_ver, max_val_ver);
    }
    
    PyObject *result = Py_BuildValue("l", (long)map);
    return result;
}

static PyObject *pysyn_drmap_print(PyObject *self, PyObject *args)
{
    long p;
    syn_drmap *map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (syn_drmap *)p;
        syn_drmap_print(map);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_drmap_bin_number(PyObject *self, PyObject *args)
{
    long p;
    syn_drmap *hist = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        hist = (syn_drmap *)p;
    }
    
    PyObject *result = Py_BuildValue("i", hist->bin_number);
    return result;
}

static PyObject *pysyn_drmap_get_value(PyObject *self, PyObject *args)
{
    long p;
    int x, y;
    syn_drmap *hist = NULL;
    double value = 0.0;

    if (PyArg_ParseTuple(args, "lii", &p, &x, &y)) {
      hist = (syn_drmap *)p;
      value = syn_drmap_get_value(hist, x, y);
    }
    
    PyObject *result = Py_BuildValue("f", value);
    return result;
}

static PyObject *pysyn_drmap_get_limits(PyObject *self, PyObject *args)
{
    long p;
    syn_drmap *map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (syn_drmap *)p;
    }
    
    PyObject *result = Py_BuildValue("(dddd)", map->min_val_hor, map->max_val_hor, map->min_val_ver, map->max_val_ver);
    return result;
}

static PyObject *pysyn_drmap_log_scale(PyObject *self, PyObject *args)
{
    long p;
    syn_drmap *map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (syn_drmap *)p;
        syn_drmap_log_scale(map);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_drmap_normalize(PyObject *self, PyObject *args)
{
    long p;
    syn_drmap *map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (syn_drmap *)p;
        syn_drmap_normalize(map);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

// GENERATOR API

static PyObject *pysyn_create_generator(PyObject *self, PyObject *args)
{
    unsigned int types_count;
    syn_gen *gen = NULL;

    if (PyArg_ParseTuple(args, "i", &types_count)) {
        gen = syn_create_generator(types_count);
    }

    PyObject *result = Py_BuildValue("l", (long)gen);
    return result;
}

static PyObject *pysyn_destroy_generator(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;

    if (PyArg_ParseTuple(args, "l", &p)) {
      gen = (syn_gen *)p;
      syn_destroy_generator(gen);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_generate_network(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    syn_net *net = NULL;
    unsigned int node_count, edge_count, max_cycles, max_walk_length;

    if (PyArg_ParseTuple(args, "liiii", &p, &node_count, &edge_count, &max_cycles, &max_walk_length)) {
      gen = (syn_gen *)p;
      net = syn_generate_network(gen, node_count, edge_count, max_cycles, max_walk_length);
    }
    
    PyObject *result = Py_BuildValue("l", (long)net);
    return result;
}


static PyObject *pysyn_generator_set_link(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int x, y;
    double val;

    if (PyArg_ParseTuple(args, "liid", &p, &x, &y, &val)) {
      gen = (syn_gen *)p;
      gen->m_link[(y * gen->types_count) + x] = val;
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}


static PyObject *pysyn_generator_set_random(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int x, y;
    double val;

    if (PyArg_ParseTuple(args, "liid", &p, &x, &y, &val)) {
      gen = (syn_gen *)p;
      gen->m_random[(y * gen->types_count) + x] = val;
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}


static PyObject *pysyn_generator_set_follow(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int x, y;
    double val;

    if (PyArg_ParseTuple(args, "liid", &p, &x, &y, &val)) {
      gen = (syn_gen *)p;
      gen->m_follow[(y * gen->types_count) + x] = val;
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}


static PyObject *pysyn_generator_set_rfollow(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int x, y;
    double val;

    if (PyArg_ParseTuple(args, "liid", &p, &x, &y, &val)) {
      gen = (syn_gen *)p;
      gen->m_rfollow[(y * gen->types_count) + x] = val;
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}


static PyObject *pysyn_generator_set_weight(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int pos;
    double val;

    if (PyArg_ParseTuple(args, "lid", &p, &pos, &val)) {
      gen = (syn_gen *)p;
      gen->m_weight[pos] = val;
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}


static PyObject *pysyn_generator_set_stop(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int pos;
    double val;

    if (PyArg_ParseTuple(args, "lid", &p, &pos, &val)) {
      gen = (syn_gen *)p;
      gen->m_stop[pos] = val;
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}


static PyObject *pysyn_generator_get_r_edges(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int r_edges = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        gen = (syn_gen *)p;
        r_edges = gen->r_edges;
    }
    
    PyObject *result = Py_BuildValue("i", r_edges);
    return result;
}


static PyObject *pysyn_generator_get_l_edges(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int l_edges = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        gen = (syn_gen *)p;
        l_edges = gen->l_edges;
    }
    
    PyObject *result = Py_BuildValue("i", l_edges);
    return result;
}


static PyObject *pysyn_generator_get_total_edges(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int total_edges = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        gen = (syn_gen *)p;
        total_edges = gen->total_edges;
    }
    
    PyObject *result = Py_BuildValue("i", total_edges);
    return result;
}


static PyObject *pysyn_generator_get_cycles(PyObject *self, PyObject *args)
{
    long p;
    syn_gen *gen;
    unsigned int cycles = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        gen = (syn_gen *)p;
        cycles = gen->cycles;
    }
    
    PyObject *result = Py_BuildValue("i", cycles);
    return result;
}


static PyMethodDef methods[] = {
    {"create_net", pysyn_create_net, METH_VARARGS, "Create network."},
    {"destroy_net", pysyn_destroy_net, METH_VARARGS, "Destroy network."},
    {"add_node", pysyn_add_node, METH_VARARGS, "Add node to network."},
    {"add_edge_to_net", pysyn_add_edge_to_net, METH_VARARGS, "Add edge to network."},
    {"compute_evc", pysyn_compute_evc, METH_VARARGS, "Compute EVC."},
    {"write_evc", pysyn_write_evc, METH_VARARGS, "Write EVC."},
    {"print_net_info", pysyn_print_net_info, METH_VARARGS, "Print net info."},
    {"net_node_count", pysyn_net_node_count, METH_VARARGS, "Net node count."},
    {"net_edge_count", pysyn_net_node_count, METH_VARARGS, "Net edge count."},
    {"destroy_drmap", pysyn_destroy_drmap, METH_VARARGS, "Destroy DRMap."},
    {"get_drmap", pysyn_get_drmap, METH_VARARGS, "Get DRMap from net."},
    {"get_drmap_with_limits", pysyn_get_drmap_with_limits, METH_VARARGS, "Get DRMap from net with limits."},
    {"drmap_print", pysyn_drmap_print, METH_VARARGS, "Print DRMap."},
    {"drmap_bin_number", pysyn_drmap_bin_number, METH_VARARGS, "Return DRmap bin number."},
    {"drmap_get_value", pysyn_drmap_get_value, METH_VARARGS, "Return DRMap bin value."},
    {"drmap_get_limits", pysyn_drmap_get_limits, METH_VARARGS, "Return DRMap limit values."},
    {"drmap_log_scale", pysyn_drmap_log_scale, METH_VARARGS, "Apply log scale to drmap bin values."},
    {"drmap_normalize", pysyn_drmap_normalize, METH_VARARGS, "Normalize drmap bin values by max value."},
    {"create_generator", pysyn_create_generator, METH_VARARGS, "Create generator."},
    {"destroy_generator", pysyn_destroy_generator, METH_VARARGS, "Destroy generator."},
    {"generate_network", pysyn_generate_network, METH_VARARGS, "Generate network."},
    {"generator_set_link", pysyn_generator_set_link, METH_VARARGS, "Set value at a position of the generator link matrix."},
    {"generator_set_random", pysyn_generator_set_random, METH_VARARGS, "Set value at a position of the generator random matrix."},
    {"generator_set_follow", pysyn_generator_set_follow, METH_VARARGS, "Set value at a position of the generator follow matrix."},
    {"generator_set_rfollow", pysyn_generator_set_rfollow, METH_VARARGS, "Set value at a position of the generator rfollow matrix."},
    {"generator_set_weight", pysyn_generator_set_weight, METH_VARARGS, "Set value at a position of the generator weight matrix."},
    {"generator_set_stop", pysyn_generator_set_stop, METH_VARARGS, "Set value at a position of the generator stop matrix."},
    {"generator_get_r_edges", pysyn_generator_get_r_edges, METH_VARARGS, "Get number of random edges generated."},
    {"generator_get_l_edges", pysyn_generator_get_l_edges, METH_VARARGS, "Get number of random walk edges generated."},
    {"generator_get_total_edges", pysyn_generator_get_total_edges, METH_VARARGS, "Get total number edges generated."},
    {"generator_get_cycles", pysyn_generator_get_cycles, METH_VARARGS, "Get number of cycles taken by the simulation."},
    {NULL, NULL, 0, NULL},
};


PyMODINIT_FUNC initcore(void)
{
    Py_InitModule("core", methods);
}

