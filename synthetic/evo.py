import numpy as np

from synthetic.utils import current_time_millis


def within_tolerance(fitness, best_fitness, tolerance):
    return abs(fitness - best_fitness) < tolerance


class EvaluatedIndividual(object):
    def __init__(self, distances_to_net, generator, net):
        self.generator = generator
        self.distances = distances_to_net.compute(net)

        # TODO: other types of fitness
        self.fitness = max(self.distances)

    def is_better_than(self, eval_indiv, best_fitness, tolerance):
        fitness_orig = self.fitness
        fitness_targ = eval_indiv.fitness

        if tolerance <= 0:
            return fitness_orig < fitness_targ

        if within_tolerance(fitness_orig, best_fitness, tolerance):
            if not within_tolerance(fitness_targ, best_fitness, tolerance):
                return True
            else:
                return self.generator.prog.size() < eval_indiv.generator.prog.size()
        return False


class Evo(object):
    def __init__(self, net, distances_to_net, generations, tolerance, base_generator, out_dir, sample_ratio):
        self.distances_to_net = distances_to_net
        self.generations = generations
        self.tolerance = tolerance
        self.base_generator = base_generator
        self.out_dir = out_dir
        self.sample_ratio = sample_ratio

        # number of nodes and edges in target network
        self.nodes = net.graph.vcount()
        self.edges = net.graph.ecount()

        # best individuals
        self.best_individual = None
        self.best_fit_individual = None

        # state
        self.curgen = 0
        self.best_count = 0

        # timers
        self.gen_time = 0
        self.sim_time = 0.
        self.fit_time = 0.

    def run(self):
        # init state
        self.gen_time = 0
        self.sim_time = 0
        self.fit_time = 0
        self.best_count = 0
        self.write_log_header()

        # init population
        generator = self.base_generator.spawn_random()
        net = generator.run(self.nodes, self.edges, self.sample_ratio)
        self.best_fit_individual = EvaluatedIndividual(self.distances_to_net, generator, net)
        self.best_individual = self.best_fit_individual

        # evolve
        stable_gens = 0
        self.curgen = 0
        while stable_gens < self.generations:
            self.curgen += 1
            stable_gens += 1

            start_time = current_time_millis()

            self.sim_time = 0
            self.fit_time = 0

            if np.random.randint(0, 2) == 0:
                generator = self.best_fit_individual.generator.clone()
            else:
                generator = self.best_individual.generator.clone()

            generator = generator.mutate()

            time0 = current_time_millis()
            net = generator.run(self.nodes, self.edges, self.sample_ratio)
            self.sim_time += current_time_millis() - time0
            time0 = current_time_millis()
            individual = EvaluatedIndividual(self.distances_to_net, generator, net)
            self.fit_time += current_time_millis() - time0

            if individual.is_better_than(self.best_fit_individual, self.best_fit_individual.fitness, 0):
                self.best_fit_individual = individual
                stable_gens = 0

            if individual.is_better_than(self.best_individual, self.best_fit_individual.fitness, self.tolerance):
                self.best_individual = individual
                self.on_new_best()
                stable_gens = 0

            # time it took to compute the generation
            self.gen_time = current_time_millis() - start_time
            self.gen_time /= 1000
            self.sim_time /= 1000
            self.fit_time /= 1000

            print('stable generations: {}'.format(stable_gens))
            self.on_generation()

        print('Done.')

    def on_new_best(self):
        suffix = '{}_gen{}'.format(self.best_count, self.curgen)
        best_gen = self.best_individual.generator

        # write net
        best_gen.net.graph.save('{}/bestnet{}.gml'.format(self.out_dir, suffix))
        best_gen.net.graph.save('{}/bestnet.gml'.format(self.out_dir))

        # write progs
        best_gen.prog.write('{}/bestprog{}.txt'.format(self.out_dir, suffix))
        best_gen.prog.write('{}/bestprog.txt'.format(self.out_dir))

        self.best_count += 1

    def write_log_header(self):
        # write header of log file
        with open('{}/evo.csv'.format(self.out_dir), 'w') as log_file:
            header = 'gen,best_fit,best_geno_size,gen_comp_time,sim_comp_time,fit_comp_time'
            stat_names = [stat_type.name for stat_type in self.distances_to_net.targ_stats_set.stat_types]
            header = '{},{}\n'.format(header, ','.join(stat_names))
            log_file.write(header)

    def on_generation(self):
        best_dists = [str(dist) for dist in self.best_individual.distances]
        best_fit_dists = [str(dist) for dist in self.best_fit_individual.distances]

        # write log line for generation
        with open('{}/evo.csv'.format(self.out_dir), 'a') as log_file:
            row = ','.join([str(metric) for metric in (self.curgen,
                            self.best_individual.fitness,
                            self.best_individual.generator.prog.size(),
                            self.best_fit_individual.fitness,
                            self.best_fit_individual.generator.prog.size(),
                            self.gen_time, self.sim_time, self.fit_time)])
            row = '{},{},{}\n'.format(row, ','.join(best_dists), ','.join(best_fit_dists))
            log_file.write(row)

        # print info
        print('>>> GENERATION #{}; gen comp time: {}s.; sim comp time: {}s.; fit comp time: {}s.'.format(
            self.curgen, self.gen_time, self.sim_time, self.fit_time))
        stat_names = [stat_type.name for stat_type in self.distances_to_net.targ_stats_set.stat_types]
        print('[BEST GENERATOR] fitness: {}; genotype size: {}'.format(
            self.best_individual.fitness, self.best_individual.generator.prog.size()))
        print('; '.join(['{}: {}'.format(stat_names[i], best_dists[i]) for i in range(len(stat_names))]))
        print('[LOWEST FITNESS] fitness: {}; genotype size: {}'.format(
            self.best_fit_individual.fitness, self.best_fit_individual.generator.prog.size()))
        print('; '.join(['{}: {}'.format(stat_names[i], best_fit_dists[i]) for i in range(len(stat_names))]))
