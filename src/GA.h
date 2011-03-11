/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "Generator.h"
#include "Network.h"
#include "Histogram2D.h"
#include <string>


using std::string;


class GA
{
public:
    GA();
    virtual ~GA();

    void setTargetNet(Network* targNet);

    void run(unsigned int popSize);

    void setRecombRate(double value) {_recombRate = value;}
    void setMutRate(double value) {_mutRate = value;}
    void setTourSize(unsigned int value) {_tourSize = value;}
    void setStab(unsigned int value) {_stab = value;}
    void setMaxCycles(unsigned int value) {_maxCycles = value;}
    void setBinNumber(unsigned int value) {_binNumber = value;}
    void setGenFilePre(const char* value) {_genFilePre = value;}
    void setNetFilePre(const char* value) {_netFilePre = value;}
    void setEVCFilePre(const char* value) {_evcFilePre = value;}

private:
    Network* _targNet;
    Histogram2D* _targHist;
    unsigned int _popSize;
    Generator** _population;

    Generator* selectParent(unsigned int tourSize);

    double _recombRate;
    double _mutRate;
    unsigned int _tourSize;
    unsigned int _stab;
    unsigned int _maxCycles;
    unsigned int _binNumber;
    bool _logCount;

    string _genFilePre;
    string _netFilePre;
    string _evcFilePre;
};

