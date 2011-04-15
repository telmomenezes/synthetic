#include <Python.h>
#include "edge.h"
#include "node.h"

static PyObject *MyCommand(PyObject *self, PyObject *args)
{
    PyObject *result = NULL;
    long a, b;

    if (PyArg_ParseTuple(args, "ii", &a, &b)) {
      result = Py_BuildValue("i", a + b);
    }
    
    return result;
}

static PyMethodDef methods[] = {
  {"xpto", MyCommand, METH_VARARGS, "TBD"},
  {NULL, NULL, 0, NULL},
};

PyMODINIT_FUNC initsyn(void)
{
    Py_InitModule("syn", methods);
}
