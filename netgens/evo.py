import random
from netgens.utils import *


def within_tolerance(fitness, best_fitness, tolerance):
    return abs(fitness - best_fitness) < tolerance


class EvaluatedIndividual(object):
    def __init__(self, evo, generator, net):
        self.generator = generator
        self.fitness_max, self.fitness_avg, self.fitness_tuple = evo.compute_fitness(net)

    def is_better_than(self, eval_indiv, best_fitness, tolerance):
        fitness_orig = self.fitness_max
        fitness_targ = eval_indiv.fitness_max

        if tolerance <= 0:
            return fitness_orig < fitness_targ

        if within_tolerance(fitness_orig, best_fitness, tolerance):
            if not within_tolerance(fitness_targ, best_fitness, tolerance):
                return True
            else:
                return self.generator.prog.size() < eval_indiv.generator.prog.size()
        return False


class Evo(object):
    def __init__(self, distances, generations, tolerance, base_generator, out_dir):
        self.distances = distances
        self.generations = generations
        self.base_generator = base_generator
        self.out_dir = out_dir
        self.tolerance = tolerance

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

    def compute_fitness(self, net):
        dists = self.distances.compute(net)
        fitness_max = max(dists)
        fitness_avg = sum(dists) / len(dists)
        return fitness_max, fitness_avg, dists

    def run(self):
        # init state
        self.gen_time = 0
        self.sim_time = 0
        self.fit_time = 0
        self.best_count = 0
        self.write_log_header()

        # init population
        generator = self.base_generator.spawn_random()
        net = generator.run()
        self.best_fit_individual = EvaluatedIndividual(self, generator, net)
        self.best_individual = self.best_fit_individual

        # evolve
        stable_gens = 0
        self.curgen = 0
        while stable_gens < self.generations:
            self.curgen += 1
            stable_gens += 1

            start_time = current_time_millis()

            sim_time = 0
            fit_time = 0

            if random.choice([True, False]):
                generator = self.best_fit_individual.generator.clone()
            else:
                generator = self.best_individual.generator.clone()

            generator = generator.mutate()

            time0 = current_time_millis()
            net = generator.run()
            sim_time += current_time_millis() - time0
            time0 = current_time_millis()
            individual = EvaluatedIndividual(self, generator, net)
            fit_time += current_time_millis() - time0

            best_fitness_max = self.best_fit_individual.fitness_max
            if individual.is_better_than(self.best_fit_individual, best_fitness_max, 0):
                self.best_fit_individual = individual
                stable_gens = 0

            if individual.is_better_than(self.best_individual, best_fitness_max, self.tolerance):
                self.best_individual = individual
                self.on_new_best()
                stable_gens = 0

            # time it took to compute the generation
            gen_time = current_time_millis() - start_time
            gen_time /= 1000
            sim_time /= 1000
            fit_time /= 1000

            print('stable generation: %s' % stable_gens)
            self.on_generation()

        print('Done.')

    def on_new_best(self):
        suffix = '%s_gen%s' % (self.best_count, self.curgen)
        best_gen = self.best_individual.generator
        
        # write net
        best_gen.net.save('%s/bestnet%s.gml' % (self.out_dir, suffix))
        best_gen.net.save('%s/bestnet.gml' % self.out_dir)
        
        # write progs
        best_gen.prog.write('%s/bestprog%s.txt' % (self.out_dir, suffix))
        best_gen.prog.write('%s/bestprog.txt' % self.out_dir)
        
        self.best_count += 1

    def write_log_header(self):
        # write header of log file
        with open('%s/evo.csv' % self.out_dir, 'w') as log_file:
            header = 'gen,best_fit_max,best_fit_avg,best_geno_size,gen_comp_time,sim_comp_time,fit_comp_time'
            stat_names = [stat.name() for stat in self.distances.targ_stats_set.stats]
            header = '%s,%s\n' % (header, ','.join(stat_names))
            log_file.write(header)

    def on_generation(self):
        dists = [str(dist) for dist in self.best_individual.fitness_tuple]

        # write log line for generation
        with open('%s/evo.csv' % self.out_dir, 'a') as log_file:
            row = ','.join((self.curgen, self.best_individual.fitness_max, self.best_individual.fitness_avg,
                            self.best_individual.prog.size(), self.gen_time, self.sim_time, self.fit_time))
            row = '%s,%s\n' % (row, ','.join(dists))
            log_file.write(row)

        # print info
        print(self.gen_info_string())
        stat_names = [stat.name() for stat in self.distances.targ_stats_set.stats]
        items = ['%s: %s' % (stat_names[i], dists[i]) for i in range(len(stat_names))]
        print('; '.join(items))

    def info_string(self):
        lines = ['stable generations: %s' % self.generations,
                 'directed: %s' % self.distances.net.is_directed(),
                 'target net node count: %s' % len(self.distances.net.vs),
                 'target net edge count: %s' % len(self.distances.net.es),
                 'distribution bins: %s' % self.distances.bins,
                 'tolerance: %s' % self.tolerance]
        return '\n'.join(lines)

    def gen_info_string(self):
        items = ['gen #%s' % self.curgen,
                 'best fitness max: %s' % self.best_individual.fitness_max,
                 'best fitness avg: %s' % self.best_individual.fitness_avg,
                 'best genotype size: %s' % self.best_individual.prog.size(),
                 'gen comp time: %ss.' % self.gen_time,
                 'sim comp time: %ss.' % self.sim_time,
                 'fit comp time: %ss.' % self.fit_time]
        return '; '.join(items)
