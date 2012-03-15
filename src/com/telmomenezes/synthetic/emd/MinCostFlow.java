package com.telmomenezes.synthetic.emd;

import java.util.List;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

class EdgeLong {
    public EdgeLong(int to, long cost) {
        _to = to;
        _cost = cost;
    }

    int _to;
    long _cost;
}

class EdgeLong0 {
    public EdgeLong0(int to, long cost, long flow) {
        _to = to;
        _cost = cost;
        _flow = flow;
    }

    int _to;
    long _cost;
    long _flow;
}

class EdgeLong1 {
    public EdgeLong1(int to, long reduced_cost) {
        _to = to;
        _reduced_cost = reduced_cost;
    }

    int _to;
    long _reduced_cost;
}

class EdgeLong2 {
    public EdgeLong2(int to, long reduced_cost, long residual_capacity) {
        _to = to;
        _reduced_cost = reduced_cost;
        _residual_capacity = residual_capacity;
    }

    int _to;
    long _reduced_cost;
    long _residual_capacity;
}

class EdgeLong3 {
    public EdgeLong3() {
    }

    public EdgeLong3(int to, long dist) {
        _to = to;
        _dist = dist;
    }

    int _to;
    long _dist;
}

/*
 * //----------------------------------------------------------------------------
 * -- template<typename NUM_T> struct edgeCompareByCost { bool operator()(const
 * edge<NUM_T>& a, const edge<NUM_T>& b) { return a._cost<b._cost; } };
 * //--------
 * ----------------------------------------------------------------------
 */

class MinCostFlow {
    int _num_nodes;
    Vector<Integer> _nodes_to_Q;

    public MinCostFlow() {
        _nodes_to_Q = new Vector<Integer>();
    }

    // e - supply(positive) and demand(negative).
    // c[i] - edges that goes from node i. first is the second nod
    // x - the flow is returned in it
    long operator(Vector<Long> e, Vector<List<EdgeLong>> c,
            Vector<List<EdgeLong0>> x) {

        assert (e.size() == c.size());
        assert (x.size() == c.size());

        _num_nodes = e.size();
        _nodes_to_Q.setSize(_num_nodes);

        // init flow
        for (int from = 0; from < _num_nodes; ++from) {
            for (EdgeLong it : c.get(from)) {
                x.get(from).add(new EdgeLong0(it._to, it._cost, 0));
                x.get(it._to).add(new EdgeLong0(from, -it._cost, 0));
            } // it
        } // from

        // reduced costs for forward edges (c[i,j]-pi[i]+pi[j])
        // Note that for forward edges the residual capacity is infinity
        Vector<List<EdgeLong1>> r_cost_forward = new Vector<List<EdgeLong1>>();
        for (int i = 0; i < _num_nodes; i++) {
            r_cost_forward.add(new LinkedList<EdgeLong1>());
        }
        for (int from = 0; from < _num_nodes; ++from) {
            for (EdgeLong it : c.get(from)) {
                r_cost_forward.get(from).add(new EdgeLong1(it._to, it._cost));
            }
        }

        // reduced costs and capacity for backward edges (c[j,i]-pi[j]+pi[i])
        // Since the flow at the beginning is 0, the residual capacity is also
        // zero
        Vector<List<EdgeLong2>> r_cost_cap_backward = new Vector<List<EdgeLong2>>();
        for (int i = 0; i < _num_nodes; i++) {
            r_cost_cap_backward.add(new LinkedList<EdgeLong2>());
        }
        for (int from = 0; from < _num_nodes; ++from) {
            for (EdgeLong it : c.get(from)) {
                r_cost_cap_backward.get(it._to).add(
                        new EdgeLong2(from, -it._cost, 0));
            } // it
        } // from

        // Max supply TODO:demand?, given U?, optimization-> min out of
        // demand,supply
        long U = 0;
        for (int i = 0; i < _num_nodes; ++i) {
            if (e.get(i) > U)
                U = e.get(i);
        }
        long delta = (long) (Math.pow(2.0,
                Math.ceil(Math.log((double) U) / Math.log(2.0))));

        Vector<Long> d = new Vector<Long>();
        for (int i = 0; i < _num_nodes; i++) {
            d.add(0l);
        }
        Vector<Integer> prev = new Vector<Integer>();
        for (int i = 0; i < _num_nodes; i++) {
            prev.add(0);
        }
        delta = 1;

        while (true) { // until we break when S or T is empty

            long maxSupply = 0;
            int k = 0;
            for (int i = 0; i < _num_nodes; ++i) {
                if (e.get(i) > 0) {
                    if (maxSupply < e.get(i)) {
                        maxSupply = e.get(i);
                        k = i;
                    }
                }
            }
            if (maxSupply == 0)
                break;
            delta = maxSupply;

            int[] l = { 0 };

            compute_shortest_path(d, prev, k, r_cost_forward,
                    r_cost_cap_backward, e, l);

            // ---------------------------------------------------------------
            // find delta (minimum on the path from k to l)
            // delta = e[k];
            // if (-e[l] < delta) delta = e[k];
            int to = l[0];
            do {
                int from = prev.get(to);
                assert (from != to);

                // residual
                EdgeLong2 itccb = null;
                for (EdgeLong2 it : r_cost_cap_backward.get(from)) {
                    if (it._to == to) {
                        itccb = it;
                        break;
                    }
                }

                if (itccb != null) {
                    if (itccb._residual_capacity < delta)
                        delta = itccb._residual_capacity;
                }

                to = from;
            } while (to != k);
            // ---------------------------------------------------------------

            // ---------------------------------------------------------------
            // augment delta flow from k to l (backwards actually...)
            to = l[0];
            do {
                int from = prev.get(to);
                assert (from != to);

                // TODO - might do here O(n) can be done in O(1)
                EdgeLong0 itx = null;
                for (EdgeLong0 it : x.get(from)) {
                    itx = it;
                    if (itx._to == to) {
                        break;
                    }
                }
                itx._flow += delta;

                // update residual for backward edges
                EdgeLong2 itccb = null;
                for (EdgeLong2 it : r_cost_cap_backward.get(to)) {
                    if (it._to != from) {
                        itccb = it;
                        break;
                    }
                }
                if (itccb != null) {
                    itccb._residual_capacity += delta;
                }

                itccb = null;
                for (EdgeLong2 it : r_cost_cap_backward.get(from)) {
                    if (it._to != to) {
                        itccb = it;
                        break;
                    }
                }
                if (itccb != null) {
                    itccb._residual_capacity -= delta;
                }

                // update e
                e.set(to, e.get(to) + delta);
                e.set(from, e.get(from) - delta);

                to = from;
            } while (to != k);
            // ---------------------------------------------------------------------------------
        }
        // compute distance from x
        long dist = 0;
        for (int from = 0; from < _num_nodes; ++from) {
            for (EdgeLong0 it : x.get(from)) {
                dist += (it._cost * it._flow);
            } // it
        } // from

        return dist;
    }

    private void compute_shortest_path(Vector<Long> d, Vector<Integer> prev,
            int from, Vector<List<EdgeLong1>> cost_forward,
            Vector<List<EdgeLong2>> cost_backward, Vector<Long> e, int[] l) {

        // ----------------------------------------------------------------
        // Making heap (all inf except 0, so we are saving comparisons...)
        // ----------------------------------------------------------------
        Stack<EdgeLong3> Q = new Stack<EdgeLong3>();
        for (int i = 0; i < _num_nodes; i++) {
            Q.push(new EdgeLong3());
        }

        Q.get(0)._to = from;
        _nodes_to_Q.set(from, 0);
        Q.get(0)._dist = 0;

        int j = 1;
        for (int i = 0; i < from; ++i) {
            Q.get(j)._to = i;
            _nodes_to_Q.set(i, j);
            // Q[j]._dist= std::numeric_limits<NUM_T>::max();
            Q.get(j)._dist = Long.MAX_VALUE;
            ++j;
        }

        for (int i = from + 1; i < _num_nodes; ++i) {
            Q.get(j)._to = i;
            _nodes_to_Q.set(i, j);
            // Q[j]._dist= std::numeric_limits<NUM_T>::max();
            Q.get(j)._dist = Long.MAX_VALUE;
            ++j;
        }
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // main loop
        // ----------------------------------------------------------------
        Vector<Boolean> finalNodesFlg = new Vector<Boolean>();
        for (int i = 0; i < _num_nodes; i++) {
            finalNodesFlg.add(false);
        }
        do {
            int u = Q.get(0)._to;

            d.set(u, Q.get(0)._dist); // final distance
            finalNodesFlg.set(u, true);
            if (e.get(u) < 0) {
                l[0] = u;
                break;
            }

            heap_remove_first(Q, _nodes_to_Q);

            // neighbors of u
            for (EdgeLong1 it : cost_forward.get(u)) {
                assert (it._reduced_cost >= 0);
                long alt = d.get(u) + it._reduced_cost;
                int v = it._to;
                if ((_nodes_to_Q.get(v) < Q.size())
                        && (alt < Q.get(_nodes_to_Q.get(v))._dist)) {
                    heap_decrease_key(Q, _nodes_to_Q, v, alt);
                    prev.set(v, u);
                }
            } // it
            for (EdgeLong2 it : cost_backward.get(u)) {
                if (it._residual_capacity > 0) {
                    assert (it._reduced_cost >= 0);
                    long alt = d.get(u) + it._reduced_cost;
                    int v = it._to;
                    if ((_nodes_to_Q.get(v) < Q.size())
                            && (alt < Q.get(_nodes_to_Q.get(v))._dist)) {
                        heap_decrease_key(Q, _nodes_to_Q, v, alt);
                        prev.set(v, u);
                    }
                }
            } // it

        } while (!Q.isEmpty());

        for (int f = 0; f < _num_nodes; ++f) {
            for (EdgeLong1 it : cost_forward.get(f)) {
                if (finalNodesFlg.get(f)) {
                    it._reduced_cost += d.get(f) - d.get(l[0]);
                }
                if (finalNodesFlg.get(it._to)) {
                    it._reduced_cost -= d.get(it._to) - d.get(l[0]);
                }
            }
        }

        // reduced costs and capacity for backward edges (c[j,i]-pi[j]+pi[i])
        for (int f = 0; f < _num_nodes; ++f) {
            for (EdgeLong2 it : cost_backward.get(f)) {
                if (finalNodesFlg.get(f)) {
                    it._reduced_cost += d.get(f) - d.get(l[0]);
                }
                if (finalNodesFlg.get(it._to)) {
                    it._reduced_cost -= d.get(it._to) - d.get(l[0]);
                }

            }// it
        }
        // ---------------------------------------------------------------------------------
        // tmp_tic_toc.toc();

        // ----------------------------------------------------------------

    } // compute_shortest_path

    void heap_decrease_key(Vector<EdgeLong3> Q, Vector<Integer> nodes_to_Q,
            int v, long alt) {
        int i = nodes_to_Q.get(v);
        Q.get(i)._dist = alt;
        while (i > 0 && Q.get(PARENT(i))._dist > Q.get(i)._dist) {
            swap_heap(Q, nodes_to_Q, i, PARENT(i));
            i = PARENT(i);
        }
    } // heap_decrease_key

    void heap_remove_first(Stack<EdgeLong3> Q, Vector<Integer> nodes_to_Q) {
        swap_heap(Q, nodes_to_Q, 0, Q.size() - 1);
        Q.pop();
        heapify(Q, nodes_to_Q, 0);
    } // heap_remove_first

    void heapify(Vector<EdgeLong3> Q, Vector<Integer> nodes_to_Q, int i) {
        do {
            // TODO: change to loop
            int l = LEFT(i);
            int r = RIGHT(i);
            int smallest;
            if ((l < Q.size()) && (Q.get(l)._dist < Q.get(i)._dist)) {
                smallest = l;
            } else {
                smallest = i;
            }
            if ((r < Q.size()) && (Q.get(r)._dist < Q.get(smallest)._dist)) {
                smallest = r;
            }

            if (smallest == i)
                return;

            swap_heap(Q, nodes_to_Q, i, smallest);
            i = smallest;

        } while (true);

    } // end heapify

    void swap_heap(Vector<EdgeLong3> Q, Vector<Integer> nodes_to_Q, int i, int j) {
        EdgeLong3 tmp = Q.get(i);
        Q.set(i, Q.get(j));
        Q.set(j, tmp);
        nodes_to_Q.set(Q.get(j)._to, j);
        nodes_to_Q.set(Q.get(i)._to, i);
    } // swap_heapify

    int LEFT(int i) {
        return 2 * (i + 1) - 1;
    }

    int RIGHT(int i) {
        return 2 * (i + 1); // 2*(i+1)+1-1
    }

    int PARENT(int i) {
        return (i - 1) / 2;
    }

} // end min_cost_flow

// Copyright (c) 2009-2011, Ofir Pele
// All rights reserved.

// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
// * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// * Neither the name of the The Hebrew University of Jerusalem nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.

// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
// IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
// THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
// PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
// LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

