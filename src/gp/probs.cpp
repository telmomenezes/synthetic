/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "probs.h"
#include <stdlib.h>


int test_prob(float prob) {
    float p = ((float)(random() % 999999999)) / 999999999.0;
    return (p < prob);
}

