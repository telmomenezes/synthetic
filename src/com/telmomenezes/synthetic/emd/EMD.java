/**
 * This class computes the Earth Mover's Distance, using the EMD-HAT algorithm
 * created by Ofir Pele and Michael Werman.
 * 
 * This implementation is strongly based on the C++ code by the same authors,
 * that can be found here:
 * http://www.cs.huji.ac.il/~ofirpele/FastEMD/code/
 * 
 * Some of the author's comments on the original code that we deemed relevant
 * were kept or edited for this context.
 */

package com.telmomenezes.synthetic.emd;

import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;


/**
 * This interface is similar to Rubner's interface. See:
 * http://www.cs.duke.edu/~tomasi/software/emd.htm

 * To get the same results as Rubner's code you should set extra_mass_penalty to 0,
 * and divide by the minimum of the sum of the two signature's weights. However, I
 * suggest not to do this as you lose the metric property and more importantly, in my
 * experience the performance is better with emd_hat. for more on the difference
 * between emd and emd_hat, see the paper:
 * A Linear Time Histogram Metric for Improved SIFT Matching
 * Ofir Pele, Michael Werman
 * ECCV 2008
 *
 * To get shorter running time, set the ground distance function to
 * be a thresholded distance. For example: min(L2, T). Where T is some threshold.
 * Note that the running time is shorter with smaller T values. Note also that
 * thresholding the distance will probably increase accuracy. Finally, a thresholded
 * metric is also a metric. See paper:
 * Fast and Robust Earth Mover's Distances
 * Ofir Pele, Michael Werman
 * ICCV 2009
 *
 * If you use this code, please cite the papers.
 */


public class EMD {
    
    /** Similar to Rubner's emd interface.
      * extra_mass_penalty - it's alpha*maxD_ij in the ECCV paper. If you want metric property
      *                      should be at least half the diameter of the space (maximum possible distance
      *                      between any two points). In Rubner's code this is implicitly 0.
      *                      Default value is -1 which means 1*max_distance_between_bins_of_signatures
      */
    public static double compute(Signature signature1, Signature signature2,
                                  double extraMassPenalty) {
    
        Vector<Double> P = new Vector<Double>();
        for (int i = 0; i < signature1.n + signature2.n; i++) {
            P.add(0.0);
        }
        Vector<Double> Q = new Vector<Double>(); 
        for (int i = 0; i < signature1.n + signature2.n; i++) {
            Q.add(0.0);
        }
        for (int i = 0; i < signature1.n; ++i) {
            P.set(i, signature1.Weights[i]);
        }
        for (int j = 0; j < signature2.n; ++j) {
            Q.set(j + signature1.n, signature2.Weights[j]);
        }
    
        Vector<Vector<Double>> C = new Vector<Vector<Double>>();
        for (int i = 0; i < P.size(); i++) {
            Vector<Double> vec = new Vector<Double>();
            for (int j = 0; j < P.size(); j++) {
                vec.add(0.0);
            }
            C.add(vec);
        }
    
        for (int i = 0; i < signature1.n; ++i) {
            for (int j = 0; j < signature2.n; ++j) {
                double dist = signature1.Features[i].groundDist(signature2.Features[j]);
                assert(dist >= 0);
                C.get(i).set(j + signature1.n, dist);
                C.get(j + signature1.n).set(i, dist);
            }
        }

        return emdhat(P, Q, C, extraMassPenalty);
    }
    
    private static double emdhat(Vector<Double> P, Vector<Double> Q,
            Vector<Vector<Double>> C, double extra_mass_penalty) {

        return emdhatDouble(P, Q, P, Q, C, extra_mass_penalty);
    }

    private static double emdhatDouble(Vector<Double> POrig,
            Vector<Double> QOrig, Vector<Double> P, Vector<Double> Q,
            Vector<Vector<Double>> C, double extra_mass_penalty) {

        // This condition should hold:
        // ( 2^(sizeof(long*8)) >= ( MULT_FACTOR^2 )
        // Note that it can be problematic to check it because
        // of overflow problems. I simply checked it with Linux calc
        // which has arbitrary precision.
        double MULT_FACTOR = 1000000;

        // Constructing the input
        int N = P.size();
        Vector<Long> iPOrig = new Vector<Long>();
        for (int i = 0; i < N; i++) {
            iPOrig.add(0l);
        }
        Vector<Long> iQOrig = new Vector<Long>();
        for (int i = 0; i < N; i++) {
            iQOrig.add(0l);
        }
        Vector<Long> iP = new Vector<Long>();
        for (int i = 0; i < N; i++) {
            iP.add(0l);
        }
        Vector<Long> iQ = new Vector<Long>();
        for (int i = 0; i < N; i++) {
            iQ.add(0l);
        }
        Vector<Vector<Long>> iC = new Vector<Vector<Long>>();
        for (int i = 0; i < N; i++) {
            Vector<Long> vec = new Vector<Long>();
            for (int j = 0; j < N; j++) {
                vec.add(0l);
            }
            iC.add(vec);
        }
        Vector<Vector<Long>> iF = new Vector<Vector<Long>>();
        for (int i = 0; i < N; i++) {
            Vector<Long> vec = new Vector<Long>();
            for (int j = 0; j < N; j++) {
                vec.add(0l);
            }
            iF.add(vec);
        }

        // Converting to long
        double sumP = 0.0;
        double sumQ = 0.0;
        double maxC = C.get(0).get(0);
        for (int i = 0; i < N; ++i) {
            sumP += POrig.get(i);
            sumQ += QOrig.get(i);
            for (int j = 0; j < N; ++j) {
                if (C.get(i).get(j) > maxC)
                    maxC = C.get(i).get(j);
            }
        }
        double minSum = Math.min(sumP, sumQ);
        double maxSum = Math.max(sumP, sumQ);
        double PQnormFactor = MULT_FACTOR / maxSum;
        double CnormFactor = MULT_FACTOR / maxC;
        for (int i = 0; i < N; ++i) {
            iPOrig.set(i,
                    (long)(Math.floor(POrig.get(i) * PQnormFactor + 0.5)));
            iQOrig.set(i,
                    (long)(Math.floor(QOrig.get(i) * PQnormFactor + 0.5)));
            iP.set(i, (long)(Math.floor(P.get(i) * PQnormFactor + 0.5)));
            iQ.set(i, (long)(Math.floor(Q.get(i) * PQnormFactor + 0.5)));
            for (int j = 0; j < N; ++j) {
                iC.get(i)
                  .set(j,
                          (long) (Math.floor(C.get(i).get(j)
                                  * CnormFactor + 0.5)));
            }
        }

        // computing distance without extra mass penalty
        double dist = emdhatLong(iPOrig, iQOrig, iP, iQ, iC, 0, iF);
        // unnormalize
        dist = dist / PQnormFactor;
        dist = dist / CnormFactor;

        // adding extra mass penalty
        if (extra_mass_penalty == -1)
            extra_mass_penalty = maxC;
        dist += (maxSum - minSum) * extra_mass_penalty;

        return dist;

    }
    
    private static long emdhatLong(Vector<Long> POrig, Vector<Long> QOrig,
            Vector<Long> Pc, Vector<Long> Qc, Vector<Vector<Long>> C,
            long extra_mass_penalty, Vector<Vector<Long>> F) {

        int N = Pc.size();
        assert (Qc.size() == N);

        // Ensuring that the supplier - P, have more mass.
        // Note that we assume here that C is symmetric
        Vector<Long> P;
        Vector<Long> Q;
        long abs_diff_sum_P_sum_Q;
        long sum_P = 0;
        long sum_Q = 0;
        for (int i = 0; i < N; ++i)
            sum_P += Pc.get(i);
        for (int i = 0; i < N; ++i)
            sum_Q += Qc.get(i);
        if (sum_Q > sum_P) {
            P = Qc;
            Q = Pc;
            abs_diff_sum_P_sum_Q = sum_Q - sum_P;
        } else {
            P = Pc;
            Q = Qc;
            abs_diff_sum_P_sum_Q = sum_P - sum_Q;
        }

        // creating the b vector that contains all vertices
        Vector<Long> b = new Vector<Long>();
        for (int i = 0; i < 2 * N + 2; i++) {
            b.add(0l);
        }
        int THRESHOLD_NODE = 2 * N;
        int ARTIFICIAL_NODE = 2 * N + 1; // need to be last !
        for (int i = 0; i < N; ++i) {
            b.set(i, P.get(i));
        }
        for (int i = N; i < 2 * N; ++i) {
            b.set(i, Q.get(i - N));
        }

        // I put here a deficit of the extra mass, as mass that flows
        // to the threshold node
        // can be absorbed from all sources with cost zero (this is in reverse
        // order from the paper,
        // where incoming edges to the threshold node had the cost of the
        // threshold and outgoing
        // edges had the cost of zero)
        // This also makes sum of b zero.
        b.set(THRESHOLD_NODE, -abs_diff_sum_P_sum_Q);
        b.set(ARTIFICIAL_NODE, 0l);

        long maxC = 0;
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                assert (C.get(i).get(j) >= 0);
                if (C.get(i).get(j) > maxC)
                    maxC = C.get(i).get(j);
            }
        }
        if (extra_mass_penalty == -1)
            extra_mass_penalty = maxC;

        Set<Integer> sources_that_flow_not_only_to_thresh = new HashSet<Integer>();
        Set<Integer> sinks_that_get_flow_not_only_from_thresh = new HashSet<Integer>();
        long pre_flow_cost = 0;

        // regular edges between sinks and sources without threshold edges
        Vector<List<EdgeLong>> c = new Vector<List<EdgeLong>>(b.size());
        for (int i = 0; i < b.size(); i++) {
            c.add(new LinkedList<EdgeLong>());
        }
        for (int i = 0; i < N; ++i) {
            if (b.get(i) == 0)
                continue;
            for (int j = 0; j < N; ++j) {
                if (b.get(j + N) == 0)
                    continue;
                if (C.get(i).get(j) == maxC)
                    continue;
                c.get(i).add(new EdgeLong(j + N, C.get(i).get(j)));
            }
        }

        // checking which are not isolated
        for (int i = 0; i < N; ++i) {
            if (b.get(i) == 0)
                continue;
            for (int j = 0; j < N; ++j) {
                if (b.get(j + N) == 0)
                    continue;
                if (C.get(i).get(j) == maxC)
                    continue;
                sources_that_flow_not_only_to_thresh.add(i);
                sinks_that_get_flow_not_only_from_thresh.add(j + N);
            }
        }

        // converting all sinks to negative
        for (int i = N; i < 2 * N; ++i) {
            b.set(i, -b.get(i));
        }

        // add edges from/to threshold node,
        // note that costs are reversed to the paper (see also remark* above)
        // It is important that it will be this way because of remark* above.
        for (int i = 0; i < N; ++i) {
            c.get(i).add(new EdgeLong(THRESHOLD_NODE, 0));
        }
        for (int j = 0; j < N; ++j) {
            c.get(THRESHOLD_NODE).add(new EdgeLong(j + N, maxC));
        }

        // artificial arcs - Note the restriction that only one edge i,j is
        // artificial so I ignore it...
        for (int i = 0; i < ARTIFICIAL_NODE; ++i) {
            c.get(i).add(new EdgeLong(ARTIFICIAL_NODE, maxC + 1));
            c.get(ARTIFICIAL_NODE).add(new EdgeLong(i, maxC + 1));
        }

        // remove nodes with supply demand of 0
        // and vertexes that are connected only to the
        // threshold vertex
        int current_node_name = 0;
        // Note here it should be vector<int> and not vector<NODE_T>
        // as I'm using -1 as a special flag !!!
        int REMOVE_NODE_FLAG = -1;
        Vector<Integer> nodes_new_names = new Vector<Integer>();
        for (int i = 0; i < b.size(); i++) {
            nodes_new_names.add(REMOVE_NODE_FLAG);
        }
        Vector<Integer> nodes_old_names = new Vector<Integer>();
        for (int i = 0; i < N * 2; ++i) {
            if (b.get(i) != 0) {
                if (sources_that_flow_not_only_to_thresh.contains(i)
                        || sinks_that_get_flow_not_only_from_thresh.contains(i)) {
                    nodes_new_names.set(i, current_node_name);
                    nodes_old_names.add(i);
                    ++current_node_name;
                } else {
                    if (i >= N) { // sink
                        pre_flow_cost -= b.get(i) * maxC;
                    }
                    b.set(THRESHOLD_NODE, b.get(THRESHOLD_NODE) + b.get(i)); // add
                                                                             // mass(i<N)
                                                                             // or
                                                                             // deficit
                                                                             // (i>=N)
                }
            }
        }
        nodes_new_names.set(THRESHOLD_NODE, current_node_name);
        nodes_old_names.add(THRESHOLD_NODE);
        ++current_node_name;
        nodes_new_names.set(ARTIFICIAL_NODE, current_node_name);
        nodes_old_names.add(ARTIFICIAL_NODE);
        ++current_node_name;

        Vector<Long> bb = new Vector<Long>();
        for (int i = 0; i < b.size(); ++i) {
            if (nodes_new_names.get(i) != REMOVE_NODE_FLAG) {
                bb.add(b.get(i));
            }
        }

        Vector<List<EdgeLong>> cc = new Vector<List<EdgeLong>>();
        for (int i = 0; i < bb.size(); i++) {
            cc.add(new LinkedList<EdgeLong>());
        }
        for (int i = 0; i < c.size(); ++i) {
            if (nodes_new_names.get(i) == REMOVE_NODE_FLAG)
                continue;
            for (EdgeLong it : c.get(i)) {
                if (nodes_new_names.get(it._to) != REMOVE_NODE_FLAG) {
                    cc.get(nodes_new_names.get(i))
                            .add(new EdgeLong(nodes_new_names.get(it._to),
                                    it._cost));
                }
            }
        }

        MinCostFlow mcf = new MinCostFlow();

        long my_dist;

        Vector<List<EdgeLong0>> flows = new Vector<List<EdgeLong0>>();
        for (int i = 0; i < bb.size(); i++) {
            flows.add(new LinkedList<EdgeLong0>());
        }

        long mcf_dist = mcf.operator(bb, cc, flows);

        my_dist = pre_flow_cost + // pre-flowing on cases where it was possible
                mcf_dist + // solution of the transportation problem
                (abs_diff_sum_P_sum_Q * extra_mass_penalty); // emd-hat extra
                                                             // mass penalty

        return my_dist;
    }
}