/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


class Params
{
public:
    static Params& inst();

    void setTagsSize(double tagsSize);
    inline double getTagsSize() {return _tagsSize;}
    void setMaxNodeTypes(double maxNodeTypes) {_maxNodeTypes = maxNodeTypes;}
    inline double getMaxNodeTypes() {return _maxNodeTypes;}
    inline double getTypeSize() {return _typeSize;}

private: 
    static Params _instance;

    unsigned int _tagsSize;
    unsigned int _maxNodeTypes;
    unsigned int _typeSize;
    
    Params();
    ~Params() {} 
    Params(const Params &);
    Params& operator=(const Params &);

    void updateTypeSize();
};
 
