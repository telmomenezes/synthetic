package com.telmomenezes.synthetic;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import com.telmomenezes.synthetic.gp.GPExtraFuns;
import com.telmomenezes.synthetic.gp.GPTree;


/**
 * This class contains a set of genetic programs that determine the behavior
 * of agents.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class ProgSet {

	public int progcount;
    public GPTree[] progs;
    public Vector<String> prognames;
    public Vector<Vector<Integer>> funsets;
    public Vector<Integer> varcounts;
    private GPExtraFuns extraFuns;

    
    public ProgSet(int progcount, GPExtraFuns extraFuns)
    {
    	this.progcount = progcount;
    	this.extraFuns = extraFuns;
    	
    	prognames = new Vector<String>();
    	funsets = new Vector<Vector<Integer>>();
    	varcounts = new Vector<Integer>();
    	
    	// init progs
    	progs = new GPTree[progcount];
    	for (int i = 0; i < progcount; i++) {
    		progs[i] = null;
    		prognames.add("?");
    		funsets.add(null);
    		varcounts.add(0);
    	}
    }


    public void init()
    {
    	for (int i = 0; i < progcount; i++)
    		progs[i] = new GPTree(varcounts.get(i), funsets.get(i), extraFuns);
    }


    public void initRandom()
    {
    	init();

    	// TODO: make this configurable
    	double termProb = 0.4;
    	int mdl = 2;
    	int mdh = 5;

    	for (int i = 0; i < progcount; i++)
    		progs[i].initRandom(termProb, mdl, mdh);
    }


    public ProgSet recombine(ProgSet parent2)
    {
    	ProgSet ps = clone(false);

    	int rprog = RandomGenerator.instance().random.nextInt(progcount);
    	for (int i = 0; i < progcount; i++) {
    		if (i == rprog)
    			ps.progs[i] = progs[i].recombine(parent2.progs[i]);
    		else
    			ps.progs[i] = progs[i].clone();
    	}

    	return ps;
    }


    public ProgSet clone(boolean clone_progs)
    {
    	ProgSet ps = new ProgSet(progcount, extraFuns);

    	for (int i = 0; i < progcount; i++) {
    		ps.prognames.set(i, prognames.get(i));
    		ps.funsets.set(i, funsets.get(i));
    		ps.varcounts.set(i, varcounts.get(i));
    	}

    	if (clone_progs)
    		for (int i = 0; i < progcount; i++)
    			ps.progs[i] = progs[i].clone();

    	return ps;
    }


    public int size()
    {
    	int psize = 0;

    	for (int i = 0; i < progcount; i++)
    		psize += progs[i].size();

    		return psize;
    }


    public void write(OutputStreamWriter out) throws IOException
    {
    	for (int i = 0; i < progcount; i++) {
    		out.write("# " + prognames.get(i));
    		progs[i].write(out);
    		out.write("\n\n");
    	}
    }


    public void load(String filePath) throws IOException
    {
    	init();

        FileInputStream fstream = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        String line = br.readLine();
    	 
    	for (int i = 0; i < progcount; i++) {
    		while ((line.equals(""))
    				|| (line.charAt(0) == '\n')
    				|| (line.charAt(0) == '#'))
    			line = br.readLine();
        
    		String prog = "";
    		while (!line.equals("")
    				&& (line.charAt(0) != '\n')
    				&& (line.charAt(0) != '#')) {
    			prog += line;
    			line = br.readLine();
    		}

    		progs[i].parse(prog);
    		
    		in.close();
    	}
    }


    public void clearBranching()
    {
    	for (int i = 0; i < progcount; i++)
    		progs[i].clearBranching();
    }


    public boolean compareBranching(ProgSet ps)
    {
    	for (int i = 0; i < progcount; i++)
    		if (progs[i].branchingDistance(ps.progs[i]) != 0)
    			return false;

    	return true;
    }


    public void dynPruning()
    {
    	for (int i = 0; i < progcount; i++)
    		progs[i].dynPruning();
    }


    public int branchingDistance(ProgSet ps)
    {
    	int distance = 0;

    	for (int i = 0; i < progcount; i++) {
    		if (progs[i].branchingDistance(ps.progs[i]) != 0)
    			distance++;
    	}

    	return distance;
    } 
}