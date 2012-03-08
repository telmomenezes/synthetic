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

    public GPNode() {
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

    public void initFun(int fun, GPNode parent, GPExtraFuns extraFuns) {
        type = GPNodeType.FUN;
        this.parent = parent;
        this.fun = fun;
        arity = funArity(fun, extraFuns);
        condpos = funCondPos(fun);
        stoppos = arity;
        dynStatus = GPNodeDynStatus.UNUSED;
    }

    private int funCondPos(int fun) {
        switch (fun) {
        case GPFun.ZER:
            return 1;
        case GPFun.EQ:
        case GPFun.GRT:
        case GPFun.LRT:
            return 2;
        default:
            return -1;
        }
    }

    private int funArity(int fun, GPExtraFuns extraFuns) {
        switch (fun) {
        case GPFun.EXP:
        case GPFun.LOG:
        case GPFun.SIN:
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
            return 3;
        case GPFun.EQ:
        case GPFun.GRT:
        case GPFun.LRT:
            return 4;
            // this is an extra function
        default:
            return extraFuns.funArity(fun);
        }
    }

    public void write(OutputStreamWriter out, GPExtraFuns extraFuns)
            throws IOException {
        if (type == GPNodeType.VAL) {
            out.write("" + val);
            return;
        }

        if (type == GPNodeType.VAR) {
            out.write("$" + var);
            return;
        }

        if (type != GPNodeType.FUN) {
            out.write("???");
            return;
        }

        switch (fun) {
        case GPFun.SUM:
            out.write("+");
            return;
        case GPFun.SUB:
            out.write("-");
            return;
        case GPFun.MUL:
            out.write("*");
            return;
        case GPFun.DIV:
            out.write("/");
            return;
        case GPFun.ZER:
            out.write("ZER");
            return;
        case GPFun.EQ:
            out.write("==");
            return;
        case GPFun.GRT:
            out.write(">");
            return;
        case GPFun.LRT:
            out.write("<");
            return;
        case GPFun.EXP:
            out.write("EXP");
            return;
        case GPFun.LOG:
            out.write("LOG");
            return;
        case GPFun.SIN:
            out.write("SIN");
            return;
        case GPFun.ABS:
            out.write("ABS");
            return;
        case GPFun.MIN:
            out.write("MIN");
            return;
        case GPFun.MAX:
            out.write("MAX");
            return;
        default:
            if (extraFuns != null)
                // maybe it's an extra function
                out.write(extraFuns.funName(fun));
            else
                out.write("F??");
            return;
        }
    }
}