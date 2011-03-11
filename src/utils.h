/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include <time.h>
#include <stdlib.h>
#include <math.h>

// MAX & MIN
#define MAX(x,y) ((x) > (y) ? (x) : (y))
#define MIN(x,y) ((x) < (y) ? (x) : (y))

// Random numbers
#define RANDOM_SEED srandom(time(NULL))
#define RANDOM_UNIFORM (((double)random()) / ((double) RAND_MAX))
#define RANDOM_TESTPROB(x) ((((double)random()) / ((double) RAND_MAX)) < (x))
#define RANDOM_UINT(x) (random() % (x))
#define RANDOM_NORMAL (sqrt(-2.0 * log(((double)random()) / ((double) RAND_MAX))) * sin(2.0 * 3.14159265 * (((double)random()) / ((double) RAND_MAX))))

