/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "Network.h"


using std::ostream;


class Generator {
public:
    Generator();
    virtual ~Generator();

    Generator* clone();

    void setType(unsigned int pos, double weight, double probConn,
                    double probRand, double probStop, const char* tags, const char* mask);

    void write(const char* filePath);

    Network* generateNetwork(unsigned int nodes, unsigned int edges,
                                unsigned int maxCycles);

    void initRandom();
    void mutate();
    Generator* recombine(Generator* parent2);

    double _fitness;

private:
    static const unsigned int ACTIVE = 0;
    static const unsigned int WEIGHT = 1;
    static const unsigned int PROB_CONN = 2;
    static const unsigned int PROB_RAND = 3;
    static const unsigned int PROB_STOP = 4;
    static const unsigned int TAGS = 5;

    double *data;
    double *affMatrix;

    unsigned int _dataSize;

    void generateNodes(Network* net, unsigned int nodes);
    double typeDistance(unsigned int origType, unsigned int targType);
    double affinity(unsigned int origType, unsigned int targType);
    void calcAffMatrix();
};
