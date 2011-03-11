/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "GA.h"
#include "utils.h"
#include <iostream>
#include <sstream>
#include <time.h>


using std::cout;
using std::endl;
using std::flush;
using std::ostringstream;


GA::GA()
{
    _targNet = NULL;
    _targHist = NULL;
    _population = NULL;

    _recombRate = 0.2;
    _mutRate = 0.2;
    _tourSize = 2;
    _stab = 10;
    _maxCycles = 50000;
    _binNumber = 1000;
    _logCount = true;

    _genFilePre = "gens/gen";
    _netFilePre = "";
    _evcFilePre = "";
}


GA::~GA()
{
    if (_population) {
        for (unsigned int i = 0; i < _popSize; i++) {
            delete _population[i];
        }
        free(_population);
        _population = NULL;
    }

    if (_targHist)
        delete _targHist;
}


void GA::setTargetNet(Network* targNet)
{
    _targNet = targNet;
    _targNet->computeEigenvectorCentr();
    _targHist = _targNet->getEVCHistogram(_binNumber, _targNet->_minEVCIn,
        _targNet->_maxEVCIn, _targNet->_minEVCOut, _targNet->_maxEVCOut);
    if (_logCount)
        _targHist->logScale();
}


void GA::run(unsigned int popSize)
{
    _popSize = popSize;

    // generate initial population
    _population = (Generator**)malloc(sizeof(Generator*) * _popSize);
    for (unsigned int i = 0; i < _popSize; i++) {
        _population[i] = new Generator();
        _population[i]->initRandom();
    }

    unsigned int gen = 0;
    float bestFitness;
    unsigned int stableGens = 0;

    while (true) {
        time_t startGenTime = time(NULL);
        time_t simTime = 0;
        time_t evcTime = 0;
        time_t emdTime = 0;

        // test stop criteria
        if (stableGens >= _stab)
            return;
        stableGens++;

        float bestGenFitness;

        // compute fitnesses
        for (unsigned int i = 0; i < _popSize; i++) {
            
            time_t startTime = time(NULL);
            Network* simNet = _population[i]->generateNetwork(_targNet->getNodeCount(),
                _targNet->getEdgeCount(), _maxCycles);
            simTime += time(NULL) - startTime;
            
            startTime = time(NULL);
            simNet->computeEigenvectorCentr();
            evcTime += time(NULL) - startTime;

            Histogram2D* simHist = simNet->getEVCHistogram(_binNumber,
                _targNet->_minEVCIn, _targNet->_maxEVCIn,
                _targNet->_minEVCOut, _targNet->_maxEVCOut);
            if (_logCount)
                simHist->logScale();

            startTime = time(NULL);
            double fitness = _targHist->emdDist(simHist);
            emdTime += time(NULL) - startTime;
            _population[i]->_fitness = fitness;

            // New best fitness found
            if (((gen == 0) && (i == 0)) || (fitness < bestFitness)) {
                bestFitness = fitness;
                cout << "new best fitness: " << fitness << endl;

                cout << endl;
                _targHist->print();
                cout << endl;
                simHist->print();
                cout << endl;

                // Write best generator
                ostringstream buff;
                buff << _genFilePre << bestFitness << ".gen";
                _population[i]->write(buff.str().c_str());
                ostringstream buff2;
                buff2 << _genFilePre << "best.gen";
                _population[i]->write(buff2.str().c_str());

                // Write best simulated net
                if (_netFilePre.size() > 0) {
                    ostringstream buff3;
                    buff3 << _netFilePre << bestFitness << ".csv";
                    simNet->write(buff3.str().c_str());
                    ostringstream buff4;
                    buff4 << _netFilePre << "best.csv";
                    simNet->write(buff4.str().c_str());
                }

                // Write evc data of best simulated net
                if (_evcFilePre.size() > 0) {
                    ostringstream buff3;
                    buff3 << _evcFilePre << bestFitness << ".csv";
                    simNet->writeEigenvectorCentr(buff3.str().c_str());
                    ostringstream buff4;
                    buff4 << _evcFilePre << "best.csv";
                    simNet->writeEigenvectorCentr(buff4.str().c_str());
                }

                stableGens = 0;
            }
            if ((i == 0) || (fitness < bestGenFitness))
                bestGenFitness = fitness;

            delete simHist;
            delete simNet;
        }


        // generate next population
        Generator** newPop = (Generator**)malloc(sizeof(Generator*) * _popSize);

        for (unsigned int i = 0; i < _popSize; i++) {

            Generator* parent1 = selectParent(_tourSize);
            Generator* child;

            if (RANDOM_TESTPROB(_recombRate)) {
                Generator* parent2 = selectParent(_tourSize);
                child = parent1->recombine(parent2);
            }
            else {
                child = parent1->clone();
            }

            if (RANDOM_TESTPROB(_mutRate)) {
                child->mutate();
            }

            newPop[i] = child;
        }

        // delete old population
        for (unsigned int i = 0; i < _popSize; i++) {
            delete _population[i];
        }
        free(_population);
        _population = newPop;

        cout << "gen #" << gen
            << " best fitness: " << bestFitness
            << "; best gen fitness: " << bestGenFitness
            << "; time: " << (time(NULL) - startGenTime)
            << "; sim time: " << simTime
            << "; evc time: " << evcTime
            << "; emd time: " << emdTime
            << endl;

        gen++;
    }
}


Generator* GA::selectParent(unsigned int tourSize)
{
    Generator* parent = NULL;
    for (unsigned int i = 0; i < tourSize; i++) {
        Generator* candidate = _population[RANDOM_UINT(_popSize)];
        if ((!parent) || (candidate->_fitness < parent->_fitness))
            parent = candidate;
    }

    return parent;
}

