package com.telmomenezes.synthetic.emd;

public class Signature {
    public int n;                  /* Number of features in the signature */
    public Feature[] Features;     /* Pointer to the features vector */
    public double[] Weights;       /* Pointer to the weights of the features (Changed from Rubner's)*/
}