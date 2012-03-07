package com.telmomenezes.synthetic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Extension of the Matrix class to provide operations on network adjacency
 * matrices.
 * 
 * This class implements the network analysis methods proposed by Dr. Joseph 
 * Johnson in the following papers:
 * 
 * Networks, Markov Lie Monoids, and Generalized Entropy, Computer Networks
 * Security - Third International Workshop on Mathematical Methods, Models, and
 * Architectures for Computer Network Security, St. Petersburg, Russia,
 * September 2005, Proceedings, 129-135
 * 
 * Markov-Type Lie Groups in GL(n,R) J. Math Phys. 26 (2) 252-257 February 1985
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class NetMatrix extends Matrix {
	
	public NetMatrix(int rows, int columns) {
		super(rows, columns);
	}
	
	
	public NetMatrix(NetMatrix matrix) {	
    	super(matrix);
    }
	
	
	public void markovGenerator() {
		
		// fill diagonal with negative column sums
		for (int i = 0; i < columns; i++) {
			double sum = 0;
			for (int j = 0; j < rows; j++) {
				if (i != j) {
					sum += data[j][i];
				}
			}
			data[i][i] = -sum;
		}
		
		// normalize
		double factor = -trace();
		factor *= rows;
		factor = 1 / factor;
		mul(factor);
	}
	
	
	public NetMatrix markovMatrix(int order, NetMatrix target) {
		NetMatrix t;
    	if (target == null) {
    		t = new NetMatrix(rows, columns);
    	}
    	else {
    		t = target;
    		t.zero();
    	}
    	
    	markovGenerator();
    	//mul(1);
    	NetMatrix m = new NetMatrix(rows, columns);
    	exp(m, 1);
    	m.pow(order, t);
    	
    	return t;
	}
	
	
	public double[] columnEntropy() {
		double[] e = new double[columns];
		
		double log2 = Math.log(2);
		
		for (int c = 0; c < columns; c++){
			for (int r = 0; r < rows; r++) {
				if (data[r][c] > 0)
					e[c] -= data[r][c] * (Math.log(data[r][c]) / log2);
			}
		}
		
		return e;
	}
	
	
	public void writeDegreeTable(String filePath)
	{
		try {
			FileWriter outFile = new FileWriter(filePath);
			PrintWriter out = new PrintWriter(outFile);
			
			out.println("node,indegree,outdegree");
			
			// could be rows, an adjacency matrix is square
			int nodeCount = columns;
			
			for (int node = 0; node < nodeCount; node++) {
				int indegree = 0;
				int outdegree = 0;
				
				for (int i = 0; i < nodeCount; i++) {
					if (node != i) {
						if (data[i][node] > 0)
							indegree ++;
						if (data[node][i] > 0)
							outdegree ++;
					}
				}
				
				String line = node + "," + indegree + "," + outdegree;
				out.println(line);
			}
			
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}