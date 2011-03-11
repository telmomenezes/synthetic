/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "Generator.h"
#include "Params.h"
#include "utils.h"
#include <stdlib.h>
#include <math.h>
#include <iostream>
#include <fstream>
#include <string.h>


using std::endl;
using std::cout;
using std::ofstream;


Generator::Generator()
{
    unsigned int maxNodeTypes = Params::inst().getMaxNodeTypes();
    _dataSize = Params::inst().getTypeSize();
    _dataSize *= maxNodeTypes;
    data = (double *)malloc(sizeof(double) * _dataSize);
    affMatrix = (double *)malloc(sizeof(double) * maxNodeTypes * maxNodeTypes);
}


Generator::~Generator()
{
    free(data);
    free(affMatrix);
}


Generator* Generator::clone()
{
    Generator* gen = new Generator();
    memcpy(gen->data, data, sizeof(double) * _dataSize);
    return gen;
}


void Generator::setType(unsigned int pos, double weight, double probConn,
                    double probRand, double probStop, const char* tags, const char* mask)
{
    unsigned int offset = Params::inst().getTypeSize() * pos;

    data[offset + ACTIVE] = 1.0;
    data[offset + WEIGHT] = weight;
    data[offset + PROB_CONN] = probConn;
    data[offset + PROB_RAND] = probRand;
    data[offset + PROB_STOP] = probStop;

    unsigned int tagsSize = Params::inst().getTagsSize();

    for (unsigned int i = 0; i < tagsSize; i++) {
        if (tags[i] == '0')
            data[offset + TAGS + i] = 0.0;
        else
            data[offset + TAGS + i] = 1.0;

        if (mask[i] == '0')
            data[offset + TAGS + tagsSize + i] = 0.0;
        else if (mask[i] == '*')
            data[offset + TAGS + tagsSize + i] = 0.5;
        else
            data[offset + TAGS + tagsSize + i] = 1.0;
    }
}


void Generator::write(const char* filePath)
{
    ofstream out;
    out.open(filePath);

    unsigned int tagsSize = Params::inst().getTagsSize();

    for (unsigned int pos = 0; pos < Params::inst().getMaxNodeTypes(); pos++) {
        unsigned int offset = Params::inst().getTypeSize() * pos;
        if (data[offset + ACTIVE] > 0.5) {
            out << "[Type " << pos << "] ";

            for (unsigned int i = 0; i < tagsSize; i++) {
                if (data[offset + TAGS + i] > 0.5)
                    out << "1";
                else
                    out << "0";
            }

            out << " ";

            for (unsigned int i = 0; i < tagsSize; i++) {
                if (data[offset + TAGS + tagsSize + i] < (1.0 / 3.0))
                    out << "0";
                else if (data[offset + TAGS + tagsSize + i] < (2.0 / 3.0))
                    out << "*";
                else
                    out << "1";
            }

            out << " weight: " << data[offset + WEIGHT];
            out << " p_conn: " << data[offset + PROB_CONN];
            out << " p_rand: " << data[offset + PROB_RAND];
            out << " p_stop: " << data[offset + PROB_STOP];
            out << endl;
        }
    }

    out.close();
}


double Generator::typeDistance(unsigned int origType, unsigned int targType)
{
    unsigned int offsetOrig = Params::inst().getTypeSize() * origType;
    unsigned int offsetTarg = Params::inst().getTypeSize() * targType;
    unsigned int tagsSize = Params::inst().getTagsSize();
    unsigned int distance = 0;
    unsigned int maxDistance = 0;

    for (unsigned int i = 0; i < tagsSize; i++) {
        bool mask = ((data[offsetOrig + TAGS + tagsSize + i] < (1.0 / 3.0)) ||
                    (data[offsetOrig + TAGS + tagsSize + i] >= (2.0 / 3.0)));
        bool tagOrig = data[offsetOrig + TAGS + tagsSize + i] > 0.5;
        bool tagTarg = data[offsetTarg + TAGS + i] > 0.5;

        if (mask && (tagOrig != tagTarg))
            distance++;

        if (mask)
            maxDistance++;
    }

    if (maxDistance == 0)
        return 0;
    else
        return (double)distance / (double)maxDistance;
}


double Generator::affinity(unsigned int origType, unsigned int targType)
{
    // TODO: config

    double prox = 1.0 - typeDistance(origType, targType);
    // sigmoid
    //double thres = 0.8;
    //double steep = 10;
    //double aff = 1 / (1 + exp(-((prox - thres) * steep)));
    // threshold - linear
    double aff = 0;
    if (prox > 0.5) {
        aff = (prox - 0.5) * 2.0;
    }

    //cout << "orig: " << origType << "; targ:" << targType << "; prox: "
    //    << prox << "; aff: " << aff << endl;

    return aff;
}


void Generator::calcAffMatrix()
{
    unsigned int maxNodeTypes = Params::inst().getMaxNodeTypes();
    unsigned int typeSize = Params::inst().getTypeSize();
    for (unsigned int i = 0; i < maxNodeTypes; i++) {
        for (unsigned int j = 0; j < maxNodeTypes; j++) {
            unsigned int offset = typeSize * i;
            double prob = data[offset + PROB_CONN];
            prob *= affinity(i, j);
            affMatrix[(i * maxNodeTypes) + j] = prob;
        }
    }
}


void Generator::generateNodes(Network* net, unsigned int nodes)
{
    unsigned int nTypes = Params::inst().getMaxNodeTypes();
    double totalWeight = 0.0;

    for (unsigned int pos = 0; pos < nTypes; pos++) {
        unsigned int offset = Params::inst().getTypeSize() * pos;
        if (data[offset + ACTIVE] > 0.5)
            totalWeight += data[offset + WEIGHT];
    }

    for (unsigned int i = 0; i < nodes; i++) {
        double targWeight = RANDOM_UNIFORM * totalWeight;
        double curWeight = 0;
       
        unsigned int type;
        for (type = 0; type < nTypes; type++) {
            unsigned int offset = Params::inst().getTypeSize() * type;
            if (data[offset + ACTIVE] > 0.5) {
                curWeight += data[offset + WEIGHT];

                if (curWeight >= targWeight)
                    break;
            }
        }

        net->addNode(type);
    }
}


Network* Generator::generateNetwork(unsigned int nodes, unsigned int edges,
                                        unsigned int maxCycles)
{
    unsigned int maxNodeTypes = Params::inst().getMaxNodeTypes();
 
    Network* net = new Network();

    calcAffMatrix();

    generateNodes(net, nodes); 

    unsigned int curEdges = 0;
    unsigned int cycle = 0;

    while ((curEdges < edges) && (cycle < maxCycles)) {
        vector<Node*>& nodevec = net->getNodes();
        for (vector<Node*>::iterator iterNode = nodevec.begin();
                iterNode != nodevec.end();
                iterNode++) {

            Node* node = (*iterNode);
            unsigned int type = node->getType();
            unsigned int offset = Params::inst().getTypeSize() * type;
            double probRand = data[offset + PROB_RAND];
            double probStop = data[offset + PROB_STOP];
            Node* visiting = node->getVisiting();

            // Navigation
            bool restart = false;
            if (visiting) {
                if ((visiting->getOutDegree() == 0) || 
                    (RANDOM_TESTPROB(probStop))) {
                    
                    restart = true;
                }
            }
            else {
                restart = true;
            }

            bool rand = false;
            if ((node->getOutDegree() == 0)
                || (RANDOM_TESTPROB(probRand))) {

                rand = true;
            }   

            if (restart) {
                if (rand) {
                    unsigned int targIndex = RANDOM_UINT(nodes);
                    visiting = nodevec[targIndex];
                }
                else {
                    visiting = node->getRandomTarget();
                }
            }
            else {
                visiting = visiting->getRandomTarget();
            }

            node->setVisiting(visiting);

            // New connection?
            unsigned int origType = node->getType();
            unsigned int targType = visiting->getType();
            double probConn = affMatrix[(origType * maxNodeTypes) + targType];

            if (RANDOM_TESTPROB(probConn))
                if (net->addEdge(node, visiting))
                    curEdges++;

            cycle++;
        }
    }

    return net;
}


void Generator::initRandom()
{
    for (unsigned int i = 0; i < _dataSize; i++)
        data[i] = RANDOM_UNIFORM;
}


void Generator::mutate()
{
    unsigned int index = RANDOM_UINT(_dataSize);
    data[index] += RANDOM_NORMAL;

    if (data[index] < 0)
        data[index] = 0;
    else if (data[index] > 1.0)
        data[index] = 1.0;
}


Generator* Generator::recombine(Generator* parent2)
{
    Generator* gen = clone();
    unsigned int index = RANDOM_UINT(_dataSize);

    memcpy(gen->data + index, data + index, sizeof(double) * (_dataSize - index));

    return gen;
}
