package com.telmomenezes.synthetic;


import java.util.Arrays;


import com.telmomenezes.synthetic.emd.Feature2D;
import com.telmomenezes.synthetic.emd.JFastEMD;
import com.telmomenezes.synthetic.emd.Signature;


public class Map2D {
    protected double[] data;
    protected int binNumber;
    protected double minValHor;
    protected double maxValHor;
    protected double minValVer;
    protected double maxValVer;

    public Map2D(int binNumber) {
        this.binNumber = binNumber;
        this.data = new double[binNumber * binNumber];

        clear();
    }

    public void clear() {
        Arrays.fill(getData(), 0);
    }

    public void setValue(int x, int y, double val) {
        getData()[(y * binNumber) + x] = val;
    }

    public void incValue(int x, int y) {
        getData()[(y * binNumber) + x] += 1;
    }

    public double getValue(int x, int y) {
        return getData()[(y * binNumber) + x];
    }

    public void logScale() {
        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                if (getData()[(y * binNumber) + x] > 0) {
                    getData()[(y * binNumber) + x] = Math.log(getData()[(y * binNumber) + x]);
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
                getData()[(y * binNumber) + x] = getData()[(y * binNumber) + x]
                        / m;
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
                getData()[(y * binNumber) + x] = getData()[(y * binNumber) + x]
                        / t;
            }
        }
    }

    public void binary() {
        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                if (getData()[(y * binNumber) + x] > 0) {
                    getData()[(y * binNumber) + x] = 1;
                }
            }
        }
    }

    public double total() {
        double total = 0;

        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                total += getData()[(y * binNumber) + x];
            }
        }

        return total;
    }

    double max() {
        double max = 0;

        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                if (getData()[(y * binNumber) + x] > max) {
                    max = getData()[(y * binNumber) + x];
                }
            }
        }

        return max;
    }

    double simpleDist(Map2D map) {
        double dist = 0;
        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                dist += Math.abs(getData()[(y * binNumber) + x]
                        - map.getData()[(y * map.binNumber) + x]);
            }
        }
        return dist;
    }

    private Signature getEmdSignature()
    {
        int n = 0;
        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                if (getValue(x, y) > 0) {
                    n++;
                }
            }
        }

        Feature2D[] features = new Feature2D[n];
        double[] weights = new double[n];

        int i = 0;
        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                double val = getValue(x, y);
                if (val > 0) {
                    Feature2D f = new Feature2D(x, y);
                    features[i] = f;
                    weights[i] = val;
                    i++;
                }
            }
        }

        Signature signature = new Signature();
        signature.setNumberOfFeatures(n);
        signature.setFeatures(features);
        signature.setWeights(weights);

        return signature;
    }
    
    public double emdDistance(Map2D map)
    {
        double infinity = Double.MAX_VALUE;

        if ((total() <= 0) || (map.total() <= 0)) {
            return infinity;
        }

        Signature sig1 = getEmdSignature();
        Signature sig2 = map.getEmdSignature();
        
        return JFastEMD.distance(sig1, sig2, -1);
    }
    
    public String cArray() {    
        String str = "{";
        for (int i = 0; i < binNumber * binNumber; i++) {
            if (i > 0) {
                str += ", ";
            }
            str += "" + data[i];
        }
        str += "}";
        return str;
    }

    @Override
    public String toString() {
        String str = "";
        for (int y = 0; y < binNumber; y++) {
            for (int x = 0; x < binNumber; x++) {
                str += getValue(x, y) + "\t";
            }
            str += "\n";
        }
        return str;
    }

    double[] getData() {
        return data;
    }

    void setData(double[] data) {
        this.data = data;
    }

    int getBinNumber() {
        return binNumber;
    }

    void setBinNumber(int binNumber) {
        this.binNumber = binNumber;
    }

    double getMinValHor() {
        return minValHor;
    }

    void setMinValHor(double minValHor) {
        this.minValHor = minValHor;
    }

    double getMaxValHor() {
        return maxValHor;
    }

    void setMaxValHor(double maxValHor) {
        this.maxValHor = maxValHor;
    }

    double getMinValVer() {
        return minValVer;
    }

    void setMinValVer(double minValVer) {
        this.minValVer = minValVer;
    }

    double getMaxValVer() {
        return maxValVer;
    }

    void setMaxValVer(double maxValVer) {
        this.maxValVer = maxValVer;
    }
}