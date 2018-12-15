=========
Tutorials
=========


To get started with synthetic, we will show you how to create a network using a known generator and then
re-discovering the generator from that network. Of course, you can apply synthetic directly to a real network you wish
to analyse, in which case you would skip the initial steps.

Defining a generator
====================

A generator is a simple program in a Lisp-like language. We will define a simple preferential attachment generator,
where the probability of connection between two nodes is proportional to the in-degree of the target. Variables in the
synthetic generator language are identified by a ``$`` sign. The in-degree of the target is given by `$targ_in_deg`.

Using your favourite editor, simply create a text file called "pa.gen". To this text file, add the follwoing single
line::

   $targ_in_deg

Generating a network
====================

You can now use the ``gen`` command to generate a network according to the rules specified in the previous generator. ::

   synth gen --prg pa.gen --onet pa.txt --nodes 200 --edges 2000

A directed network with 200 nodes and 2000 edges is created. The ``--nodes`` and ``--edges`` parameters are optional.
Their respective default values are 1000 and 10000. Here we wanted to create a smaller network to make the following
step faster. An undirected network can also be generated, using the optional ``--undir``.

Evolving a generator from the network
=====================================

Now we can try to reverse-engineer the generator we previously defined. Create an ``evo`` directory to store the several
files produced by the search process and then run::

   synth evo --inet pa.txt --odir evo

Metrics for each generation of evolutionary search will be output to ``stdout``, and the process will stop after 1000
generations without improvement. You should be able to observe a gradual decrease in fitness, meaning that the generated
network is becoming more similar to the target. The best generator found so far will be stored at ``evo/bestprog.txt``.
Most of the times, by the time the process ends the best generator will be the original::

   $targ_in_deg

Of course, this is not guaranteed to happen. We are dealing with stochastic processes, after all. Some times a correct
solution will be found, but it will contain unnecessary code. For example::

   (== 7.0
     (max $orig_id 5.0) 0.49489811074561507 $targ_in_deg)

In this case, notice that the max value between $orig_id and 5 can never be equal to 7, so the expression will always
evaluate to $targ_in_deg. We refer to this type of unnecessary complexity as bloat. There is evolutionary pressure
against bloat, so if you monitor the contents of ``evo/bestprog.txt`` during the evolutionary process, you will
probably be able to observe bloated solutions forming and then being simplified.

With real networks leading to more complex networks, it becomes more likely that the process does not succeed in
completely getting rid of bloat. In this case, some manual simplification may be necessary.
