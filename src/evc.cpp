/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "CLParser.h"
#include "Generator.h"
#include "Histogram2D.h"
#include <stdio.h>
#include <iostream>


using std::cout;
using std::endl;


int main(int argc, char *argv[])
{
    CLParser clp(2, 2);
    clp.parse(argc, argv);
    const char* netFile = clp.getOperand(0);
    const char* csvFile = clp.getOperand(1);

    Network* net = new Network();
    net->load(netFile);

    net->computeEigenvectorCentr();
    net->writeEigenvectorCentr(csvFile);
    
    delete(net);

    cout << "done." << endl;

    return 0;
}

