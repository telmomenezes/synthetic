/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "emd.h"


typedef struct syn_histogram2d_s {
    double *data;
    unsigned int bin_number;
    double min_val_hor;
    double max_val_hor;
    double min_val_ver;
    double max_val_ver;
} syn_histogram2d;

syn_histogram2d *syn_histogram2d_create(unsigned int bin_number, double min_val_hor, double max_val_hor,
    double min_val_ver, double max_val_ver);
void syn_histogram2d_destroy(syn_histogram2d *hist);

void syn_histogram2d_clear(syn_histogram2d *hist);
void syn_histogram2d_set_value(syn_histogram2d *hist, unsigned int x, unsigned int y, double val);
void syn_historgram2d_inc_value(syn_histogram2d *hist, unsigned int x, unsigned int y);
double syn_histogram2d_get_value(syn_histogram2d *hist, unsigned int x, unsigned int y);

void syn_histogram2d_log_scale(syn_histogram2d *hist);

double syn_histogram2d_simple_dist(syn_histogram2d *hist1, syn_histogram2d *hist2);
double syn_histogram2d_emd_dist(syn_histogram2d *hist1, syn_histogram2d *hist2);

void syn_histogram2d_print(syn_histogram2d *hist);

signature_t* syn_histogram2d_get_emd_signature(syn_histogram2d *hist);
