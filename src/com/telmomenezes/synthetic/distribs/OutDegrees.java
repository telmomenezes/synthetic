package com.telmomenezes.synthetic.distribs;

import com.telmomenezes.synthetic.Net;

public class OutDegrees extends Distrib {
    public OutDegrees(Net net, int bins) {
       init(net.outDegSeq(), bins); 
    }
}