/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "network.h"


static const unsigned int ACTIVE = 0;
static const unsigned int WEIGHT = 1;
static const unsigned int PROB_CONN = 2;
static const unsigned int PROB_RAND = 3;
static const unsigned int PROB_STOP = 4;
static const unsigned int TAGS = 5;


typedef struct syn_gen_s {
    unsigned int max_node_types;
    unsigned int tags_size;
    double *data;
    unsigned int data_size;
    double *aff_matrix;
    double fitness;
} syn_gen;


syn_gen *syn_create_generator(unsigned int max_node_types, unsigned int tags_size);
void syn_destroy_generator(syn_gen *gen);

syn_gen *syn_clone_generator(syn_gen *gen);

void setType(unsigned int pos, double weight, double probConn,
                    double probRand, double probStop, const char* tags, const char* mask);

void write(const char* filePath);

Network* generateNetwork(unsigned int nodes, unsigned int edges,
                                unsigned int maxCycles);

void initRandom();
void mutate();
Generator* recombine(Generator* parent2);

void generateNodes(Network* net, unsigned int nodes);
double typeDistance(unsigned int origType, unsigned int targType);
double affinity(unsigned int origType, unsigned int targType);
void calcAffMatrix();

