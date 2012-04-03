package com.telmomenezes.synthetic.kinship;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.io.MatrixFile;


public class TopologicalIndices {
    private double[][] matrix;
    private int m;
    private double n;
    
    // topological indices
    private double endogamousPercentage;
    private double networkConcentration;
    private double endogamicNetworkConcentration;
    private double networkSymmetry;
    
    public TopologicalIndices(Net net) {
        m = net.getNodeCount();
        n = 0;
            
        matrix = new double[m][m];
        
        // extract matrix from net
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                Edge edge = net.getEdge(net.getNodes().get(i), net.getNodes().get(j));
                double x = 0;
                if (edge != null) {
                    x = edge.getWeight();
                    n += x;
                }
                matrix[i][j] = x;
            }
        }
        
        // normalize matrix
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] /= n;
            }
        }
        
        // calc topological indices
        calcEndogamousPercentage();
        calcNetworkConcentration();
        calcEndogamicNetworkConcentration();
        calcNetworkSymmetry();
        
        // release matrix
        matrix = null;
    }
    
    private void calcEndogamousPercentage() {
        double sum = 0;
        for (int i = 0; i < m; i++) {
            sum += matrix[i][i];
        }
        
        endogamousPercentage = sum / n;
    }
    
    private void calcNetworkConcentration() {
        networkConcentration = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                networkConcentration += matrix[i][j] * matrix[i][j];
            }
        }
    }
    
    private void calcEndogamicNetworkConcentration() {
        endogamicNetworkConcentration = 0;
        
        double endogamicN = endogamousPercentage * n;
        for (int i = 0; i < m; i++) {
            double x = (matrix[i][i] * n) / endogamicN;
            endogamicNetworkConcentration += x * x;
        }
        
        for (int i = 0; i < m; i++) {
            double x = matrix[i][i];
            endogamicNetworkConcentration += x * x;
        }
        
        endogamicNetworkConcentration /= endogamousPercentage * endogamousPercentage;
    }
    
    private void calcNetworkSymmetry() {
        networkSymmetry = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                networkSymmetry += matrix[i][j] * matrix[j][i];
            }
        }
        networkSymmetry /= networkConcentration;
    }

    public double getEndogamousPercentage() {
        return endogamousPercentage;
    }

    public double getNetworkConcentration() {
        return networkConcentration;
    }

    public double getEndogamicNetworkConcentration() {
        return endogamicNetworkConcentration;
    }

    public double getNetworkSymmetry() {
        return networkSymmetry;
    }

    @Override
    public String toString() {
        return "TopologicalIndices [m=" + m + ", n=" + n
                + ", endogamousPercentage=" + endogamousPercentage
                + ", networkConcentration=" + networkConcentration
                + ", endogamicNetworkConcentration="
                + endogamicNetworkConcentration + ", networkSymmetry="
                + networkSymmetry + "]";
    }
    
    public static void main(String[] args) {
        MatrixFile mf = new MatrixFile();
        Net net = mf.load("alliance_nets/Chimane_AGNATES.csv");
        TopologicalIndices ti = new TopologicalIndices(net);
        System.out.println(ti);
    }
}