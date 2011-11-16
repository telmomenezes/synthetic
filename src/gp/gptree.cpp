/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <math.h>
#include "gptree.h"
#include "utils.h"
#include <sstream>
#include <iostream>


using std::cout;
using std::endl;


template <class T>
bool from_string(T& t, 
                 const std::string& s, 
                 std::ios_base& (*f)(std::ios_base&))
{
  std::istringstream iss(s);
  return !(iss >> f >> t).fail();
}


namespace syn {

GPTree::GPTree(unsigned int varcount)
{
    this->varcount = varcount;
    vars = (gpval *)malloc(sizeof(gpval) * varcount);
    bzero(vars, sizeof(gpval) * varcount);
}


GPTree::~GPTree()
{
    r_destroy_gpnode(root);
    free(vars);
}


void GPTree::init_random(float prob_term,
                            unsigned int max_depth_low_limit,
                            unsigned int max_depth_high_limit)
{
    unsigned int grow = random() % 2;
    unsigned int max_depth = max_depth_low_limit + (random() % (max_depth_high_limit - max_depth_low_limit));

    root = init_random2(prob_term, NULL, max_depth, grow, 0);
}


GPNode* GPTree::init_random2(float prob_term,
                                GPNode* parent,
                                unsigned int max_depth,
                                unsigned int grow,
                                unsigned int depth)
{
    GPNode* node;

    float p = ((float)(random() % 999999999)) / 999999999.0;
    if (((!grow) || (p > prob_term)) && (depth < max_depth)) {
        node = new GPNode(FUN, (gpnode_fun)(random() % GPFUN_COUNT), 0, 0, parent);
        for (unsigned int i = 0; i < node->arity; i++) {
            node->params[i] = init_random2(prob_term, node, max_depth, grow, depth + 1);
        }
    }
    else {
        if ((random() % 2) && (varcount > 0)) {
            unsigned int var = random() % varcount;
            node = new GPNode(VAR, SUM, 0, var, parent);
        }
        else {
            gpval val = RANDOM_UNIFORM;

            if (0) {
                val *= 10;
                gpval power = (RANDOM_UNIFORM * 40.0) - 20;
                val = pow(val, power);
            }
            node = new GPNode(VAL, SUM, val, 0, parent);
        }
    }

    return node;
}


void GPTree::r_destroy_gpnode(GPNode* node)
{
    for (unsigned int i = 0; i < node->arity; i++) {
        r_destroy_gpnode(node->params[i]);
    }
    delete node;
}


gpval GPTree::eval()
{
    GPNode* curnode = root;
    curnode->curpos = -1;
    gpval val;

    while(curnode) {
        curnode->curpos++;
        if (curnode->curpos < curnode->stoppos) {
            if (curnode->curpos == curnode->condpos) {
                switch(curnode->fun) {
                        case EQ:
                            if (curnode->vals[0] == curnode->vals[1])
                                curnode->stoppos = 3;
                            else {
                                curnode->stoppos = 4;
                                curnode->curpos++;
                            }
                            break;
                        case GRT:
                            if (curnode->vals[0] > curnode->vals[1])
                                curnode->stoppos = 3;
                            else {
                                curnode->stoppos = 4;
                                curnode->curpos++;
                            }
                            break;
                        case LRT:
                            if (curnode->vals[0] < curnode->vals[1])
                                curnode->stoppos = 3;
                            else {
                                curnode->stoppos = 4;
                                curnode->curpos++;
                            }
                            break;
                        case ZER:
                            if (curnode->vals[0] == 0)
                                curnode->stoppos = 2;
                            else {
                                curnode->stoppos = 3;
                                curnode->curpos++;
                            }
                            break;
                        default:
                            break;
                    }
            }

            curnode = curnode->params[curnode->curpos];
            curnode->curpos = -1;
        }
        else {
            switch (curnode->type) {
                case FUN:
                    switch(curnode->fun) {
                        case SUM:
                            val = curnode->vals[0] + curnode->vals[1];
                            break;
                        case SUB:
                            val = curnode->vals[0] - curnode->vals[1];
                            break;
                        case MUL:
                            val = curnode->vals[0] * curnode->vals[1];
                            break;
                        case DIV:
                            if (curnode->vals[1] == 0)
                                val = 0;
                            else
                                val = curnode->vals[0] / curnode->vals[1];
                            break;
                        case MIN:
                            if (curnode->vals[0] < curnode->vals[1]) {
                                val = curnode->vals[0];
                            }
                            else {
                                val = curnode->vals[1];
                            }
                            break;
                        case MAX:
                            if (curnode->vals[0] > curnode->vals[1]) {
                                val = curnode->vals[0];
                            }
                            else {
                                val = curnode->vals[1];
                            }
                            break;
                        case EXP:
                            val = exp(curnode->vals[0]);
                            break;
                        case LOG:
                            if (curnode->vals[0] <= 0)
                                val = 0;
                            else
                                val = log(curnode->vals[0]);
                            break;
                        case SIN:
                            val = sin(curnode->vals[0]);
                            break;
                        case ABS:
                            val = curnode->vals[0];
                            if (val < 0)
                                val = -val;
                            break;
                        case EQ:
                        case GRT:
                        case LRT:
                        case ZER:
                            val = curnode->vals[curnode->stoppos - 1];
                            break;
                    }
                    break;
                case VAR:
                    val = vars[curnode->var];
                    break;
                case VAL:
                    val = curnode->val;
                    break;
            }

            curnode = curnode->parent;
            if (curnode)
                curnode->vals[curnode->curpos] = val;
        }
    }

    return val;
}


void GPTree::print()
{
    print2(root, 0);
    printf("\n");
}


void GPTree::print2(GPNode* node, unsigned int indent)
{
    unsigned int ind = indent;

    if (node->arity > 0) {
        if (node->parent) {
            printf("\n");
        }
        for (unsigned int i = 0; i < indent; i++) {
            printf("  ");
        }
        printf("(");
        ind++;
    }

    node->print();

    for (unsigned int i = 0; i < node->arity; i++) {
        printf(" ");
        print2(node->params[i], ind);
    }

    if (node->arity > 0) {
        printf(")");
        ind--;
    }
}


GPTree* GPTree::clone()
{
    GPTree* ctree = new GPTree(varcount);
    ctree->root = clone2(root, NULL);

    return ctree;
}


GPNode* GPTree::clone2(GPNode* node, GPNode* parent)
{
    GPNode* cnode = new GPNode(node->type, node->fun, node->val, node->var, parent);
    for (unsigned int i = 0; i < node->arity; i++)
        cnode->params[i] = clone2(node->params[i], cnode);

    return cnode;
}


unsigned int GPTree::size()
{
    return size2(root);
}


unsigned int GPTree::size2(GPNode* node)
{
    unsigned int c = 1;
    for (unsigned int i = 0; i < node->arity; i++) {
        c += size2(node->params[i]);
    }

    return c;
}


GPNode* GPTree::gpnode_by_pos(unsigned int pos)
{
    unsigned int curpos = 0;
    return gpnode_by_pos2(root, pos, &curpos);
}


GPNode* GPTree::gpnode_by_pos2(GPNode* node,
                                unsigned int pos,
                                unsigned int* curpos)
{
    GPNode* nodefound;

    if (pos == (*curpos)) {
        return node;
    }
    (*curpos)++;
    for (unsigned int i = 0; i < node->arity; i++) {
        nodefound = gpnode_by_pos2(node->params[i], pos, curpos);
        if (nodefound) {
            return nodefound;
        }
    }

    return NULL;
}


GPTree* GPTree::recombine(GPTree* parent2)
{
    GPTree* child = clone();
    unsigned int size1 = size();
    unsigned int size2 = parent2->size();
    unsigned int pos1 = random() % size1;
    unsigned int pos2 = random() % size2;

    GPNode* point1 = child->gpnode_by_pos(pos1);
    GPNode* point2 = parent2->gpnode_by_pos(pos2);
    GPNode* point1parent = point1->parent;
    GPNode* point2clone;

    unsigned int parampos;

    // remove sub-tree from child
    // find point1 position in it's parent's param array
    if (point1parent) {
        for (unsigned int i = 0; i < point1parent->arity; i++) {
            if (point1parent->params[i] == point1) {
                parampos = i;
                break;
            }
        }
    }

    delete point1;

    // copy sub-tree from parent 2 to parent 1
    point2clone = clone2(point2, point1parent);
    if (point1parent) {
        point1parent->params[parampos] = point2clone;
    }
    else {
        child->root = point2clone;
    }

    return child;
}


int GPTree::token_end(string prog, int pos)
{
    int curpos = pos;
    char curchar = prog[curpos];
    while ((curchar != ' ') 
            && (curchar != '\n')
            && (curchar != '\t')
            && (curchar != '\r')
            && (curchar != ')')
            && (curchar != '(')
            && (curchar != 0)) {

        curpos++;
        if (curpos >= prog.length()) {
            return curpos;
        }
        
        curchar = prog[curpos];
    }
            
    return curpos;
}


int GPTree::token_start(string prog)
{
    int curpos = parse_pos;
    char curchar = prog[curpos];
    while ((curchar == ' ')
            || (curchar == '\n')
            || (curchar == '\t')
            || (curchar == '\r')
            || (curchar == ')')
            || (curchar == '(')
            || (curchar == 0)) {

        curchar = prog[++curpos];
    }

    return curpos;
}


GPNode* GPTree::parse2(string prog, GPNode* parent)
{
    int start = token_start(prog);
    int end = token_end(prog, start);
        
    // TODO: off by 1?
    string token = prog.substr(start, (end - start));
    //cout << token << endl;

    GPNode* node;

    double val;
    if (from_string<double>(val, string(token), std::dec)) {
        node = new GPNode(VAL, SUM, val, 0, parent);
    }
    else {
        if (token[0] == '$') {
            int var;
            from_string<int>(var, token.substr(1), std::dec);
            node = new GPNode(VAR, SUM, 0, var, parent);
        }
        else {
            gpnode_fun fun = SUM;
            if (token.compare("+") == 0)
                fun = SUM;
            else if (token.compare("-") == 0)
                fun = SUB;
            else if (token.compare("*") == 0)
                fun = MUL;
            else if (token.compare("/") == 0)
                fun = DIV;
            else if (token.compare("ZER") == 0)
                fun = ZER;
            else if (token.compare("==") == 0)
                fun = EQ;
            else if (token.compare(">") == 0)
                fun = GRT;
            else if (token.compare("<") == 0)
                fun = LRT;
            else if (token.compare("EXP") == 0)
                fun = EXP;
            else if (token.compare("LOG") == 0)
                fun = LOG;
            else if (token.compare("SIN") == 0)
                fun = SIN;
            else if (token.compare("ABS") == 0)
                fun = ABS;
            else if (token.compare("MIN") == 0)
                fun = MIN;
            else if (token.compare("MAX") == 0)
                fun = MAX;
            
            node = new GPNode(FUN, fun, 0, 0, parent);

            parse_pos = end;
            
            for (int i = 0; i < node->arity; i++) {
                node->params[i] = parse2(prog, node);
            }

            return node;
        }
    }

    parse_pos = end;
    return node;
}


void GPTree::parse(string prog)
{
    parse_pos = 0;
    root = parse2(prog, NULL);
}

}