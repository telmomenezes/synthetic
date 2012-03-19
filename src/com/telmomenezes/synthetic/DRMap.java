package com.telmomenezes.synthetic;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.telmomenezes.synthetic.emd.EMD;
import com.telmomenezes.synthetic.emd.Feature;
import com.telmomenezes.synthetic.emd.Signature;


public class DRMap {

    private double[] data;
    private int binNumber;
    private double minValHor;
    private double maxValHor;
    private double minValVer;
    private double maxValVer;

    public DRMap(int binNumber, double minValHor, double maxValHor,
            double minValVer, double maxValVer) {
        this.binNumber = binNumber;
        this.minValHor = minValHor;
        this.maxValHor = maxValHor;
        this.minValVer = minValVer;
        this.maxValVer = maxValVer;
        setData(new double[binNumber * binNumber]);

        clear();
    }
    
    public DRMap(int binNumber) {
        this(binNumber, Double.MIN_VALUE, Double.MAX_VALUE,
                Double.MIN_VALUE, Double.MAX_VALUE);
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
    
    public void draw(Graphics2D g, double side) {
        double binSide = ((double)side) / ((double)binNumber);

        // colors
        Color gridColor = new Color(255, 255, 0);

        // font
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        FontMetrics metrics = g.getFontMetrics(font);
        int textHeight = metrics.getHeight();
        g.setFont(font);
        
        // draw cells
        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                double val = getValue(x, y);
                Color color = new Color(0, 150, 200);
                if (val > 0.0) {
                    color = new Color((int)(255.0 * val), 0, 0);
                }
                g.setPaint(color);
                g.fill(new Rectangle2D.Double(x * binSide,
                        side - ((y + 1) * binSide),
                        binSide,
                        binSide));
            }
        }
                

        // draw grid
        double center = side / 2.0;
        g.setPaint(gridColor);
        g.draw(new Line2D.Double(center, 0, center, side));
        g.draw(new Line2D.Double(0, center, side, center));

        // TODO: configure limit
        double limit = 7.0;
        
        int divs = ((int)limit) - 1;
        for (int j = 0; j < divs; j++) {
            double y = center - ((center / limit) * (j + 1));
            g.draw(new Line2D.Double(center - 5, y, center + 5, y));
            g.drawString(Integer.toString(j + 1), (float)(center + 10), (float)(y + (textHeight / 2) - 2));
            
            y = center + ((center / limit) * (j + 1));
            g.draw(new Line2D.Double(center - 5, y, center + 5, y));
            g.drawString("-" + Integer.toString(j + 1), (float)(center + 10), (float)(y + (textHeight / 2) - 2));

            double x = center - ((center / limit) * (j + 1));
            g.draw(new Line2D.Double(x, center - 5, x, center + 5));
            g.drawString("-" + Integer.toString(j + 1), (float)(x - 10), (float)(center + 20));

            x = center + ((center / limit) * (j + 1));
            g.draw(new Line2D.Double(x, center - 5, x, center + 5));
            g.drawString(Integer.toString(j + 1), (float)(x - 3), (float)(center + 20));
        }

        g.drawString("R", (float)(center - 15), 15);
        g.drawString("D", (float)(side - 15), (float)(center - 10));
    }
    
    public void draw(String filename, int side) {
        BufferedImage img = new BufferedImage(side, side, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = img.createGraphics();
        draw(g, side);
        
        try {
            File outputfile = new File(filename);
            ImageIO.write(img, "png", outputfile);
        }
        catch (IOException e) {
           e.printStackTrace();
        }
    }
    
    public void draw(String filename) {
        draw(filename, 500);
    }

    double simpleDist(DRMap map) {
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

        Feature[] features = new Feature[n];
        for (int i = 0; i < n; i++) {
            features[i] = new Feature();
        }
        double[] weights = new double[n];

        int i = 0;
        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                double val = getValue(x, y);
                if (val > 0) {
                    features[i].x = x;
                    features[i].y = y;
                    weights[i] = val;
                    i++;
                }
            }
        }

        Signature signature = new Signature();
        signature.n = n;
        signature.Features = features;
        signature.Weights = weights;

        return signature;
    }
    
    public double emdDistance(DRMap map)
    {
        double infinity = Double.MAX_VALUE;

        if ((total() <= 0) && (map.total() <= 0)) {
            return 0;
        }
        if (total() <= 0) {
            return infinity;
        }
        if (map.total() <= 0) {
            return infinity;
        }

        Signature sig1 = getEmdSignature();
        Signature sig2 = map.getEmdSignature();
        
        return EMD.compute(sig1, sig2, -1);
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
    
    public static void main(String[] args) {
        DRMap m1 = new DRMap(3);
        m1.setValue(0, 0, 1.0);
        System.out.println(m1);
        
        DRMap m2 = new DRMap(3);
        //m2.setValue(1, 1, 1.0);
        m2.setValue(2, 2, 1.0);
        System.out.println(m2);
        
        double dist = m1.emdDistance(m2);
        System.out.println("dist: " + dist);
    }
}