package com.telmomenezes.synthetic;

import java.awt.Color;
//import java.awt.Font;
//import java.awt.FontMetrics;
import java.awt.Graphics2D;
//import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.telmomenezes.synthetic.io.MatrixFile;


public class EdgeBalanceMap extends Map2D {

    public EdgeBalanceMap(Net net, int binNumber) {
        super(binNumber);
        
        double maxWeight = 0;
        for (Edge edge : net.getEdges()) {
            double weight = safelog(edge.getWeight());
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }
        
        double interval = maxWeight / ((double)(binNumber - 1));
        
        for (Edge edge : net.getEdges()) {
            int x = (int)Math.floor(safelog(edge.getWeight()) / interval);
            int y = 0;
            Edge inverseEdge = net.getInverseEdge(edge);
            if (inverseEdge != null) {
                y = (int)Math.floor(safelog(inverseEdge.getWeight()) / interval);
            }
            //System.out.println("x: " + x + "; y: " + y);
            if ((x > 0) || (y > 0)) {
                incValue(x, y);
            }
        }
    }
    
    private double safelog(double x) {
        if (x <= 00) {
            return 0.0;
        }
        
        return Math.log(x + 1);
    }
    
    public void draw(Graphics2D g, double side) {
        double binSide = ((double)side) / ((double)binNumber);
        double maxVal = max();
        
        // colors
        //Color gridColor = new Color(255, 255, 0);

        // font
        //Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        //FontMetrics metrics = g.getFontMetrics(font);
        //int textHeight = metrics.getHeight();
        //g.setFont(font);
        
        // draw cells
        for (int x = 0; x < binNumber; x++) {
            for (int y = 0; y < binNumber; y++) {
                double val = getValue(x, y);
                Color color = new Color(0, 150, 200);
                if (val > 0.0) {
                    color = new Color((int)(255.0 * (val / maxVal)), 0, 0);
                }
                g.setPaint(color);
                g.fill(new Rectangle2D.Double(x * binSide,
                        side - ((y + 1) * binSide),
                        binSide,
                        binSide));
            }
        }
                
        /*
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
        */
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
    
    public static void main(String[] args) {
        MatrixFile mf = new MatrixFile();
        Net net = mf.load("Chimane_AllianceMatrix.txt");
        EdgeBalanceMap map = new EdgeBalanceMap(net, 10);
        //map.logScale();
        map.normalizeTotal();
        map.draw("chimane.png");
        System.exit(0);
    }
}