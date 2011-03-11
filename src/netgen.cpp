/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "Generator.h"
#include "utils.h"
#include <stdio.h>
#include <iostream>


using std::cout;
using std::endl;


int main(int argc, char *argv[])
{
    unsigned int binNumber = 10;

    RANDOM_SEED;

    Generator gen;
    gen.setType(0, 0.40, 0.1, 0.0, 0.5, "010", "0**");
    gen.setType(1, 0.40, 0.1, 0.3, 0.1, "101", "1**");
    //gen.setType(2, 0.1, 0.1, 1.0, 1.0, "001", "*1*");
    //gen.setType(3, 0.1, 0.1, 1.0, 1.0, "110", "**1");
    
    Network* net = gen.generateNetwork(20000, 300000, 100000000);
    net->computeEigenvectorCentr();
    Histogram2D* simHist = net->getEVCHistogram(binNumber, net->_minEVCIn, net->_maxEVCIn,
                net->_minEVCOut, net->_maxEVCOut);
    simHist->logScale();

    net->printInfo();
    simHist->print();

    delete(simHist);
    delete(net);
    return 0;
}

