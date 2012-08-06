package com.telmomenezes.synthetic.distribs;

import com.telmomenezes.synthetic.Net;

public class PageRanks extends Distrib {
    public PageRanks(Net net, int bins) {
       init(net.prInSeq(), bins); 
    }
}