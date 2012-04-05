package com.telmomenezes.synthetic;

import java.util.Arrays;


public class DistMatrix {
    private static DistMatrix _instance = null;
    
    // undirected distance matrix
    int[] _umatrix;
    // directed distance matrix
    int[] _dmatrix;
    int _nodes;
    
    public DistMatrix() {
        _nodes = 0;
    }

    public static DistMatrix instance()
    {
        if (_instance == null) {
            _instance = new DistMatrix();
        }

        return _instance;
    }
    
    public void setNodes(int nodes)
    {
        if (nodes != _nodes) {
            if (_nodes > 0) {
                _umatrix = null;
                _dmatrix = null;
            }
            _nodes = nodes;
            if (_nodes > 0) {
                _umatrix = new int[_nodes * _nodes];
                _dmatrix = new int[_nodes * _nodes];
            }
        }

        // clear matrices
        if (_nodes > 0) {
            Arrays.fill(_umatrix, _nodes * _nodes);
            Arrays.fill(_dmatrix, _nodes * _nodes);
        }
    }

    public int getUDist(int x, int y) {
        return _umatrix[(y * _nodes) + x];
    }
    
    public int getDDist(int x, int y) {
        return _dmatrix[(y * _nodes) + x];
    }
    
    public void setUDist(int x, int y, int d) {
        _umatrix[(y * _nodes) + x] = d;
    }
    
    public void setDDist(int x, int y, int d) {
        _dmatrix[(y * _nodes) + x] = d;
    }
    
    public void updateDistances(int new_orig, int new_targ)
    {   
        // update distances between new_orig and new_targ
        setUDist(new_orig, new_targ, 1);
        setUDist(new_targ, new_orig, 1);
        setDDist(new_orig, new_targ, 1);

        // iterate through all pairs of nodes
        for (int i = 0; i < _nodes; i++) {
            if ((getUDist(i, new_orig) == 0) && (getUDist(i, new_targ) == 0)) {
                continue;
            }

            for (int j = 0; j < _nodes; j++) {
                // update undirected distances
                int d0 = getUDist(i, new_orig);
                int d1 = getUDist(new_targ, j);
                int d = d0 + d1;
                if ((d0 > 0) && (d1 > 0)) {
                    if ((d < getUDist(i, j)) || (getUDist(i, j) == 0)) {
                        setUDist(i, j, d);
                    }
                }

                d0 = getUDist(j, new_orig);
                d1 = getUDist(new_targ, i);
                d = d0 + d1;
                if ((d0 > 0) && (d1 > 0)) {
                    if ((d < getUDist(i, j)) || (getUDist(i, j) == 0)) {
                        setUDist(i, j, d);
                    }
                }

                // update directed distances
                d0 = getDDist(i, new_orig);
                d1 = getDDist(new_targ, j);
                d = d0 + d1;
                if ((d0 > 0) && (d1 > 0)) {
                    if ((d < getDDist(i, j)) || (getDDist(i, j) == 0)) {
                        setDDist(i, j, d);
                    }
                }
            }
        }
    }
}
