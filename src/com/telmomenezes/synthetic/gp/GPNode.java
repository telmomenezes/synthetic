package com.telmomenezes.synthetic.gp;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Genetic program node.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class GPNode {

    public GPNodeType type;
    public double val;
    public int var;
    public int fun;
    public int arity;
    public GPNode[] params;
    public double curval;
    protected GPNode parent;

    public int curpos;
    public int stoppos;
    public int condpos;

    public int branching;
    public GPNodeDynStatus dynStatus;
    
    public int evals;
    public int lastEval;
    
    private Prog tree;

    public GPNode(Prog tree) {
    	this.tree = tree;
        params = new GPNode[4];
    }

    public void initVal(double val, GPNode parent) {
        type = GPNodeType.VAL;
        this.parent = parent;
        this.val = val;
        arity = 0;
        condpos = -1;
        stoppos = 0;
        dynStatus = GPNodeDynStatus.UNUSED;
    }

    public void initVar(int var, GPNode parent) {
        type = GPNodeType.VAR;
        this.parent = parent;
        this.var = var;
        arity = 0;
        condpos = -1;
        stoppos = 0;
        dynStatus = GPNodeDynStatus.UNUSED;
    }

    public void initFun(int fun, GPNode parent) {
        type = GPNodeType.FUN;
        this.parent = parent;
        this.fun = fun;
        arity = funArity(fun);
        condpos = funCondPos(fun);
        stoppos = arity;
        dynStatus = GPNodeDynStatus.UNUSED;
    }

    private int funCondPos(int fun) {
        switch (fun) {
        case GPFun.ZER:
        case GPFun.GRP:
            return 1;
        case GPFun.EQ:
        case GPFun.GRT:
        case GPFun.LRT:
        case GPFun.MOD:
            return 2;
        default:
            return -1;
        }
    }

    private int funArity(int fun) {
        switch (fun) {
        case GPFun.EXP:
        case GPFun.LOG:
        case GPFun.ABS:
            return 1;
        case GPFun.SUM:
        case GPFun.SUB:
        case GPFun.MUL:
        case GPFun.DIV:
        case GPFun.MIN:
        case GPFun.MAX:
            return 2;
        case GPFun.ZER:
        case GPFun.GRP:
            return 3;
        case GPFun.EQ:
        case GPFun.GRT:
        case GPFun.LRT:
        case GPFun.MOD:
            return 4;
        // this should not happen
        default:
            return 0;
        }
    }

    public void write(OutputStreamWriter out, boolean evalStats)
            throws IOException {
        if (type == GPNodeType.VAL) {
            out.write("" + val);
        }
        else if (type == GPNodeType.VAR) {
            out.write("$" + tree.getVariableNames().get(var));
        }
        else if (type == GPNodeType.FUN) {
            switch (fun) {
            case GPFun.SUM:
                out.write("+");
                break;
            case GPFun.SUB:
                out.write("-");
                break;
            case GPFun.MUL:
                out.write("*");
                break;
            case GPFun.DIV:
                out.write("/");
                break;
            case GPFun.ZER:
                out.write("ZER");
                break;
            case GPFun.EQ:
                out.write("==");
                break;
            case GPFun.GRT:
                out.write(">");
                break;
            case GPFun.LRT:
                out.write("<");
                break;
            case GPFun.EXP:
                out.write("EXP");
                break;
            case GPFun.LOG:
                out.write("LOG");
                break;
            case GPFun.ABS:
                out.write("ABS");
                break;
            case GPFun.MIN:
                out.write("MIN");
                break;
            case GPFun.MAX:
                out.write("MAX");
                break;
            case GPFun.MOD:
                out.write("%");
                break;
            case GPFun.GRP:
                out.write("GRP");
                break;
            default:
                out.write("F??");
                break;
            }
        }
        else {
            out.write("???");
        }
        
        if (evalStats) {
            out.write(" " + evals + " " + lastEval);
        }
    }
}