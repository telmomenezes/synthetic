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
    unsigned int* get_matrix(){return _matrix;}

    void update_distances(unsigned int new_orig, unsigned int new_targ);

private:
    DistMatrix();
    virtual ~DistMatrix();

    DistMatrix(DistMatrix const&);      // do not implement
    void operator=(DistMatrix const&);  // do not implement

    unsigned int* _matrix;
    unsigned int _nodes;
};

}
