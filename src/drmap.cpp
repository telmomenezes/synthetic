/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "drmap.h"
#include "utils.h"

#include "emd_hat_signatures_interface.hpp"

#include <stdlib.h>
#include <strings.h>
#include <stdio.h>

namespace syn
{

DRMap::DRMap(unsigned int bin_number, double min_val_hor, double max_val_hor,
                double min_val_ver, double max_val_ver)
{
    this->bin_number = bin_number;
    this->min_val_hor = min_val_hor;
    this->max_val_hor = max_val_hor;
    this->min_val_ver = min_val_ver;
    this->max_val_ver = max_val_ver;
    data = (double*)malloc(bin_number * bin_number * sizeof(double));

    clear();
}


DRMap::~DRMap()
{
    free(data);
}


void DRMap::clear()
{
    bzero(data, bin_number * bin_number * sizeof(double));
}


void DRMap::set_value(unsigned int x, unsigned int y, double val)
{
    data[(y * bin_number) + x] = val;
}


void DRMap::inc_value(unsigned int x, unsigned int y)
{
    data[(y * bin_number) + x] += 1;
}


double DRMap::get_value(unsigned int x, unsigned int y)
{
    return data[(y * bin_number) + x];
}

    
void DRMap::log_scale()
{
    for (unsigned int x = 0; x < bin_number; x++) {
        for (unsigned int y = 0; y < bin_number; y++) {
            if(data[(y * bin_number) + x] > 0) { 
                data[(y * bin_number) + x] = log(data[(y * bin_number) + x]);
            }
        }
    }
}


void DRMap::normalize()
{
    double t = total();
    if (t <= 0) {
        return;
    }
    // normalize by total
    for (unsigned int x = 0; x < bin_number; x++) {
        for (unsigned int y = 0; y < bin_number; y++) {
            data[(y * bin_number) + x] = data[(y * bin_number) + x] / t;
        }
    }
}


void DRMap::binary()
{
    for (unsigned int x = 0; x < bin_number; x++) {
        for (unsigned int y = 0; y < bin_number; y++) {
            if(data[(y * bin_number) + x] > 0) {
                data[(y * bin_number) + x] = 1;
            }
        }
    }
}


double DRMap::total()
{
    double total = 0;

    for (unsigned int x = 0; x < bin_number; x++) {
        for (unsigned int y = 0; y < bin_number; y++) {
            total += data[(y * bin_number) + x];
        }
    }

    return total;
}


double DRMap::simple_dist(DRMap* map)
{
    double dist = 0;
    for (unsigned int x = 0; x < bin_number; x++) {
        for (unsigned int y = 0; y < bin_number; y++) {
            dist += fabs(data[(y * bin_number) + x] - map->data[(y * map->bin_number) + x]);
        }
    }
    return dist;
}


double ground_dist(feature_tt* feature1, feature_tt* feature2)
{
    double deltaX = feature1->x - feature2->x;
    double deltaY = feature1->y - feature2->y;
    double dist = sqrt((deltaX * deltaX) + (deltaY * deltaY));
    //double dist = (deltaX * deltaX) + (deltaY * deltaY);
    return dist;
}


signature_tt* get_emd_signature(DRMap* map)
{
    unsigned int n = 0;
    unsigned int bin_number = map->get_bin_number();
    for (unsigned int x = 0; x < bin_number; x++) {
        for (unsigned int y = 0; y < bin_number; y++) {
            if (map->get_value(x, y) > 0) {
                n++;
            }
        }
    }

    feature_tt* features = (feature_tt*)malloc(sizeof(feature_tt) * n);
    double* weights = (double*)malloc(sizeof(double) * n);

    unsigned int i = 0;
    for (unsigned int x = 0; x < bin_number; x++) {
        for (unsigned int y = 0; y < bin_number; y++) {
            double val = map->get_value(x, y);
            if (val > 0) {
                features[i].x = x;
                features[i].y = y;
                weights[i] = val;
                i++;
            }
        }
    }

    signature_tt* signature = (signature_tt*)malloc(sizeof(signature_tt));
    signature->n = n;
    signature->Features = features;
    signature->Weights = weights;

    return signature;
}


double DRMap::emd_dist(DRMap* map)
{
    printf("totals-> %f; %f\n", total(), map->total());

    double infinity = 9999999999.9;

    if (total() <= 0) {
        return infinity;
    }
    if (map->total() <= 0) {
        return infinity;
    }

    signature_tt* sig1 = get_emd_signature(this);
    signature_tt* sig2 = get_emd_signature(map);
    
    double dist = emd_hat_signature_interface(sig1, sig2, ground_dist, -1);

    free(sig1->Features);
    free(sig1->Weights);
    free(sig1);
    free(sig2->Features);
    free(sig2->Weights);
    free(sig2);
    
    return dist;
}


void DRMap::print()
{
    for (unsigned int y = 0; y < bin_number; y++) {
        for (unsigned int x = 0; x < bin_number; x++) {
            printf("%f\t", get_value(x, y));
        }
        printf("\n");
    }
}

}
