/*
 * Copyright (C) 2011 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


namespace syn
{
class Node;

class Edge {
public:
	Edge();
	virtual ~Edge();
    
    Node* orig;
    Node* targ;
    Edge* next_orig;
    Edge* next_targ;
    unsigned long timestamp;
};

}
