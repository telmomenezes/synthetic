/*
 * Copyright (C) 2011 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


namespace syn
{

class DistMatrix
{
public:
    static DistMatrix& get_instance()
    {
        static DistMatrix instance;
        return instance;
    }

    void set_nodes(unsigned int nodes);
    unsigned int* get_umatrix(){return _umatrix;}
    unsigned int* get_dmatrix(){return _dmatrix;}

    void update_distances(unsigned int new_orig, unsigned int new_targ);
    unsigned int get_udistance(unsigned int orig, unsigned int targ);
    unsigned int get_ddistance(unsigned int orig, unsigned int targ);

private:
    DistMatrix();
    virtual ~DistMatrix();

    DistMatrix(DistMatrix const&);      // do not implement
    void operator=(DistMatrix const&);  // do not implement


    // undirected distance matrix
    unsigned int* _umatrix;
    // directed distance matrix
    unsigned int* _dmatrix;
    unsigned int _nodes;
};

}
