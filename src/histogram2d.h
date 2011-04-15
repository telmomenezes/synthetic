/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "emd.h"


class Histogram2D {
public:
    Histogram2D(unsigned int binNumber, double minValHor, double maxValHor,
        double minValVer, double maxValVer);
    virtual ~Histogram2D();

    unsigned int getBinNumber() {return _binNumber;}
    double getMinValHor() {return _minValHor;}
    double getMaxValHor() {return _maxValHor;}
    double getMinValVer() {return _minValVer;}
    double getMaxValVer() {return _maxValVer;}

    void clear();
    void setValue(unsigned int x, unsigned int y, double val);
    void incValue(unsigned int x, unsigned int y);
    double getValue(unsigned int x, unsigned int y);
    double* getData() {return _data;}

    void logScale();

    double simpleDist(Histogram2D* hist);
    double emdDist(Histogram2D* hist);

    void print();

private:
    double* _data;
    unsigned int _binNumber;
    double _minValHor;
    double _maxValHor;
    double _minValVer;
    double _maxValVer;

    signature_t* getEMDSignature();
};

