package com.telmomenezes.synthetic.distribs;

import com.telmomenezes.synthetic.Net;

public class RevPageRanks extends Distrib {
    public RevPageRanks(Net net, int bins) {
       init(net.prOutSeq(), bins); 
    }
}