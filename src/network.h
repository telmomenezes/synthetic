/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "node.h"
#include "histogram2D.h"


typedef struct 

class Network {
public:
    Network();
    virtual ~Network();

    Node* addNode(unsigned int type);
    bool addEdge(Node* orig, Node* targ);
    vector<Node*>& getNodes() {return _nodes;}

    void write(const char* filePath);
    void writeGEXF(const char* filePath);

    void computeEigenvectorCentr();
    void writeEigenvectorCentr(const char* filePath);
    Histogram2D* getEVCHistogram(unsigned int binNumber, double minValHor,
        double maxValHor, double minValVer, double maxValVer);

    void load(const char* filePath);

    unsigned int getNodeCount() {return _nodeCount;}
    unsigned int getEdgeCount() {return _edgeCount;}

    void printInfo();

    double _minEVCIn;
    double _minEVCOut;
    double _maxEVCIn;
    double _maxEVCOut;

private:
    static unsigned int _CURID;
    vector<Node*> _nodes;

    unsigned int _nodeCount;
    unsigned int _edgeCount;

    Histogram2D* _lastHistogram;
};
