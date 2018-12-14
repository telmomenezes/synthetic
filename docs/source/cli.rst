======================
Command-line interface
======================

Synthetic provides a command-line interface that can be used to execute a variety of tasks.

Here's an overview of the interface::

   usage: synth [-h] [--inet INET] [--inet2 INET2] [--onet ONET] [--dir DIR]
             [--odir ODIR] [--prg PRG] [--prg2 PRG2] [--oprg OPRG] [--out OUT]
             [--gens GENS] [--bins BINS] [--runs RUNS] [--undir]
             [--tolerance TOLERANCE] [--nodes NODES] [--edges EDGES] [--mean]
             [--gentype GENTYPE]
             command

   positional arguments:
     command               command to execute

   optional arguments:
     -h, --help            show this help message and exit
     --inet INET           input net file
     --inet2 INET2         second input net file
     --onet ONET           output net file
     --dir DIR             directory
     --odir ODIR           output directory
     --prg PRG             generator program file
     --prg2 PRG2           second generator program file
     --oprg OPRG           generator output program file
     --out OUT             output file
     --gens GENS           number of generations
     --bins BINS           number of distribution bins
     --runs RUNS           number of generator runs
     --undir               undirected network
     --tolerance TOLERANCE
                        antibloat tolerance
     --nodes NODES         number of nodes
     --edges EDGES         number of edges
     --mean                compute mean
     --gentype GENTYPE     generator type

The only obligatory argument, command, is used to specify the task to perform. Each command uses a subset of the
optional arguments. Presented below are the details for each command.


compare
-------

Compares two networks. ::

   synth compare --inet <network1> --inet2 <network2>


Optional parameters: ``--undir``, ``--bins``

const
-----

Check if generator weight is constant (random network generator). ::

   synth const --prg <generator>

Optional parameters: ``--undir``, ``--sr``, ``--nodes``, ``--edges``

eval_distance
-------------

Computes matrix of behavioral distances between a set of generators. ::

   synth dists --inet <network> --dir <dir> --out <csv_file>

Optional parameters: ``--undir``, ``--sr``

evo
---

Evolve network generator. ::

   synth evo --inet <network> --odir <dir>

Optional parameters: ``--undir``, ``--gens``, ``--sr``, ``--bins``, ``--tolerance``

fit
---

Computes mean fitness for several runs of a generator. ::

   synth fit --inet <network> --prg <generator>

Optional parameters: ``--undir``, ``--sr``, ``--bins``, ``--runs``

gen
---

Generates network. ::

   synth gen --prg <generator> --onet <network>

Optional parameters: ``--undir``, ``--nodes``, ``--edges``, ``--sr``

prune
-----

Simplify generator program. ::

   synth prune --inet <network> --prg <in_generator> --oprg <out_generator>

Optional parameters: ``--undir``, ``--sr``

rand_gen
--------

Create a random generator program. ::

   synth randgen --oprg <out_generator>

Optional parameters: ``-undir``
