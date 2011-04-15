/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "Histogram2D.h"
#include "utils.h"
#include <stdlib.h>
#include <strings.h>
#include <stdio.h>
#include <iostream>


using std::cout;
using std::endl;


Histogram2D::Histogram2D(unsigned int binNumber, double minValHor, double maxValHor,
        double minValVer, double maxValVer)
{
    _binNumber = binNumber;
    _minValHor = minValHor;
    _maxValHor = maxValHor;
    _minValVer = minValVer;
    _maxValVer = maxValVer;
    _data = (double*)malloc(_binNumber * _binNumber * sizeof(double));

    clear();
}


Histogram2D::~Histogram2D()
{
    free(_data);
}


void Histogram2D::clear()
{
    bzero(_data, _binNumber * _binNumber * sizeof(double));
}


void Histogram2D::setValue(unsigned int x, unsigned int y, double val)
{
    _data[(y * _binNumber) + x] = val;
}


void Histogram2D::incValue(unsigned int x, unsigned int y)
{
    _data[(y * _binNumber) + x] += 1;
}


double Histogram2D::getValue(unsigned int x, unsigned int y)
{
    return _data[(y * _binNumber) + x];
}

    
void Histogram2D::logScale()
{
    for (unsigned int x = 0; x < _binNumber; x++)
        for (unsigned int y = 0; y < _binNumber; y++)
            if(_data[(y * _binNumber) + x] > 0) 
                _data[(y * _binNumber) + x] = log(_data[(y * _binNumber) + x]);
}


double Histogram2D::simpleDist(Histogram2D* hist)
{
    double dist = 0;
    for (unsigned int x = 0; x < _binNumber; x++)
        for (unsigned int y = 0; y < _binNumber; y++)
            dist += fabs(_data[(y * _binNumber) + x] - hist->_data[(y * _binNumber) + x]);
    return dist;
}


signature_t* Histogram2D::getEMDSignature()
{
    double intervalHor = (_maxValHor - _minValHor) / ((double)_binNumber);
    double intervalVer = (_maxValVer - _minValVer) / ((double)_binNumber);
    
    unsigned int n = 0;
    
    for (unsigned int x = 0; x < _binNumber; x++) {
        for (unsigned int y = 0; y < _binNumber; y++) {
            if (_data[(y * _binNumber) + x] > 0)
                n++;
        }
    }

    feature_t* features = (feature_t*)malloc(sizeof(feature_t) * n);
    double* weights = (double*)malloc(sizeof(double) * n);

    unsigned int i = 0;

    for (unsigned int x = 0; x < _binNumber; x++) {
        for (unsigned int y = 0; y < _binNumber; y++) {
            double val = _data[(y * _binNumber) + x];
            if (val > 0) {
                features[i].x = (x * intervalHor) + _minValHor;
                features[i].y = (y * intervalVer) + _minValVer;
                weights[i] = val;
                i++;
            }
        }
    }

    signature_t* signature = (signature_t*)malloc(sizeof(signature_t));
    signature->n = n;
    signature->Features = features;
    signature->Weights = weights;

    return signature;
}


double Histogram2D::emdDist(Histogram2D* hist)
{
    signature_t* sig1 = getEMDSignature();
    signature_t* sig2 = hist->getEMDSignature();
    
    double dist = emd(sig1, sig2, groundDist, NULL, NULL);
    
    free(sig1->Features);
    free(sig1->Weights);
    free(sig1);
    free(sig2->Features);
    free(sig2->Weights);
    free(sig2);
    
    return dist;
}


void Histogram2D::print()
{
    unsigned int count = 0;

    for (unsigned int y = 0; y < _binNumber; y++) {
        for (unsigned int x = 0; x < _binNumber; x++) {
            cout << getValue(x, y) << "\t";
            count += getValue(x, y);
        }
        cout << endl;
    }

    cout << "count: " << count << endl;
}

