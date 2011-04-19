#include <Python.h>
#include "edge.h"
#include "node.h"
#include "network.h"

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
    
    return NULL;
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
    
    return NULL;
}

static PyObject *pysyn_compute_evc(PyObject *self, PyObject *args)
{
    long p;
    syn_net *net;

    if (PyArg_ParseTuple(args, "i", &p)) {
      net = (syn_net *)p;
      syn_compute_evc(net);
    }
    
    return NULL;
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
    
    return NULL;
}

static PyMethodDef methods[] = {
    {"create_net", pysyn_create_net, METH_VARARGS, "Create network."},
    {"destroy_net", pysyn_destroy_net, METH_VARARGS, "Destroy network."},
    {"load_net", pysyn_load_net, METH_VARARGS, "Load network."},
    {"compute_evc", pysyn_compute_evc, METH_VARARGS, "Compute EVC."},
    {"write_evc", pysyn_write_evc, METH_VARARGS, "Write EVC."},
    {NULL, NULL, 0, NULL},
};

PyMODINIT_FUNC initsyn(void)
{
    Py_InitModule("syn", methods);
}
