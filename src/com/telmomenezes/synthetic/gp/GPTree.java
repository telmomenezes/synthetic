package com.telmomenezes.synthetic.gp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import com.telmomenezes.synthetic.RandomGenerator;


/**
 * Genetic program represented as a tree of nodes.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class GPTree {

	public double[] vars;
    private GPNode root;
    private int varcount;
    private Vector<Integer> funset;
    private GPExtraFuns extraFuns;
    
    private int parsePos;
    
    
	public GPTree(int varcount, Vector<Integer> funset, GPExtraFuns extraFuns)
	{
		this.varcount = varcount;
		this.funset = funset;
		vars = new double[varcount];
		root = null;
		this.extraFuns = extraFuns;
	}


	private void destroyGPNode(GPNode node)
	{
		for (int i = 0; i < node.arity; i++)
			destroyGPNode(node.params[i]);
		GPMemPool.instance().returnNode(node);
	}


	public double eval()
	{
		GPNode curnode = root;
		curnode.curpos = -1;
		double val = 0;

		while (curnode != null) {
			curnode.curpos++;
			if (curnode.curpos < curnode.stoppos) {
				if (curnode.curpos == curnode.condpos) {
					switch(curnode.fun) {
					case GPFun.EQ:
						if (curnode.params[0].val == curnode.params[1].curval)
							curnode.stoppos = 3;
							else {
								curnode.stoppos = 4;
								curnode.curpos++;
							}
						break;
					case GPFun.GRT:
						if (curnode.params[0].curval > curnode.params[1].curval)
							curnode.stoppos = 3;
							else {
								curnode.stoppos = 4;
								curnode.curpos++;
							}
						break;
					case GPFun.GE:
						if (curnode.params[0].curval >= curnode.params[1].curval)
							curnode.stoppos = 3;
							else {
								curnode.stoppos = 4;
								curnode.curpos++;
							}
						break;
					case GPFun.ZER:
						if (curnode.params[0].curval == 0)
							curnode.stoppos = 2;
							else {
								curnode.stoppos = 3;
								curnode.curpos++;
							}
						break;
					default:
						break;
					}

					// update branching info
					if (curnode.branching < 0)
						curnode.branching = curnode.stoppos;
					else if (curnode.branching != curnode.stoppos)
							curnode.branching = 0;
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
					case GPFun.EQ:
					case GPFun.GRT:
					case GPFun.GE:
					case GPFun.ZER:
						val = curnode.params[curnode.stoppos - 1].curval;
						break;
					case GPFun.RAND_UNIF:
						val = RandomGenerator.instance().random.nextFloat();
						break;
					case GPFun.RAND_NORM:
						val = RandomGenerator.instance().random.nextGaussian();
						break;
					// This is an extra function
					default:
						val = extraFuns.eval(this, curnode);
					}
					break;
				case VAR:
					val = vars[curnode.var];
					break;
				case VAL:
					val = curnode.val;
					break;
				}

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
				curnode.curval = val;
				curnode = curnode.parent;
			}
		}

		return val;
	}


	private void write2(GPNode node, int indent, OutputStreamWriter out) throws IOException
	{
		int ind = indent;

		if (node.arity > 0) {
			if (node.parent != null)
				out.write("\n");
			for (int i = 0; i < indent; i++)
				out.write(" ");
			out.write("(");
			ind++;
		}

		node.write(out, extraFuns);

		for (int i = 0; i < node.arity; i++) {
			out.write(" ");
			write2(node.params[i], ind, out);
		}

		if (node.arity > 0) {
			out.write(")");
			ind--;
		}
	}


	public void write(OutputStreamWriter out) throws IOException
	{
		write2(root, 0, out);
		out.write("\n");
	}


	private GPNode initRandom2(double probTerm,
						GPNode parent,
						int maxDepth,
						boolean grow,
						int depth)
	{
		GPNode node;
		double p = RandomGenerator.instance().random.nextDouble();
		if (((!grow) || (p > probTerm)) && (depth < maxDepth)) {
			int fun;
			int pos = RandomGenerator.instance().random.nextInt(funset.size());
			fun = funset.get(pos);
			node = GPMemPool.instance().getNode();
			node.initFun(fun, parent, extraFuns);
			for (int i = 0; i < node.arity; i++)
				node.params[i] = initRandom2(probTerm, node, maxDepth, grow, depth + 1);
		}
		else {
			if (RandomGenerator.instance().random.nextBoolean() && (varcount > 0)) {
				int var = RandomGenerator.instance().random.nextInt(varcount);
				node = GPMemPool.instance().getNode();
				node.initVar(var, parent);
			}
			else {
				double val;
				int r = RandomGenerator.instance().random.nextInt(10);
				switch (r) {
				case 0:
					val = 0.0;
					break;
				default:
					val = RandomGenerator.instance().random.nextDouble();
					break;
				}
				node = GPMemPool.instance().getNode();
				node.initVal(val, parent);
			}
		}

		return node;
	}


	public void initRandom(double probTerm,
					int maxDepthLowLimit,
					int maxDepthHighLimit)
	{
		boolean grow = RandomGenerator.instance().random.nextBoolean();
		int max_depth = maxDepthLowLimit +(RandomGenerator.instance().random.nextInt(maxDepthHighLimit - maxDepthLowLimit));

		root = initRandom2(probTerm, null, max_depth, grow, 0);
	}


	private GPNode cloneGPNode(GPNode node, GPNode parent)
	{
		GPNode cnode = GPMemPool.instance().getNode();
		switch (node.type) {
		case VAL:
			cnode.initVal(node.val, parent);
			break;
		case VAR:
			cnode.initVar(node.var, parent);
			break;
		default:
			cnode.initFun(node.fun, parent, extraFuns);
			break;
		}
		cnode.curval = node.curval;
		cnode.branching = node.branching;
		cnode.dynStatus = node.dynStatus;

		for (int i = 0; i < node.arity; i++)
			cnode.params[i] = cloneGPNode(node.params[i], cnode);
			return cnode;
	}


	public GPTree clone()
	{
		GPTree ctree = new GPTree(varcount, funset, extraFuns);
		ctree.root = cloneGPNode(root, null);
		return ctree;
	}


	private int size2(GPNode node)
	{
		int c = 1;
		for (int i = 0; i < node.arity; i++)
			c += size2(node.params[i]);
		return c;
	}


	public int size()
	{
		return size2(root);
	}


	private GPNode GPNodeByPos2(GPNode node,
						int pos,
						int[] curpos)
	{
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


	public GPNode GPNodeByPos(int pos)
	{
		int[] curpos;
		curpos = new int[1];
		curpos[0] = 0;
		return GPNodeByPos2(root, pos, curpos);
	}


	public GPTree recombine(GPTree parent2)
	{
		GPTree child = clone();
		int size1 = size();
		int size2 = parent2.size();
		int pos1 = RandomGenerator.instance().random.nextInt(size1);
		int pos2 = RandomGenerator.instance().random.nextInt(size2);

		GPNode point1 = child.GPNodeByPos(pos1);
		GPNode point2 = parent2.GPNodeByPos(pos2);
		GPNode point1parent = point1.parent;
		GPNode point2clone;

		int i;
		int parampos = 0;

		// remove sub-tree from child
		// find point1 position in it's parent's param array
		if (point1parent != null)
			for (i = 0; i < point1parent.arity; i++) {
				if (point1parent.params[i] == point1) {
					parampos = i;
					break;
				}
			}

		destroyGPNode(point1);

		// copy sub-tree from parent 2 to parent 1
		point2clone = cloneGPNode(point2, point1parent);
		if (point1parent != null)
			point1parent.params[parampos] = point2clone;
		else
			child.root = point2clone;

		return child;
	}


	private int tokenEnd(String prog, int pos)
	{
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


	private int tokenStart(String prog)
	{
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


	private GPNode parse2(String prog, GPNode parent)
	{
		int start = tokenStart(prog);
		int end = tokenEnd(prog, start);
		
		String token = prog.substring(start, end);
		
		GPNode node = GPMemPool.instance().getNode();

		try {
			double val = new Double(token);
			node.initVal(val, parent);
		}
		catch (Exception e) {
		
			if (token.charAt(0) == '$') {
				int var = new Integer(token.substring(1));
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
				else if (token.equals(">="))
					fun = GPFun.GE;
				else if (token.equals("RAND_UNIF"))
					fun = GPFun.RAND_UNIF;
				else if (token.equals("RAND_NORM"))
					fun = GPFun.RAND_NORM;
			
				node.initFun(fun, parent, extraFuns);

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


	public void parse(String prog)
	{
		parsePos = 0;
		root = parse2(prog, null);
	}


	private void clearBranching2(GPNode node)
	{
		node.branching = -1;
		for (int i = 0; i < node.arity; i++)
			clearBranching2(node.params[i]);
	}


	public void clearBranching()
	{
		clearBranching2(root);
	}


	public int branchingDistance(GPTree tree)
	{
		return branchingDistance2(root, tree.root);
	}


	private int branchingDistance2(GPNode node1, GPNode node2)
	{
		int distance = 0;
		if (node1.branching != node2.branching)
			distance += 1;
		for (int i = 0; i < node1.arity; i++)
			distance += branchingDistance2(node1.params[i], node2.params[i]);
		return distance;
	}


	private void moveUp(GPNode origNode, GPNode targNode)
	{
		switch (origNode.type) {
		case VAL:
			targNode.initVal(origNode.val, origNode.parent);
			break;
		case VAR:
			targNode.initVar(origNode.var, origNode.parent);
			break;
		default:
			targNode.initFun(origNode.fun, origNode.parent, extraFuns);
			break;
		}
		targNode.branching = origNode.branching;
		targNode.dynStatus = origNode.dynStatus;

		for (int i = 0; i < origNode.arity; i++) {
			targNode.params[i] = origNode.params[i];
			targNode.params[i].parent = targNode;
		}

		GPMemPool.instance().returnNode(origNode);
	}


	public void dynPruning()
	{
		dynPruning2(root);
	}


	private void dynPruning2(GPNode node)
	{
		// nodes with constant value
		if (node.dynStatus == GPNodeDynStatus.CONSTANT) {
			for (int i = 0; i < node.arity; i++)
				destroyGPNode(node.params[i]);
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
				for (int i = 0; i < node.arity; i++)
					if (i != branch)
						destroyGPNode(node.params[i]);

				moveUp(node.params[branch], node);
			}
		}

		for (int i = 0; i < node.arity; i++)
			dynPruning2(node.params[i]);
	}
}