package com.telmomenezes.synthetic.distribs;

import com.telmomenezes.synthetic.Net;

public class InDegrees extends Distrib {
    public InDegrees(Net net, int bins) {
       init(net.inDegSeq(), bins); 
    }
}