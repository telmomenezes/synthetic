/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


namespace syn {

class DRMap
{
public:
	DRMap(unsigned int bin_number, double min_val_hor, double max_val_hor,
    	double min_val_ver, double max_val_ver);
	virtual ~DRMap();

	void clear();
	void set_value(unsigned int x, unsigned int y, double val);
	void inc_value(unsigned int x, unsigned int y);
	double get_value(unsigned int x, unsigned int y);

	void log_scale();
	void normalize();
	void binary();

	double total();

	double simple_dist(DRMap* map);
	double emd_dist(DRMap* map);

	void print();

	unsigned int get_bin_number() {return bin_number;}
	double get_min_val_hor() {return min_val_hor;}
	double get_max_val_hor() {return max_val_hor;}
	double get_min_val_ver() {return min_val_ver;}
	double get_max_val_ver() {return max_val_ver;}

private:
	double* data;
    unsigned int bin_number;
    double min_val_hor;
    double max_val_hor;
    double min_val_ver;
    double max_val_ver;
};

}
