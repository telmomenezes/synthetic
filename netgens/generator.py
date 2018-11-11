import random
import numpy as np
from igraph import *
import netgens.progs.prog as prog


class Generator(object):
    def __init__(self, net_params, sr):
        self.net_params = net_params
        self.sr = sr

        self.trials = int(sr * net_params['nodes'] * net_params['edges'])
        if self.trials < 2:
            self.trials = 2

        self.sample_origs = np.zeros(self.trials, dtype=np.uint64)
        self.sample_targs = np.zeros(self.trials, dtype=np.uint64)
        self.sample_weights = np.zeros(self.trials)

        self.valid = True

        self.fitness_avg = 0.
        self.fitness_max = 0.

        self.var_names = None  # must be set by derived class

        self.prog = None
        self.net = None

        self.iterations = -1

        self.last_weight = 0.
        self.constant = False

    def instance(self):
        # TODO!!
        return self

    def clone(self):
        pass

    def set_prog_vars(self, orig_index, targ_index):
        pass

    def load(self, file_path):
        self.prog = prog.load(self.var_names, file_path)

    def get_random_node_index(self):
        return random.randrange(self.net_params['nodes'])

    def generate_sample(self):
        for i in range(self.trials):
            orig_index = -1
            targ_index = -1
            found = False

            while not found:
                orig_index = self.get_random_node_index()
                targ_index = self.get_random_node_index()

                if orig_index != targ_index:
                    if self.net[orig_index, targ_index] == 0:
                        found = True

            self.sample_origs[i] = orig_index
            self.sample_targs[i] = targ_index

    def cycle(self, master=None):
        if master is None:
            self.generate_sample()
        else:
            self.sample_origs = master.sample_origs
            self.sample_targs = master.sample_targs

        total_weight = 0.
        for i in range(self.trials):
            self.set_prog_vars(self.sample_origs[i], self.sample_targs[i])

            weight = self.prog.eval()
            if weight < 0.:
                weight = 0.

            if math.isnan(weight):
                weight = 0.
                self.valid = False

            self.sample_weights[i] = weight
            total_weight += weight

            if self.constant:
                if self.last_weight < 0:
                    self.last_weight = weight
                else:
                    if self.last_weight != weight:
                        self.constant = False

        if total_weight == 0.:
            for i in range(self.trials):
                self.sample_weights[i] = 1
                total_weight += 1

        targ_weight = random.random() * total_weight
        i = 0
        total_weight = self.sample_weights[i]
        while targ_weight > total_weight:
            i += 1
            total_weight += self.sample_weights[i]
        best_orig_index = self.sample_origs[i]
        best_targ_index = self.sample_targs[i]

        orig_node = self.net.get_nodes()[best_orig_index]
        targ_node = self.net.get_nodes()[best_targ_index]

        return orig_node, targ_node

    def weights_dist(self, gen):
        total_weight1 = 0.
        total_weight2 = 0.

        for i in range(self.trials):
            total_weight1 += self.sample_weights[i]
            total_weight2 += gen.sample_weights[i]

        dist = 0.
        for i in range(self.trials):
            self.sample_weights[i] /= total_weight1
            gen.sample_weights[i] /= total_weight2
            dist += abs(self.sample_weights[i] - gen.sample_weights[i])
        dist /= self.trials
        return dist

    def run(self, shadow=None):
        self.last_weight = -1.
        self.constant = True

        dist = 0.

        self.net = Graph(n=self.net_params['nodes'], directed=self.net_params['directed'])

        if shadow is not None:
            shadow.net = self.net

        # create edges
        self.iterations = 0
        while self.iterations < self.net_params['edges']:
            orig, targ = self.cycle()

            if shadow is not None:
                shadow.cycle(self)
                dist += self.weights_dist(shadow)

            self.net.add_edge(orig, targ)
            self.iterations += 1

        dist /= self.net_params['edges']
        return dist

    def init_random(self):
        self.prog = prog.create_random(self.var_names)

    def recombine(self, parent2):
        generator = self.instance()
        generator.prog = self.prog.recombine(parent2.prog)
        return generator

    def mutate(self):
        random_gen = self.instance()
        random_gen.init_random()
        return self.recombine(random_gen)
