package com.telmomenezes.synthetic.emd;


// TODO: make this based on an abstract class or interface so that
//       other types of features can be created (e.g different dimensionality)
public class Feature {
    public double x;
    public double y;
    
    public double groundDist(Feature f)
    {
        double deltaX = x - f.x;
        double deltaY = y - f.y;
        double dist = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        return dist;
    }
}