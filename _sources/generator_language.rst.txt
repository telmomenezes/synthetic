Generator language
==================

Synthetic generators are expressed in a very simple Lisp-like language. In short, it uses S-Expressions
(http://en.wikipedia.org/wiki/S-expression) and prefix notation. So, instead of writing ``1 + 1``, you write
``(+ 1 1)``. Of course, these expression can be nested into things like::

   (> $orig_out_deg 8.0
     (aff $targ_in_deg 0.0 5.0) $targ_in_deg)

All values are treated as floating point.

There are 3 types of atomic expressions:

* Variables
* Functions
* Constant values

Variables
---------

Variables are pre-defined and all used to convey local information about origin and target nodes, during the generative
process. They are:

* ``$orig_id``: Sequential id of the origin node
* ``$targ_id``: Sequential id of the target node
* ``$orig_in_deg``: In-degree of the origin node (directed networks only)
* ``$orig_out_deg``: Out-degree of the origin node (directed networks only)
* ``$targ_in_deg``: In-degree of the target node (directed networks only)
* ``$targ_out_deg``: Out-degree of the target node (directed networks only)
* ``$orig_deg``: Degree of the origin node (undirected networks only)
* ``$targ_deg``: Degree of the target node (undirected networks only)
* ``$dist``: Simple random-walk distance between nodes, ignores edge direction
* ``$dir_dist``: Direct random-walk distance between nodes (directed networks only)
* ``$rev_dist``: Reverse random-walk distance between nodes, transverses edges in the reverse direction (directed networks only)

Functions
---------

* Basic arithmetic functions: ``+``, ``-``, ``*``, ``/``, ``exp``, ``log``, ``abs``, ``^``
* Comparison: ``==``, ``>``, ``<``, ``zer``
* Affinity: ``aff``

Examples
--------

::

   (< $targ_in_deg 3 $targ_out_deg $targ_id)

If target in-degree is less than 3 then return the target out-degree, else return the target id.

The ``<`` and ``==`` functions have similar semantics. They apply the comparator to the first and second parameters and
then return the third parameter if the comparison is true, or the fourth parameter otherwise. The `ZER` comparator
function has one less parameter and works like this::

   (zer $targ_in_deg $targ_out_deg $targ_id)

If target in-degree is zero then return the target out-degree, else return the target id.

Here's a more nested example, with comparators::

   (< $targInDeg
     (+ $targOutDeg $revDist)
     $targOutDeg $targId)

And so on.
