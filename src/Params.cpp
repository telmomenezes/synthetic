/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "Params.h"


Params Params::_instance;
 

Params::Params()
{
    _tagsSize = 8;
    _maxNodeTypes = 10;
    updateTypeSize();
}


Params& Params::inst()
{
    return _instance;
}


void Params::updateTypeSize()
{
    _typeSize = (_tagsSize * 2) + 5;
}


void Params::setTagsSize(double tagsSize)
{
    _tagsSize = tagsSize;
    updateTypeSize();
}

