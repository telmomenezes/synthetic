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


gptree* create_gptree(unsigned int varcount)
{
    gptree* tree = (gptree*)malloc(sizeof(gptree));
    tree->varcount = varcount;
    tree->vars = (gpval *)malloc(sizeof(gpval) * varcount);
    bzero(tree->vars, sizeof(gpval) * varcount);

    return tree;
}


gptree* create_random_gptree(unsigned int varcount,
                                float prob_term,
                                unsigned int max_depth_low_limit,
                                unsigned int max_depth_high_limit)
{
    gptree* tree = create_gptree(varcount);
    unsigned int grow = random() % 2;
    unsigned int max_depth = max_depth_low_limit + (random() % (max_depth_high_limit - max_depth_low_limit));

    tree->root = create_random_gptree2(varcount, prob_term, NULL, max_depth, grow, 0);

    return tree;
}


gpnode* create_random_gptree2(unsigned int varcount,
                                float prob_term,
                                gpnode* parent,
                                unsigned int max_depth,
                                unsigned int grow,
                                unsigned int depth)
{
    gpnode* node;
    gpval val;
    gpval power;
    unsigned int var;
    unsigned int i;

    float p = ((float)(random() % 999999999)) / 999999999.0;
    if (((!grow) || (p > prob_term)) && (depth < max_depth)) {
        node = create_gpnode(FUN, (gpnode_fun)(random() % GPFUN_COUNT), 0, 0, parent);
        for (i = 0; i < node->arity; i++) {
            node->params[i] = create_random_gptree2(varcount, prob_term, node, max_depth, grow, depth + 1);
        }
    }
    else {
        if ((random() % 2) && (varcount > 0)) {
            var = random() % varcount;
            node = create_gpnode(VAR, SUM, 0, var, parent);
        }
        else {
            val = RANDOM_UNIFORM;

            if (0) {
                val *= 10;
                power = (RANDOM_UNIFORM * 40.0) - 20;
                val = pow(val, power);
            }
            node = create_gpnode(VAL, SUM, val, 0, parent);
        }
    }

    return node;
}


void destroy_gptree(gptree* tree)
{
    r_destroy_gpnode(tree->root);
    free(tree->vars);
    free(tree);
}


void r_destroy_gpnode(gpnode* node)
{
    unsigned int i;
    for (i = 0; i < node->arity; i++)
        r_destroy_gpnode(node->params[i]);
    destroy_gpnode(node);
}


gpval eval_gptree(gptree* tree)
{
    gpnode* curnode = tree->root;
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
                    val = tree->vars[curnode->var];
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


void print_gptree(gptree* tree)
{
    print_gptree2(tree->root, 0);
    printf("\n");
}


void print_gptree2(gpnode* node, unsigned int indent)
{
    unsigned int i;
    unsigned int ind = indent;

    if (node->arity > 0) {
        if (node->parent) {
            printf("\n");
        }
        for (i = 0; i < indent; i++) {
            printf("  ");
        }
        printf("(");
        ind++;
    }

    print_gpnode(node);

    for (i = 0; i < node->arity; i++) {
        printf(" ");
        print_gptree2(node->params[i], ind);
    }

    if (node->arity > 0) {
        printf(")");
        ind--;
    }
}


gptree* clone_gptree(gptree* tree)
{
    gptree* ctree = create_gptree(tree->varcount);
    ctree->root = clone_gpnode(tree->root, NULL);

    return ctree;
}


gpnode* clone_gpnode(gpnode* node, gpnode* parent)
{
    unsigned int i;
    gpnode* cnode = create_gpnode(node->type, node->fun, node->val, node->var, parent);
    for (i = 0; i < node->arity; i++)
        cnode->params[i] = clone_gpnode(node->params[i], cnode);

    return cnode;
}


unsigned int gptree_size(gptree* tree)
{
    return gptree_size2(tree->root);
}


unsigned int gptree_size2(gpnode* node)
{
    unsigned int c = 1;
    unsigned int i;
    for (i = 0; i < node->arity; i++) {
        c += gptree_size2(node->params[i]);
    }

    return c;
}


gpnode* gpnode_by_pos(gptree* tree, unsigned int pos)
{
    unsigned int curpos = 0;
    return gpnode_by_pos2(tree->root, pos, &curpos);
}


gpnode* gpnode_by_pos2(gpnode* node,
                            unsigned int pos,
                            unsigned int *curpos)
{
    gpnode* nodefound;
    unsigned int i;

    if (pos == (*curpos)) {
        return node;
    }
    (*curpos)++;
    for (i = 0; i < node->arity; i++) {
        nodefound = gpnode_by_pos2(node->params[i], pos, curpos);
        if (nodefound) {
            return nodefound;
        }
    }

    return NULL;
}


gptree* recombine_gptrees(gptree* parent1, gptree* parent2)
{
    gptree* child = clone_gptree(parent1);
    unsigned int size1 = gptree_size(parent1);
    unsigned int size2 = gptree_size(parent2);
    unsigned int pos1 = random() % size1;
    unsigned int pos2 = random() % size2;

    gpnode* point1 = gpnode_by_pos(child, pos1);
    gpnode* point2 = gpnode_by_pos(parent2, pos2);
    gpnode* point1parent = point1->parent;
    gpnode* point2clone;

    unsigned int i;
    unsigned int parampos;

    /* remove sub-tree from child */
    /* find point1 position in it's parent's param array */
    if (point1parent) {
        for (i = 0; i < point1parent->arity; i++) {
            if (point1parent->params[i] == point1) {
                parampos = i;
                break;
            }
        }
    }

    destroy_gpnode(point1);

    /* copy sub-tree from parent 2 to parent 1*/
    point2clone = clone_gpnode(point2, point1parent);
    if (point1parent) {
        point1parent->params[parampos] = point2clone;
    }
    else {
        child->root = point2clone;
    }

    return child;
}

