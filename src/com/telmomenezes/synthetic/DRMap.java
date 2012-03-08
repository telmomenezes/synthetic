package com.telmomenezes.synthetic;

import java.util.Arrays;


public class DRMap {

	double[] data;
    int binNumber;
    double minValHor;
    double maxValHor;
    double minValVer;
    double maxValVer;

    public DRMap(int binNumber, double minValHor, double maxValHor,
                double minValVer, double maxValVer) {
    	this.binNumber = binNumber;
    	this.minValHor = minValHor;
    	this.maxValHor = maxValHor;
    	this.minValVer = minValVer;
    	this.maxValVer = maxValVer;
    	data = new double[binNumber * binNumber];

    	clear();
    }
    
    public void clear() {
    	Arrays.fill(data, 0);
    }


    public void setValue(int x, int y, double val) {
    	data[(y * binNumber) + x] = val;
    }


    public void incValue(int x, int y) {
    	data[(y * binNumber) + x] += 1;
    }


    public double getValue(int x, int y) {
    	return data[(y * binNumber) + x];
    }

    
    public void logScale() {
    	for (int x = 0; x < binNumber; x++) {
    		for (int y = 0; y < binNumber; y++) {
    			if(data[(y * binNumber) + x] > 0) { 
    				data[(y * binNumber) + x] = Math.log(data[(y * binNumber) + x]);
    			}
    		}
    	}
    }


    public void normalizeMax() {
    	double m = max();
    	if (m <= 0) {
    		return;
    	}
    	// normalize by max
    	for (int x = 0; x < binNumber; x++) {
    		for (int y = 0; y < binNumber; y++) {
    			data[(y * binNumber) + x] = data[(y * binNumber) + x] / m;
    		}
    	}
    }


    public void normalizeTotal() {
    	double t = total();
    	if (t <= 0) {
    		return;
    	}
    	// normalize by max
    	for (int x = 0; x < binNumber; x++) {
    		for (int y = 0; y < binNumber; y++) {
    			data[(y * binNumber) + x] = data[(y * binNumber) + x] / t;
    		}
    	}
    }


    public void binary() {
    	for (int x = 0; x < binNumber; x++) {
    		for (int y = 0; y < binNumber; y++) {
    			if(data[(y * binNumber) + x] > 0) {
    				data[(y * binNumber) + x] = 1;
    			}
    		}
    	}
    }


    public double total() {
    	double total = 0;

    	for (int x = 0; x < binNumber; x++) {
    		for (int y = 0; y < binNumber; y++) {
    			total += data[(y * binNumber) + x];
    		}
    	}

    	return total;
    }


    double max() {
    	double max = 0;

    	for (int x = 0; x < binNumber; x++) {
    		for (int y = 0; y < binNumber; y++) {
    			if (data[(y * binNumber) + x] > max) {
    				max = data[(y * binNumber) + x];
    			}
    		}
    	}

    	return max;
    }


    double simpleDist(DRMap map) {
    	double dist = 0;
    	for (int x = 0; x < binNumber; x++) {
    		for (int y = 0; y < binNumber; y++) {
    			dist += Math.abs(data[(y * binNumber) + x] - map.data[(y * map.binNumber) + x]);
    		}
    	}
    	return dist;
    }


    /*
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
    	//printf("totals-> %f; %f\n", total(), map->total());

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
    }*/


    public void print() {
    	for (int y = 0; y < binNumber; y++) {
    		for (int x = 0; x < binNumber; x++) {
    			System.out.println(getValue(x, y) + '\t');
    		}
    		System.out.println("");
    	}
    }
}