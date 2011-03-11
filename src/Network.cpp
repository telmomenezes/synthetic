/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "Network.h"
#include "utils.h"
#include <iostream>
#include <fstream>
#include <map>
#include <math.h>
#include <string.h>
#include <time.h>


using std::ofstream;
using std::ifstream;
using std::cout;
using std::endl;
using std::ios;
using std::map;


unsigned int Network::_CURID = 0;


Network::Network()
{
    _nodeCount = 0;
    _edgeCount = 0;
}


Network::~Network()
{
    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        delete (*iterNode);
    }
}


Node* Network::addNode(unsigned int type)
{
    _nodeCount++;
    Node* node = new Node(type, _CURID++);
    _nodes.push_back(node);
    return node;
}


bool Network::addEdge(Node* orig, Node* targ)
{
    if (orig->addEdge(targ)) {
        _edgeCount++;
        return true;
    }
    return false;
}


void Network::write(const char* filePath)
{
    ofstream f;
    f.open(filePath);

    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* origNode = (*iterNode);
        vector<Node*>* targets = origNode->getTargets();
        
        for (vector<Node*>::iterator iterConn = targets->begin();
                iterConn != targets->end();
                iterConn++) {

            Node* targNode = (*iterConn);
            f << origNode->getId() << "," << targNode->getId() << endl;
        }
    }

    f.close();
}


void Network::writeGEXF(const char* filePath)
{
    ofstream f;
    f.open(filePath);

    // start file
    f << "<gexf xmlns=\"http://www.gexf.net/1.1draft\" version=\"1.1\">" << endl;
    f << "<graph mode=\"static\" defaultedgetype=\"directed\">" << endl;

    // write nodes
    f << "<nodes>" << endl;
    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* node = (*iterNode);
        f << "<node id=\"" << node->getId() << "\">" << endl;

        /*
        unsigned int red = (unsigned int )(((node->getInEntropy() - _minInEntropy) / (_maxInEntropy - _minInEntropy)) * 255.0);
        unsigned int green = (unsigned int )(((node->getOutEntropy() - _minOutEntropy) / (_maxOutEntropy - _minOutEntropy)) * 255.0);
        
        unsigned int blue = 0;
        //f << "<viz:color r=\"" << red << "\" g=\"" << green << "\" b=\"" << blue << "\"/>" << endl;
        */

        /*
        if ((*iterNode)->getType() == 0)
            f << "<viz:color r=\"255\" g=\"0\" b=\"0\"/>" << endl;
        else if ((*iterNode)->getType() == 1)
            f << "<viz:color r=\"0\" g=\"0\" b=\"255\"/>" << endl;
        else if ((*iterNode)->getType() == 2)
            f << "<viz:color r=\"0\" g=\"255\" b=\"0\"/>" << endl;
        else if ((*iterNode)->getType() == 3)
            f << "<viz:color r=\"0\" g=\"0\" b=\"0\"/>" << endl;
        */
        f << "</node>" << endl;
    }
    f << "</nodes>" << endl;

    // write edges
    unsigned int edgeId = 0;
    f << "<edges>" << endl;
    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* origNode = (*iterNode);
        vector<Node*>* targets = origNode->getTargets();
        
        for (vector<Node*>::iterator iterConn = targets->begin();
                iterConn != targets->end();
                iterConn++) {

            Node* targNode = (*iterConn);
            f << "<edge id=\"" << edgeId++ << "\" source=\"" << origNode->getId()
                << "\" target=\"" << targNode->getId() << "\" />" << endl;
        }
    }
    f << "</edges>" << endl;

    // end file
    f << "</graph>" << endl;
    f << "</gexf>" << endl;

    f.close();
}


Histogram2D* Network::getEVCHistogram(unsigned int binNumber, double minValHor,
    double maxValHor, double minValVer, double maxValVer)
{
    double intervalHor = (maxValHor - minValHor) / ((double)binNumber);
    double intervalVer = (maxValVer - minValVer) / ((double)binNumber);

    Histogram2D* hist = new Histogram2D(binNumber + 1, minValHor - intervalHor,
        maxValHor, minValVer - intervalVer, maxValVer);

    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* node = (*iterNode);
       
        int x = 0;
        int y = 0;

        if (isfinite(node->_evcIn))
            if (node->_evcIn < minValHor)
                x = -1;
            else if (node->_evcIn > maxValHor)
                x = -1;
            else
                x = (unsigned int)ceil((node->_evcIn - minValHor) / intervalHor);
        if (isfinite(node->_evcOut))
            if (node->_evcOut < minValVer)
                y = -1;
            else if (node->_evcOut > maxValVer)
                y = -1;
            else
                y = (unsigned int)ceil((node->_evcOut - minValVer) / intervalVer);

        if ((x >= 0) && (y >= 0))
            hist->incValue(x, y);
    }

    return hist;
}


void Network::computeEigenvectorCentr()
{
    // TODO: config
    unsigned int maxIter = 100;

    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* node = (*iterNode);
        node->_evcInLast = 1;
        node->_evcOutLast = 1;
    }

    unsigned int i = 0;

    double deltaEVCIn = 999;
    double deltaEVCOut = 999;
    
    double zeroTest = 0.0001;

    while (((deltaEVCIn > zeroTest) || (deltaEVCOut > zeroTest)) && (i < maxIter)) {
        double accEVCIn = 0;
        double accEVCOut = 0;

        for (vector<Node*>::iterator iterNode = _nodes.begin();
                iterNode != _nodes.end();
                iterNode++) {

            Node* node = (*iterNode);

            node->_evcIn = 0;
            vector<Node*>* origins = node->getOrigins();
            for (vector<Node*>::iterator iterOrigin = origins->begin();
                    iterOrigin != origins->end();
                    iterOrigin++) {
                Node* origin = (*iterOrigin);
                //node->_evcIn += origin->_evcInLast / ((double)origin->getOutDegree());
                node->_evcIn += origin->_evcInLast;
            }
            //node->_evcIn *= 0.85;
            //node->_evcIn += (1 - 0.85) / ((double)_nodes.size());
            //node->_evcIn += (1.0 - 0.85);
            accEVCIn += node->_evcIn;

            node->_evcOut = 0;
            vector<Node*>* targets = node->getTargets();
            for (vector<Node*>::iterator iterTarget = targets->begin();
                    iterTarget != targets->end();
                    iterTarget++) {
                Node* target = (*iterTarget);
                node->_evcOut += target->_evcOutLast;
            }
            //node->_evcOut *= 0.85;
            //node->_evcIn += (1 - 0.85) / ((double)_nodes.size());
            //node->_evcOut += (1.0 - 0.85);
            accEVCOut += node->_evcOut;
        }

        deltaEVCIn = 0;
        deltaEVCOut = 0;

        for (vector<Node*>::iterator iterNode = _nodes.begin();
                iterNode != _nodes.end();
                iterNode++) {

            Node* node = (*iterNode);
            node->_evcIn /= accEVCIn;
            node->_evcOut /= accEVCOut;
            deltaEVCIn += fabs(node->_evcIn - node->_evcInLast);
            deltaEVCOut += fabs(node->_evcOut - node->_evcOutLast);
            //cout << "evcin: " << node->_evcIn << "; evcin_last: " << node->_evcInLast
                //<< "; delta: " << deltaEVCIn << endl;
            node->_evcInLast = node->_evcIn;
            node->_evcOutLast = node->_evcOut;
        }

        //cout << "#" << i << " delta in: " << deltaEVCIn
        //    << "; delta out: " << deltaEVCOut << endl;
        i++;
    }

    // use log scale
    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* node = (*iterNode);
        node->_evcIn = log(node->_evcIn);
        node->_evcOut = log(node->_evcOut);
    }


    // compute max EVC in and out
    _minEVCIn = 0;
    _minEVCOut = 0;
    _maxEVCIn = 0;
    _maxEVCOut = 0;
    bool first = true;
    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* node = (*iterNode);
        if (isfinite(node->_evcIn) && (first || (node->_evcIn < _minEVCIn)))
            _minEVCIn = node->_evcIn;
        if (isfinite(node->_evcOut) && (first || (node->_evcOut < _minEVCOut)))
            _minEVCOut = node->_evcOut;
        if (isfinite(node->_evcIn) && (first || (node->_evcIn > _maxEVCIn)))
            _maxEVCIn = node->_evcIn;
        if (isfinite(node->_evcOut) && (first || (node->_evcOut > _maxEVCOut)))
            _maxEVCOut = node->_evcOut;

        first = false;
    }
}


void Network::writeEigenvectorCentr(const char* filePath)
{
    /*
    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* node = (*iterNode);
        node->_evcIn = log(node->_evcIn);
        node->_evcOut = log(node->_evcOut);
        if (node->_evcIn < -30)
            node->_evcIn = -30;
        if (node->_evcOut < -30)
            node->_evcOut = -30;
    }
    */

    ofstream f;
    f.open(filePath);
   
    f << "evc_in, evc_out" << endl;

    for (vector<Node*>::iterator iterNode = _nodes.begin();
            iterNode != _nodes.end();
            iterNode++) {

        Node* node = (*iterNode);
        f << node->_evcIn << "," << node->_evcOut << endl;
    }

    f.close();
}


void Network::load(const char* filePath)
{
    // add nodes
    map<string, Node*> nodes;
    map<string, Node*>::iterator iterMap;

    string line;
    ifstream f(filePath);

    char* chrLine = (char*)malloc(sizeof(char) * 1000);
    
    while (f.good()) {
        getline(f, line);
        if (line == "")
            break;
        chrLine = strcpy(chrLine, line.c_str());
        string orig = string(strtok(chrLine, ","));
        string targ = string(strtok(NULL, ","));
        iterMap = nodes.find(orig);
        if (iterMap == nodes.end())
            nodes[orig] = addNode(0);
        iterMap = nodes.find(targ);
        if (iterMap == nodes.end())
            nodes[targ] = addNode(0);
    }

    // add links
    f.clear();
    f.seekg(0, ios::beg);

    while (f.good()) {
        getline(f, line);
        if (line == "")
            break;
        chrLine = strcpy(chrLine, line.c_str());
        string orig = string(strtok(chrLine, ","));
        string targ = string(strtok(NULL, ","));

        addEdge(nodes[orig], nodes[targ]);
    }

    free(chrLine);
    f.close();
}


void Network::printInfo()
{
    cout << "node number: " << _nodeCount << endl;
    cout << "edge number: " << _edgeCount << endl;
    cout << "log(evc_in): [" << _minEVCIn << ", " << _maxEVCIn << "]" << endl;
    cout << "log(evc_out): [" << _minEVCOut << ", " << _maxEVCOut << "]" << endl;
}

