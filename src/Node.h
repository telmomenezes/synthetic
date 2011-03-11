/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once
#include <vector>
#include <map>


using std::vector;
using std::map;


class Node {
public:
    Node(unsigned int type, unsigned int id);
    virtual ~Node();

    bool addEdge(Node* target);
    bool edgeExists(Node* target);

    unsigned int getType() {return _type;}
    unsigned int getId() {return _id;}
    void setVisiting(Node* node) {_visiting = node;}
    Node* getVisiting() {return _visiting;}
    unsigned int getInDegree() {return _inDegree;}
    unsigned int getOutDegree() {return _outDegree;}
    Node* getRandomTarget();
    vector<Node*>* getTargets() {return &_targets;}
    vector<Node*>* getOrigins() {return &_origins;}
    void setMarked(bool value) {_marked = value;}
    bool isMarked() {return _marked;}

    unsigned int walk(unsigned long walkId);

    // eigenvector centrality
    double _evcIn;
    double _evcInLast;
    double _evcOut;
    double _evcOutLast;

private:
    unsigned int _type;
    unsigned int _id;
    vector<Node*> _targets;
    vector<Node*> _origins;
    unsigned int _inDegree;
    unsigned int _outDegree;
    Node* _visiting;
    bool _marked;
    unsigned long _lastWalkId;
};

