/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "gptree.h"
#include "probs.h"
#include <string.h>
#include <iostream>


using std::cout;
using std::endl;
using std::flush;


GPTree::GPTree(unsigned int varcount, GPMemPool* mempool)
{
    _varcount = varcount;
    _mempool = mempool;
    vars = (gpval *)malloc(sizeof(gpval) * varcount);
    bzero(vars, sizeof(gpval) * varcount);
    active = 0;
}


GPTree::~GPTree()
{
    destroy_gpnode(_root);
    free(vars);
}


void GPTree::destroy_gpnode(GPNode* node)
{
    unsigned int i;
    for (i = 0; i < node->arity; i++)
        destroy_gpnode(node->params[i]);
    _mempool->return_node(node);
}


gpval GPTree::eval(unsigned int active_p)
{
    GPNode* curnode = _root;
    active = active_p;
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
                        case GET:
                            if (curnode->vals[0] >= curnode->vals[1])
                                curnode->stoppos = 3;
                            else {
                                curnode->stoppos = 4;
                                curnode->curpos++;
                            }
                            break;
                        case LET:
                            if (curnode->vals[0] <= curnode->vals[1])
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
                        case EQ:
                        case GRT:
                        case LRT:
                        case GET:
                        case LET:
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


void GPTree::print2(GPNode* node, unsigned int indent)
{
    unsigned int i;
    unsigned int ind = indent;

    if (node->arity > 0) {
        if (node->parent) cout << endl;
        for (i=0; i < indent; i++) cout << "  ";
        cout << "(";
        ind++;
    }

    node->print();

    for (i = 0; i < node->arity; i++) {
        cout << " ";
        print2(node->params[i], ind);
    }

    if (node->arity > 0) {
        cout << ")";
        ind--;
    }
}


void GPTree::print()
{
    print2(_root, 0);
    cout << endl;
}


GPNode* GPTree::create_random2(unsigned int varcount,
                                float prob_term,
                                GPNode *parent,
                                unsigned int max_depth,
                                unsigned int grow,
                                unsigned int depth,
                                GPMemPool* mempool)
{
    GPNode* node;
    gpval val;
    float p = ((float)(random() % 999999999)) / 999999999.0;
    if (((!grow) || (p > prob_term)) && (depth < max_depth)) {
            node = mempool->get_node();
            node->init(FUN, (gpnode_fun)(random() % GPFUN_COUNT), 0, 0, parent);
            for (unsigned int i = 0; i < node->arity; i++)
                node->params[i] = create_random2(varcount, prob_term, node, max_depth, grow, depth + 1, mempool);
    }
    else {
        if ((random() % 2) && (varcount > 0)) {
            unsigned int var = random() % varcount;
            node = mempool->get_node();
            node->init(VAR, SUM, 0, var, parent);
        }
        else {
            long r = random() % 3;
            switch (r) {
                case 0:
                    val = random() % RAND_MAX;
                    break;
                case 1:
                    val = random() % 16;
                    break;
                default:
                    val = 0;
                    break;
            }
            node = mempool->get_node();
            node->init(VAL, SUM, val, 0, parent);
        }
    }

    return node;
}


GPTree* GPTree::create_random(unsigned int varcount,
                            float prob_term,
                            unsigned int max_depth_low_limit,
                            unsigned int max_depth_high_limit,
                            GPMemPool* mempool)
{
    GPTree* tree = new GPTree(varcount, mempool);
    unsigned int grow = random() % 2;
    unsigned int max_depth = max_depth_low_limit + (random() % (max_depth_high_limit - max_depth_low_limit));

    tree->_root = create_random2(varcount, prob_term, NULL, max_depth, grow, 0, mempool);
    return tree;
}


GPNode* GPTree::clone_gpnode(GPNode* node, GPNode* parent)
{
    GPNode* cnode = _mempool->get_node();
    cnode->init(node->type, node->fun, node->val, node->var, parent);
    for (unsigned int i = 0; i < node->arity; i++)
        cnode->params[i] = clone_gpnode(node->params[i], cnode);
    return cnode;
}


GPTree* GPTree::clone()
{
    GPTree* ctree = new GPTree(_varcount, _mempool);
    ctree->_root = clone_gpnode(_root, NULL);
    return ctree;
}


unsigned int GPTree::size2(GPNode* node)
{
    unsigned int c = 1;
    for (unsigned int i = 0; i < node->arity; i++)
        c += size2(node->params[i]);
    return c;
}


unsigned int GPTree::size()
{
    return size2(_root);
}


GPNode* GPTree::gpnode_by_pos2(GPNode* node,
                            unsigned int pos,
                            unsigned int *curpos)
{
    GPNode* nodefound;

    if (pos == (*curpos)) return node;
    (*curpos)++;
    for (unsigned int i = 0; i < node->arity; i++) {
        nodefound = gpnode_by_pos2(node->params[i], pos, curpos);
        if (nodefound) return nodefound;
    }

    return NULL;
}


GPNode* GPTree::gpnode_by_pos(unsigned int pos)
{
    unsigned int curpos = 0;
    return gpnode_by_pos2(_root, pos, &curpos);
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

    unsigned int i;
    unsigned int parampos;

    /* remove sub-tree from child */
    /* find point1 position in it's parent's param array */
    if (point1parent)
        for (i = 0; i < point1parent->arity; i++) {
            if (point1parent->params[i] == point1) {
                parampos = i;
                break;
            }
        }

    destroy_gpnode(point1);

    /* copy sub-tree from parent 2 to parent 1*/
    point2clone = clone_gpnode(point2, point1parent);
    if (point1parent)
        point1parent->params[parampos] = point2clone;
    else
        child->_root = point2clone;

    return child;
}

