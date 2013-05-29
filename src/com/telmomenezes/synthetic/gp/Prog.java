package com.telmomenezes.synthetic.gp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.telmomenezes.synthetic.random.RandomGenerator;


public class Prog {

	public double[] vars;
    public GPNode root;
    private int varcount;
    
    private int parsePos;
    
    private Vector<String> variableNames;
    private Map<String, Integer> variableIndices;
    
    
	public Prog(int varcount, Vector<String> variableNames) {
		this.varcount = varcount;
		vars = new double[varcount];
		root = null;
		
		this.variableNames = variableNames;
    	// initialize variable indices table
    	variableIndices = new HashMap<String, Integer>();
    	for (int i = 0; i < variableNames.size(); i++) {
    	    variableIndices.put(variableNames.get(i), i);
    	}
	}


	public double eval() {
		GPNode curnode = root;
		curnode.curpos = -1;
		double val = 0;
		
		while (curnode != null) {
			curnode.curpos++;
			if (curnode.curpos < curnode.stoppos) {
				if (curnode.curpos == curnode.condpos) {
					switch(curnode.fun) {
					case GPFun.EQ:
						if (curnode.params[0].curval == curnode.params[1].curval) {
							curnode.stoppos = 3;
						}
						else {
							curnode.stoppos = 4;
							curnode.curpos++;
						}
						break;
					case GPFun.GRT:
						if (curnode.params[0].curval > curnode.params[1].curval) {
							curnode.stoppos = 3;
						}
						else {
							curnode.stoppos = 4;
							curnode.curpos++;
						}
						break;
					case GPFun.LRT:
                        if (curnode.params[0].curval < curnode.params[1].curval) {
                            curnode.stoppos = 3;
                        }
                        else {
                            curnode.stoppos = 4;
                            curnode.curpos++;
                        }
                        break;
					case GPFun.ZER:
						if (curnode.params[0].curval == 0) {
							curnode.stoppos = 2;
						}
						else {
							curnode.stoppos = 3;
							curnode.curpos++;
						}
						break;
					case GPFun.AFF:
						long g = Math.round(curnode.params[0].curval);
						long id1 = Math.round(vars[0]);
						long id2 = Math.round(vars[1]);
						if ((g == 0) || ((id1 % g) == (id2 % g))) {
							curnode.stoppos = 2;
						}
						else {
							curnode.stoppos = 3;
							curnode.curpos++;
						}
						break;
					default:
						break;
					}

					// update branching info
					if (curnode.branching < 0) {
						curnode.branching = curnode.stoppos;
					}
					else if (curnode.branching != curnode.stoppos) {
						curnode.branching = 0;
					}
				}

				curnode = curnode.params[curnode.curpos];
				curnode.curpos = -1;
			}
			else {
				switch (curnode.type) {
				case FUN:
					switch(curnode.fun) {
					case GPFun.SUM:
						val = curnode.params[0].curval + curnode.params[1].curval;
						break;
					case GPFun.SUB:
						val = curnode.params[0].curval - curnode.params[1].curval;
						break;
					case GPFun.MUL:
						val = curnode.params[0].curval * curnode.params[1].curval;
						break;
					case GPFun.DIV:
						if (curnode.params[1].curval == 0)
							val = 0;
						else
							val = curnode.params[0].curval / curnode.params[1].curval;
						break;
					case GPFun.MIN:
						val = curnode.params[0].curval;
                        if (curnode.params[1].curval < val) {
                            val = curnode.params[1].curval;
                        }
                        break;
					case GPFun.MAX:
                        val = curnode.params[0].curval;
                        if (curnode.params[1].curval > val) {
                            val = curnode.params[1].curval;
                        }
                        break;
					case GPFun.EXP:
                        val = Math.exp(curnode.params[0].curval);
                        break;
					case GPFun.LOG:
						if (curnode.params[0].curval == 0) {
							val = 0;
						}
						else {
							val = Math.log(curnode.params[0].curval);
						}
                        break;
					case GPFun.ABS:
                        val = Math.abs(curnode.params[0].curval);
                        break;
					case GPFun.POW:
                        val = Math.pow(curnode.params[0].curval, curnode.params[1].curval);
                        break;
					case GPFun.EQ:
					case GPFun.GRT:
					case GPFun.LRT:
					case GPFun.ZER:
					case GPFun.AFF:
						val = curnode.params[curnode.stoppos - 1].curval;
						break;
					// this should not happen
					default:
						break;
					}
					break;
				case VAR:
					val = vars[curnode.var];
					break;
				case VAL:
					val = curnode.val;
					break;
				}

				// update dynamic status
				switch (curnode.dynStatus) {
				case UNUSED:
					curnode.dynStatus = GPNodeDynStatus.CONSTANT;
					break;
				case CONSTANT:
					if (curnode.curval != val)
						curnode.dynStatus = GPNodeDynStatus.DYNAMIC;
						break;
				default:
					break;
				}
				
				// update and move to next node
				curnode.curval = val;
				curnode = curnode.parent;
			}
		}

		return val;
	}


	private void write2(GPNode node, int indent, OutputStreamWriter out) throws IOException {
		int ind = indent;

		if (node.arity > 0) {
			if (node.parent != null)
				out.write("\n");
			for (int i = 0; i < indent; i++)
				out.write(" ");
			out.write("(");
			ind++;
		}

		node.write(out);

		for (int i = 0; i < node.arity; i++) {
			out.write(" ");
			write2(node.params[i], ind, out);
		}

		if (node.arity > 0) {
			out.write(")");
			ind--;
		}
	}


	public void write(OutputStreamWriter out) throws IOException {
		write2(root, 0, out);
		out.write("\n");
	}
	
	
	public void write(String filePath) {
        try {
            FileOutputStream fstream = new FileOutputStream(filePath);
            OutputStreamWriter out = new OutputStreamWriter(fstream);
            write(out);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void print() {
        try {
            write(new OutputStreamWriter(System.out));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void load(String filePath) throws IOException {
        FileInputStream fstream = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        String line = br.readLine();
    	 
    	while ((line.equals(""))
    			|| (line.charAt(0) == '\n')
    			|| (line.charAt(0) == '#'))
    		line = br.readLine();
        
    	String prog = "";
    	while ((line != null)
    			&& (line.charAt(0) != '\n')
    			&& (line.charAt(0) != '#')) {
    		prog += line;
    		line = br.readLine();
    	}

    	parse(prog);
    		
    	in.close();
    }


	private GPNode initRandom2(double probTerm,
						GPNode parent,
						int maxDepth,
						boolean grow,
						int depth) {
		GPNode node;
		double p = RandomGenerator.random.nextDouble();
		if (((!grow) || (p > probTerm)) && (depth < maxDepth)) {
			int fun = RandomGenerator.random.nextInt(GPFun.FUN_COUNT);
			node = new GPNode(this);
			node.initFun(fun, parent);
			for (int i = 0; i < node.arity; i++)
				node.params[i] = initRandom2(probTerm, node, maxDepth, grow, depth + 1);
		}
		else {
			if (RandomGenerator.random.nextBoolean() && (varcount > 0)) {
				int var = RandomGenerator.random.nextInt(varcount);
				node = new GPNode(this);
				node.initVar(var, parent);
			}
			else {
				double val;
				int r = RandomGenerator.random.nextInt(10);
				if (r == 0) {
				    val = 0.0;
				}
				else if (r > 5) {
				    val = RandomGenerator.random.nextInt(10);
				}
				else {
				    val = RandomGenerator.random.nextDouble();
				}
				
				node = new GPNode(this);
				node.initVal(val, parent);
			}
		}

		return node;
	}


	public void initRandom(double probTerm,
					int maxDepthLowLimit,
					int maxDepthHighLimit) {
		boolean grow = RandomGenerator.random.nextBoolean();
		int max_depth = maxDepthLowLimit +(RandomGenerator.random.nextInt(maxDepthHighLimit - maxDepthLowLimit));

		root = initRandom2(probTerm, null, max_depth, grow, 0);
	}

	
	public void initRandom() {
		initRandom(0.4, 2, 5);
    }

	
	private GPNode cloneGPNode(GPNode node, GPNode parent) {
		GPNode cnode = new GPNode(this);
		switch (node.type) {
		case VAL:
			cnode.initVal(node.val, parent);
			break;
		case VAR:
			cnode.initVar(node.var, parent);
			break;
		default:
			cnode.initFun(node.fun, parent);
			break;
		}
		cnode.curval = node.curval;
		cnode.branching = node.branching;
		cnode.dynStatus = node.dynStatus;

		for (int i = 0; i < node.arity; i++)
			cnode.params[i] = cloneGPNode(node.params[i], cnode);
			return cnode;
	}


	public Prog clone() {
		Prog ctree = new Prog(varcount, variableNames);
		ctree.root = cloneGPNode(root, null);
		return ctree;
	}


	private int size2(GPNode node) {
		int c = 1;
		for (int i = 0; i < node.arity; i++)
			c += size2(node.params[i]);
		return c;
	}


	public int size() {
		return size2(root);
	}


	private GPNode GPNodeByPos2(GPNode node,
						int pos,
						int[] curpos) {
		GPNode nodefound;

		if (pos == curpos[0])
			return node;
		
		curpos[0]++;
		for (int i = 0; i < node.arity; i++) {
			nodefound = GPNodeByPos2(node.params[i], pos, curpos);
			if (nodefound != null)
				return nodefound;
		}

		return null;
	}


	public GPNode GPNodeByPos(int pos) {
		int[] curpos;
		curpos = new int[1];
		curpos[0] = 0;
		return GPNodeByPos2(root, pos, curpos);
	}


	public Prog recombine(Prog parent2) {
		Prog parentA = null;
		Prog parentB = null;
		
		if ((RandomGenerator.random.nextInt() % 2) == 0) {
			parentA = parent2.clone();
			parentB = clone();
		}
		else {
			parentB = parent2.clone();
			parentA = clone();
		}
		
		Prog child = parentA.clone();
		int size1 = parentA.size();
		int size2 = parentB.size();
		int pos1 = RandomGenerator.random.nextInt(size1);
		int pos2 = RandomGenerator.random.nextInt(size2);

		GPNode point1 = child.GPNodeByPos(pos1);
		GPNode point2 = parentB.GPNodeByPos(pos2);
		GPNode point1parent = point1.parent;
		GPNode point2clone;

		int i;
		int parampos = 0;

		// remove sub-tree from child
		// find point1 position in it's parent's param array
		if (point1parent != null) {
			for (i = 0; i < point1parent.arity; i++) {
				if (point1parent.params[i] == point1) {
					parampos = i;
					break;
				}
			}
		}

		// copy sub-tree from parent 2 to parent 1
		point2clone = cloneGPNode(point2, point1parent);
		if (point1parent != null)
			point1parent.params[parampos] = point2clone;
		else
			child.root = point2clone;

		return child;
	}


	private int tokenEnd(String prog, int pos) {
		int curpos = pos;
		char curchar = prog.charAt(curpos);
		while ((curchar != ' ') 
				&& (curchar != '\n')
				&& (curchar != '\t')
				&& (curchar != '\r')
				&& (curchar != ')')
				&& (curchar != '(')
				&& (curchar != 0)) {

			curpos++;
			if (curpos >= prog.length())
				return curpos;
			curchar = prog.charAt(curpos);
		}
			
		return curpos;
	}


	private int tokenStart(String prog) {
		int curpos = parsePos;
		char curchar = prog.charAt(curpos);
		while ((curchar == ' ')
				|| (curchar == '\n')
				|| (curchar == '\t')
				|| (curchar == '\r')
				|| (curchar == ')')
				|| (curchar == '(')
				|| (curchar == 0))
			curchar = prog.charAt(++curpos);
		
		return curpos;
	}


	private GPNode parse2(String prog, GPNode parent) {
		int start = tokenStart(prog);
		int end = tokenEnd(prog, start);
		
		String token = prog.substring(start, end);
		
		GPNode node = new GPNode(this);

		try {
			double val = new Double(token);
			node.initVal(val, parent);
		}
		catch (Exception e) {
		
			if (token.charAt(0) == '$') {
				int var = variableIndices.get(token.substring(1));
				node.initVar(var, parent);
			}
			else {
				int fun = -1;
				if (token.equals("+"))
					fun = GPFun.SUM;
				else if (token.equals("-"))
					fun = GPFun.SUB;
				else if (token.equals("*"))
					fun = GPFun.MUL;
				else if (token.equals("/"))
					fun = GPFun.DIV;
				else if (token.equals("ZER"))
					fun = GPFun.ZER;
				else if (token.equals("=="))
					fun = GPFun.EQ;
				else if (token.equals(">"))
					fun = GPFun.GRT;
				else if (token.equals("<"))
                    fun = GPFun.LRT;
				else if (token.equals("EXP"))
                    fun = GPFun.EXP;
				else if (token.equals("LOG"))
                    fun = GPFun.LOG;
				else if (token.equals("ABS"))
                    fun = GPFun.ABS;
				else if (token.equals("MIN"))
                    fun = GPFun.MIN;
				else if (token.equals("MAX"))
                    fun = GPFun.MAX;
				else if (token.equals("AFF"))
                    fun = GPFun.AFF;
				else if (token.equals("^"))
                    fun = GPFun.POW;
			
				node.initFun(fun, parent);

				parsePos = end;
			
				for (int i = 0; i < node.arity; i++) {
					node.params[i] = parse2(prog, node);
				}

				return node;
			}
		}

		parsePos = end;
		return node;
	}


	public void parse(String prog) {
		parsePos = 0;
		root = parse2(prog, null);
	}
	

	private void clearBranching2(GPNode node) {
		node.branching = -1;
		for (int i = 0; i < node.arity; i++)
			clearBranching2(node.params[i]);
	}


	public void clearBranching() {
		clearBranching2(root);
	}
	

	public int branchingDistance(Prog tree) {
		return branchingDistance2(root, tree.root);
	}


	private int branchingDistance2(GPNode node1, GPNode node2) {
		int distance = 0;
		if (node1.branching != node2.branching)
			distance += 1;
		for (int i = 0; i < node1.arity; i++)
			distance += branchingDistance2(node1.params[i], node2.params[i]);
		return distance;
	}

	
	public boolean compareBranching(Prog tree) {
		return (branchingDistance(tree) == 0);
    }
	

	private void moveUp(GPNode origNode, GPNode targNode) {
		switch (origNode.type) {
		case VAL:
			targNode.initVal(origNode.val, origNode.parent);
			break;
		case VAR:
			targNode.initVar(origNode.var, origNode.parent);
			break;
		default:
			targNode.initFun(origNode.fun, origNode.parent);
			break;
		}
		targNode.branching = origNode.branching;
		targNode.dynStatus = origNode.dynStatus;

		for (int i = 0; i < origNode.arity; i++) {
			targNode.params[i] = origNode.params[i];
			targNode.params[i].parent = targNode;
		}
	}


	public void dynPruning() {
		dynPruning2(root);
	}


	private void dynPruning2(GPNode node) {
		// nodes with constant value
		if (node.dynStatus == GPNodeDynStatus.CONSTANT) {
			node.initVal(node.curval, node.parent);
		}

		// conditions with constant branching
		if (node.condpos > 0) {
			GPNode branch1 = node.params[node.condpos];
			GPNode branch2 = node.params[node.condpos + 1];

			int branch = -1;

			if (branch1.dynStatus == GPNodeDynStatus.UNUSED)
				branch = node.condpos + 1;
			else if (branch2.dynStatus == GPNodeDynStatus.UNUSED)
				branch = node.condpos;

			if (branch > 0) {
				moveUp(node.params[branch], node);
			}
		}

		for (int i = 0; i < node.arity; i++)
			dynPruning2(node.params[i]);
	}
	
	
	public Vector<String> getVariableNames() {
        return variableNames;
    }
}