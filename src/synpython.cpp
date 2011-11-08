#include <Python.h>
#include "edge.h"
#include "node.h"
#include "network.h"
#include "drmap.h"
#include "gpgenerator.h"

using syn::Net;
using syn::DRMap;

// NET API

static PyObject *pysyn_create_net(PyObject *self, PyObject *args)
{
    Net* net = new Net();
    PyObject *result = Py_BuildValue("l", (long)net);
    
    return result;
}

static PyObject *pysyn_destroy_net(PyObject *self, PyObject *args)
{
    long p;
    Net* net;

    if (PyArg_ParseTuple(args, "l", &p)) {
        net = (Net*)p;
        delete net;
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_add_node(PyObject *self, PyObject *args)
{
    long p;
    int type;
    Net* net;
    syn_node* node = NULL;

    if (PyArg_ParseTuple(args, "li", &p, &type)) {
        net = (Net*)p;
        node = net->add_node(type);
    }

    PyObject *result = Py_BuildValue("l", (long)node);
    return result;
}

static PyObject *pysyn_add_node_with_id(PyObject *self, PyObject *args)
{
    long p;
    unsigned int nid;
    int type;
    Net* net;
    syn_node* node = NULL;

    if (PyArg_ParseTuple(args, "lii", &p, &nid, &type)) {
        net = (Net*)p;
        node = net->add_node_with_id(nid, type);
    }

    PyObject *result = Py_BuildValue("l", (long)node);
    return result;
}

static PyObject *pysyn_add_edge_to_net(PyObject *self, PyObject *args)
{
    long p1, p2, p3;
    unsigned long ts = 0;
    Net* net;
    syn_node* orig;
    syn_node* targ;
    int res = 0;

    if (PyArg_ParseTuple(args, "llll", &p1, &p2, &p3, &ts)) {
      net = (Net*)p1;
      orig = (syn_node *)p2;
      targ = (syn_node *)p3;
      res = net->add_edge_to_net(orig, targ, ts);
    }

    PyObject *result = Py_BuildValue("i", res);

    return result;
}


static PyObject *pysyn_compute_pageranks(PyObject *self, PyObject *args)
{
    long p;
    Net* net;

    if (PyArg_ParseTuple(args, "l", &p)) {
      net = (Net*)p;
      net->compute_pageranks();
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_write_pageranks(PyObject *self, PyObject *args)
{
    long p;
    char* file_path;
    Net* net;

    if (PyArg_ParseTuple(args, "ls", &p, &file_path)) {
      net = (Net*)p;
      net->write_pageranks(file_path);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_print_net_info(PyObject *self, PyObject *args)
{
    long p;
    Net* net;

    if (PyArg_ParseTuple(args, "l", &p)) {
      net = (Net*)p;
      net->print_net_info();
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_net_node_count(PyObject *self, PyObject *args)
{
    long p;
    Net* net;
    unsigned int node_count = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
      net = (Net*)p;
      node_count = net->get_node_count();
    }
    
    PyObject *result = Py_BuildValue("i", node_count);
    return result;
}

static PyObject *pysyn_net_edge_count(PyObject *self, PyObject *args)
{
    long p;
    Net* net;
    unsigned int edge_count = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
      net = (Net*)p;
      edge_count = net->get_edge_count();
    }
    
    PyObject *result = Py_BuildValue("i", edge_count);
    return result;
}

static PyObject *pysyn_net_temporal(PyObject *self, PyObject *args)
{
    long p;
    Net* net;
    unsigned int temporal = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        net = (Net*)p;
        temporal = net->get_temporal();
    }
    
    PyObject *result = Py_BuildValue("i", temporal);
    return result;
}

static PyObject *pysyn_net_min_ts(PyObject *self, PyObject *args)
{
    long p;
    Net* net;
    unsigned int min_ts = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        net = (Net*)p;
        min_ts = net->get_min_ts();
    }
    
    PyObject *result = Py_BuildValue("i", min_ts);
    return result;
}

static PyObject *pysyn_net_max_ts(PyObject *self, PyObject *args)
{
    long p;
    Net* net;
    unsigned int max_ts = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        net = (Net*)p;
        max_ts = net->get_max_ts();
    }
    
    PyObject *result = Py_BuildValue("i", max_ts);
    return result;
}

static PyObject *pysyn_net_first_node(PyObject *self, PyObject *args)
{
    long p;
    Net* net;
    syn_node* node = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        net = (Net*)p;
        node = net->get_nodes();
    }

    PyObject *result = Py_BuildValue("l", (long)node);
    return result;
}


// NODE API

static PyObject *pysyn_node_next_node(PyObject *self, PyObject *args)
{
    long p;
    syn_node *node;
    syn_node *next_node = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        node = (syn_node *)p;
        next_node = node->next;
    }

    PyObject *result = Py_BuildValue("l", (long)next_node);
    return result;
}

static PyObject *pysyn_node_id(PyObject *self, PyObject *args)
{
    long p;
    syn_node *node;
    unsigned int value = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        node = (syn_node *)p;
        value = node->id;
    }

    PyObject *result = Py_BuildValue("i", value);
    return result;
}

static PyObject *pysyn_node_in_degree(PyObject *self, PyObject *args)
{
    long p;
    syn_node *node;
    unsigned int value = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        node = (syn_node *)p;
        value = node->in_degree;
    }

    PyObject *result = Py_BuildValue("i", value);
    return result;
}

static PyObject *pysyn_node_out_degree(PyObject *self, PyObject *args)
{
    long p;
    syn_node *node;
    unsigned int value = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        node = (syn_node *)p;
        value = node->out_degree;
    }

    PyObject *result = Py_BuildValue("i", value);
    return result;
}

static PyObject *pysyn_node_pr_in(PyObject *self, PyObject *args)
{
    long p;
    syn_node *node;
    double value = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        node = (syn_node *)p;
        value = node->pr_in;
    }

    PyObject *result = Py_BuildValue("f", value);
    return result;
}

static PyObject *pysyn_node_pr_out(PyObject *self, PyObject *args)
{
    long p;
    syn_node *node;
    double value = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        node = (syn_node *)p;
        value = node->pr_out;
    }

    PyObject *result = Py_BuildValue("f", value);
    return result;
}


// DRMAP API

static PyObject *pysyn_create_drmap(PyObject *self, PyObject *args)
{
    unsigned int bin_number;
    float min_val_hor, max_val_hor, min_val_ver, max_val_ver;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "iffff", &bin_number, &min_val_hor, &max_val_hor, &min_val_ver, &max_val_ver)) {
        map = new DRMap(bin_number, min_val_hor, max_val_hor, min_val_ver, max_val_ver);
    }

    PyObject *result = Py_BuildValue("l", (long)map);
    return result;
}

static PyObject *pysyn_destroy_drmap(PyObject *self, PyObject *args)
{
    long p;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (DRMap*)p;
        delete map;
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_get_drmap(PyObject *self, PyObject *args)
{
    long p;
    int bin_number;
    Net* net;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "li", &p, &bin_number)) {
        net = (Net*)p;
        map = net->get_drmap(bin_number);
    }
    
    PyObject *result = Py_BuildValue("l", (long)map);
    return result;
}

static PyObject *pysyn_get_drmap_with_limits(PyObject *self, PyObject *args)
{
    long p;
    int bin_number;
    Net* net;
    DRMap* map = NULL;
    double min_val_hor;
    double max_val_hor;
    double min_val_ver;
    double max_val_ver;

    if (PyArg_ParseTuple(args, "lidddd", &p, &bin_number, &min_val_hor, &max_val_hor, &min_val_ver, &max_val_ver)) {
        net = (Net*)p;
        map = net->get_drmap_with_limits(bin_number, min_val_hor, max_val_hor, min_val_ver, max_val_ver);
    }
    
    PyObject *result = Py_BuildValue("l", (long)map);
    return result;
}

static PyObject *pysyn_drmap_print(PyObject *self, PyObject *args)
{
    long p;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (DRMap*)p;
        map->print();
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_drmap_bin_number(PyObject *self, PyObject *args)
{
    long p;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (DRMap*)p;
    }
    
    PyObject *result = Py_BuildValue("i", map->get_bin_number());
    return result;
}

static PyObject *pysyn_drmap_get_value(PyObject *self, PyObject *args)
{
    long p;
    int x, y;
    DRMap* map = NULL;
    double value = 0.0;

    if (PyArg_ParseTuple(args, "lii", &p, &x, &y)) {
      map = (DRMap*)p;
      value = map->get_value(x, y);
    }
    
    PyObject *result = Py_BuildValue("f", value);
    return result;
}

static PyObject *pysyn_drmap_set_value(PyObject *self, PyObject *args)
{
    long p;
    int x, y;
    DRMap* map = NULL;
    double val;

    if (PyArg_ParseTuple(args, "liid", &p, &x, &y, &val)) {
      map = (DRMap*)p;
      map->set_value(x, y, val);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_drmap_get_limits(PyObject *self, PyObject *args)
{
    long p;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (DRMap*)p;
    }
    
    PyObject *result = Py_BuildValue("(dddd)", map->get_min_val_hor(), map->get_max_val_hor(),
                                        map->get_min_val_ver(), map->get_max_val_ver());
    return result;
}

static PyObject *pysyn_drmap_log_scale(PyObject *self, PyObject *args)
{
    long p;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (DRMap*)p;
        map->log_scale();
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_drmap_normalize(PyObject *self, PyObject *args)
{
    long p;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (DRMap*)p;
        map->normalize();
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_drmap_binary(PyObject *self, PyObject *args)
{
    long p;
    DRMap* map = NULL;

    if (PyArg_ParseTuple(args, "l", &p)) {
        map = (DRMap*)p;
        map->binary();
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_drmap_emd_distance(PyObject *self, PyObject *args)
{
    long p1, p2;
    DRMap* map1 = NULL;
    DRMap* map2 = NULL;
    double value = 0.0;

    if (PyArg_ParseTuple(args, "ll", &p1, &p2)) {
        map1 = (DRMap*)p1;
        map2 = (DRMap*)p2;
        value = map1->emd_dist(map2);
    }

    PyObject *result = Py_BuildValue("d", value);
    return result;
}

static PyObject *pysyn_drmap_simple_distance(PyObject *self, PyObject *args)
{
    long p1, p2;
    DRMap* map1 = NULL;
    DRMap* map2 = NULL;
    double value = 0.0;

    if (PyArg_ParseTuple(args, "ll", &p1, &p2)) {
        map1 = (DRMap*)p1;
        map2 = (DRMap*)p2;
        value = map1->simple_dist(map2);
    }

    PyObject *result = Py_BuildValue("d", value);
    return result;
}


// GPGENERATOR API

static PyObject *pysyn_create_gpgenerator(PyObject *self, PyObject *args)
{
    syn_gpgen *gen = syn_create_gpgenerator();
    PyObject *result = Py_BuildValue("l", (long)gen);
    return result;
}

static PyObject *pysyn_destroy_gpgenerator(PyObject *self, PyObject *args)
{
    long p;
    syn_gpgen *gen;

    if (PyArg_ParseTuple(args, "l", &p)) {
      gen = (syn_gpgen *)p;
      syn_destroy_gpgenerator(gen);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_clone_gpgenerator(PyObject *self, PyObject *args)
{
    long p;
    syn_gpgen *gen;
    syn_gpgen *cgen;

    if (PyArg_ParseTuple(args, "l", &p)) {
      gen = (syn_gpgen*)p;
      cgen = syn_clone_gpgenerator(gen);
    }

    PyObject *result = Py_BuildValue("l", (long)cgen);
    return result;
}

static PyObject *pysyn_gpgen_run(PyObject *self, PyObject *args)
{
    long p;
    syn_gpgen *gen;
    Net* net = NULL;
    unsigned int nodes, edges, max_cycles;

    if (PyArg_ParseTuple(args, "liii", &p, &nodes, &edges, &max_cycles)) {
      gen = (syn_gpgen*)p;
      net = syn_gpgen_run(gen, nodes, edges, max_cycles);
    }
    
    PyObject *result = Py_BuildValue("l", (long)net);
    return result;
}

static PyObject *pysyn_gpgenerator_get_edges(PyObject *self, PyObject *args)
{
    long p;
    syn_gpgen *gen;
    unsigned int edges = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        gen = (syn_gpgen*)p;
        edges = gen->edges;
    }
    
    PyObject *result = Py_BuildValue("i", edges);
    return result;
}

static PyObject *pysyn_gpgenerator_get_cycles(PyObject *self, PyObject *args)
{
    long p;
    syn_gpgen* gen;
    unsigned int cycles = 0;

    if (PyArg_ParseTuple(args, "l", &p)) {
        gen = (syn_gpgen*)p;
        cycles = gen->cycle;
    }
    
    PyObject *result = Py_BuildValue("i", cycles);
    return result;
}

static PyObject *pysyn_print_gpgen(PyObject *self, PyObject *args)
{
    long p;
    syn_gpgen* gen;

    if (PyArg_ParseTuple(args, "l", &p)) {
        gen = (syn_gpgen*)p;
        syn_print_gpgen(gen);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_recombine_gpgens(PyObject *self, PyObject *args)
{
    long p1;
    long p2;
    syn_gpgen* g1;
    syn_gpgen* g2;
    syn_gpgen* gen;

    if (PyArg_ParseTuple(args, "ll", &p1, &p2)) {
        g1 = (syn_gpgen*)p1;
        g2 = (syn_gpgen*)p2;
        gen = syn_recombine_gpgens(g1, g2);
    }
    
    PyObject *result = Py_BuildValue("l", (long)gen);
    return result;
}


// AUX
static PyObject *pysyn_seed_random(PyObject *self, PyObject *args)
{
    srandom(time(NULL));
    
    PyObject *result = Py_BuildValue("");
    return result;
}


static PyMethodDef methods[] = {
    {"create_net", pysyn_create_net, METH_VARARGS, "Create network."},
    {"destroy_net", pysyn_destroy_net, METH_VARARGS, "Destroy network."},
    {"add_node", pysyn_add_node, METH_VARARGS, "Add node to network."},
    {"add_node_with_id", pysyn_add_node_with_id, METH_VARARGS, "Add node to network, specify id."},
    {"add_edge_to_net", pysyn_add_edge_to_net, METH_VARARGS, "Add edge to network."},
    {"compute_pageranks", pysyn_compute_pageranks, METH_VARARGS, "Compute pageranks."},
    {"write_pageranks", pysyn_write_pageranks, METH_VARARGS, "Write pageranks."},
    {"print_net_info", pysyn_print_net_info, METH_VARARGS, "Print net info."},
    {"net_node_count", pysyn_net_node_count, METH_VARARGS, "Net node count."},
    {"net_edge_count", pysyn_net_edge_count, METH_VARARGS, "Net edge count."},
    {"net_temporal", pysyn_net_temporal, METH_VARARGS, "Does the net contain temporal information?"},
    {"net_min_ts", pysyn_net_min_ts, METH_VARARGS, "Net min timestamp."},
    {"net_max_ts", pysyn_net_max_ts, METH_VARARGS, "Net max timestamp."},
    {"net_first_node", pysyn_net_first_node, METH_VARARGS, "First node in net."},
    {"node_next_node", pysyn_node_next_node, METH_VARARGS, "Get next node from a node."},
    {"node_id", pysyn_node_id, METH_VARARGS, "Get node id."},
    {"node_in_degree", pysyn_node_in_degree, METH_VARARGS, "Get node in degree."},
    {"node_out_degree", pysyn_node_out_degree, METH_VARARGS, "Get node out degree."},
    {"node_pr_in", pysyn_node_pr_in, METH_VARARGS, "Get node pr in."},
    {"node_pr_out", pysyn_node_pr_out, METH_VARARGS, "Get node pr out."},
    {"create_drmap", pysyn_create_drmap, METH_VARARGS, "Create DRMap."},
    {"destroy_drmap", pysyn_destroy_drmap, METH_VARARGS, "Destroy DRMap."},
    {"get_drmap", pysyn_get_drmap, METH_VARARGS, "Get DRMap from net."},
    {"get_drmap_with_limits", pysyn_get_drmap_with_limits, METH_VARARGS, "Get DRMap from net with limits."},
    {"drmap_print", pysyn_drmap_print, METH_VARARGS, "Print DRMap."},
    {"drmap_bin_number", pysyn_drmap_bin_number, METH_VARARGS, "Return DRmap bin number."},
    {"drmap_get_value", pysyn_drmap_get_value, METH_VARARGS, "Return DRMap bin value."},
    {"drmap_set_value", pysyn_drmap_set_value, METH_VARARGS, "Set DRMap bin value."},
    {"drmap_get_limits", pysyn_drmap_get_limits, METH_VARARGS, "Return DRMap limit values."},
    {"drmap_log_scale", pysyn_drmap_log_scale, METH_VARARGS, "Apply log scale to drmap bin values."},
    {"drmap_normalize", pysyn_drmap_normalize, METH_VARARGS, "Normalize drmap bin values by max value."},
    {"drmap_binary", pysyn_drmap_binary, METH_VARARGS, "Make drmap binary (0 or 1)."},
    {"drmap_simple_dist", pysyn_drmap_simple_distance, METH_VARARGS, "DRMap simple distance."},
    {"drmap_emd_dist", pysyn_drmap_emd_distance, METH_VARARGS, "DRMap earth mover's distance."},
    {"create_gpgenerator", pysyn_create_gpgenerator, METH_VARARGS, "Create gpgenerator."},
    {"destroy_gpgenerator", pysyn_destroy_gpgenerator, METH_VARARGS, "Destroy gpgenerator."},
    {"clone_gpgenerator", pysyn_clone_gpgenerator, METH_VARARGS, "Clone gpgenerator."},
    {"gpgen_run", pysyn_gpgen_run, METH_VARARGS, "Generate network with gp generator."},
    {"gpgenerator_get__edges", pysyn_gpgenerator_get_edges, METH_VARARGS, "Get total number edges generated."},
    {"gpgenerator_get_cycles", pysyn_gpgenerator_get_cycles, METH_VARARGS, "Get number of cycles taken by the simulation."},
    {"print_gpgen", pysyn_print_gpgen, METH_VARARGS, "Print gpgenerator."},
    {"recombine_gpgens", pysyn_recombine_gpgens, METH_VARARGS, "Recombine gpgenerators."},
    {"seed_random", pysyn_seed_random, METH_VARARGS, "Sedd C random number generator with current time."},
    {NULL, NULL, 0, NULL},
};


PyMODINIT_FUNC initcore(void)
{
    Py_InitModule("core", methods);
}

