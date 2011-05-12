#include <Python.h>
#include "edge.h"
#include "node.h"
#include "network.h"
#include "histogram2d.h"

syn_net *syn_create_net(void);
void syn_destroy_net(syn_net *net);

static PyObject *pysyn_create_net(PyObject *self, PyObject *args)
{
    syn_net *net = syn_create_net();
    PyObject *result = Py_BuildValue("i", (long)net);
    
    return result;
}

static PyObject *pysyn_destroy_net(PyObject *self, PyObject *args)
{
    long p;
    syn_net *net;

    if (PyArg_ParseTuple(args, "i", &p)) {
      net = (syn_net *)p;
      syn_destroy_net(net);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_load_net(PyObject *self, PyObject *args)
{
    long p;
    char *file_path;
    syn_net *net;

    if (PyArg_ParseTuple(args, "is", &p, &file_path)) {
      net = (syn_net *)p;
      syn_load_net(net, file_path);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_compute_evc(PyObject *self, PyObject *args)
{
    long p;
    syn_net *net;

    if (PyArg_ParseTuple(args, "i", &p)) {
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

    if (PyArg_ParseTuple(args, "is", &p, &file_path)) {
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

    if (PyArg_ParseTuple(args, "i", &p)) {
      net = (syn_net *)p;
      syn_print_net_info(net);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_write_gexf(PyObject *self, PyObject *args)
{
    long p;
    char *file_path;
    syn_net *net;

    if (PyArg_ParseTuple(args, "is", &p, &file_path)) {
      net = (syn_net *)p;
      syn_write_gexf(net, file_path);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_get_evc_histogram(PyObject *self, PyObject *args)
{
    long p;
    int bin_number;
    syn_net *net;
    syn_histogram2d *hist = NULL;

    if (PyArg_ParseTuple(args, "ii", &p, &bin_number)) {
      net = (syn_net *)p;
      hist = syn_get_evc_histogram(net, bin_number);
    }
    
    PyObject *result = Py_BuildValue("i", (long)hist);
    return result;
}

static PyObject *pysyn_histogram2d_print(PyObject *self, PyObject *args)
{
    long p;
    syn_histogram2d *hist = NULL;

    if (PyArg_ParseTuple(args, "i", &p)) {
      hist = (syn_histogram2d *)p;
      syn_histogram2d_print(hist);
    }
    
    PyObject *result = Py_BuildValue("");
    return result;
}

static PyObject *pysyn_histogram2d_bin_number(PyObject *self, PyObject *args)
{
    long p;
    syn_histogram2d *hist = NULL;

    if (PyArg_ParseTuple(args, "i", &p)) {
      hist = (syn_histogram2d *)p;
    }
    
    PyObject *result = Py_BuildValue("i", hist->bin_number);
    return result;
}

static PyObject *pysyn_histogram2d_get_value(PyObject *self, PyObject *args)
{
    long p;
    int x, y;
    syn_histogram2d *hist = NULL;
    double value = 0.0;

    if (PyArg_ParseTuple(args, "iii", &p, &x, &y)) {
      hist = (syn_histogram2d *)p;
      value = syn_histogram2d_get_value(hist, x, y);
    }
    
    PyObject *result = Py_BuildValue("f", value);
    return result;
}

static PyMethodDef methods[] = {
    {"create_net", pysyn_create_net, METH_VARARGS, "Create network."},
    {"destroy_net", pysyn_destroy_net, METH_VARARGS, "Destroy network."},
    {"load_net", pysyn_load_net, METH_VARARGS, "Load network."},
    {"compute_evc", pysyn_compute_evc, METH_VARARGS, "Compute EVC."},
    {"write_evc", pysyn_write_evc, METH_VARARGS, "Write EVC."},
    {"print_net_info", pysyn_print_net_info, METH_VARARGS, "Print net info."},
    {"write_gexf", pysyn_write_gexf, METH_VARARGS, "Write gexf file for net."},
    {"get_evc_histogram", pysyn_get_evc_histogram, METH_VARARGS, "Get EVC histogram from net."},
    {"histogram2d_print", pysyn_histogram2d_print, METH_VARARGS, "Print histogram."},
    {"histogram2d_bin_number", pysyn_histogram2d_bin_number, METH_VARARGS, "Return histogram bin number."},
    {"histogram2d_get_value", pysyn_histogram2d_get_value, METH_VARARGS, "Return histogram bin value."},
    {NULL, NULL, 0, NULL},
};

PyMODINIT_FUNC initcore(void)
{
    Py_InitModule("core", methods);
}
