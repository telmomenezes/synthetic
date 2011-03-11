/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "CLParser.h"
#include "GA.h"
#include "Network.h"
#include "Params.h"
#include "utils.h"
#include <stdio.h>
#include <iostream>
#include <string>


using std::cout;
using std::endl;
using std::string;


int main(int argc, char *argv[])
{
    unsigned int popSize = 100;
    double recombRate = 0.1;
    double mutRate = 0.1;
    unsigned int tourSize = 2;
    unsigned int stab = 10;
    unsigned int maxCycles = 100000;
    unsigned int binNumber = 10;
    unsigned int tagsSize = 2;
    unsigned int maxNodeTypes = 10;
    
    RANDOM_SEED;

    Params::inst().setTagsSize(tagsSize);
    Params::inst().setMaxNodeTypes(maxNodeTypes);

    CLParser clp(1, 1);
    clp.addOption("genFilePre", "g", "genfilepre", true, "generator file name prefix");
    clp.addOption("netFilePre", "n", "netfilepre", true, "network file name prefix");
    clp.addOption("evcFilePre", "e", "evcfilepre", true, "evc data file name prefix");
    clp.parse(argc, argv);
    const char* netFile = clp.getOperand(0);
    string genFilePre = clp.getStringOption("genFilePre", "gens/gen");
    string netFilePre = clp.getStringOption("netFilePre", "");
    string evcFilePre = clp.getStringOption("evcFilePre", "");

    cout << "evonet started" << endl << endl;
    cout << "population size: " << popSize << endl;
    cout << "mutation rate: " << mutRate << endl;
    cout << "recombination rate: " << recombRate << endl;
    cout << "tournament size: " << tourSize << endl;
    cout << "stable generations: " << stab << endl;
    cout << "maximum simulation cycles: " << maxCycles << endl;
    cout << "bins per metric: " << binNumber << endl;
    cout << "tag size: " << tagsSize << endl;
    cout << "maximum number of node types: " << maxNodeTypes << endl;
    cout << endl;

    Network net;
    net.load(netFile);

    GA ga;
    ga.setBinNumber(binNumber);
    
    ga.setTargetNet(&net);
    net.printInfo();
    cout << endl;
    
    ga.setRecombRate(recombRate);
    ga.setMutRate(mutRate);
    ga.setTourSize(tourSize);
    ga.setStab(stab);
    ga.setMaxCycles(maxCycles);
    ga.setGenFilePre(genFilePre.c_str());
    ga.setNetFilePre(netFilePre.c_str());
    ga.setEVCFilePre(evcFilePre.c_str());
    ga.run(popSize);
    
    return 0;
}

