/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "emd.h"


typedef struct syn_drmap_s {
    double *data;
    unsigned int bin_number;
    double min_val_hor;
    double max_val_hor;
    double min_val_ver;
    double max_val_ver;
} syn_drmap;

syn_drmap *syn_drmap_create(unsigned int bin_number, double min_val_hor, double max_val_hor,
    double min_val_ver, double max_val_ver);
void syn_drmap_destroy(syn_drmap *hist);

void syn_drmap_clear(syn_drmap *hist);
void syn_drmap_set_value(syn_drmap *hist, unsigned int x, unsigned int y, double val);
void syn_drmap_inc_value(syn_drmap *hist, unsigned int x, unsigned int y);
double syn_drmap_get_value(syn_drmap *hist, unsigned int x, unsigned int y);

void syn_drmap_log_scale(syn_drmap *hist);

double syn_drmap_simple_dist(syn_drmap *hist1, syn_drmap *hist2);
double syn_drmap_emd_dist(syn_drmap *hist1, syn_drmap *hist2);

void syn_drmap_print(syn_drmap *hist);

signature_t* syn_drmap_get_emd_signature(syn_drmap *hist);
