package com.telmomenezes.synthetic.io;

import com.telmomenezes.synthetic.Net;


public abstract class NetFile {
    abstract public Net load(String filePath, boolean directed);
    abstract public void save(Net net, String filePath);
    
    public static Net loadNet(String filePath, boolean directed) {
        int dotpos = filePath.lastIndexOf(".");
        String ext = "";
        if (dotpos > 0) {
            ext = filePath.substring(dotpos + 1, filePath.length()); 
        }
        ext = ext.toLowerCase();
            
        Net net = null;
        if (ext.equals("txt")) {
            net = (new SNAPNetFile()).load(filePath, directed);
            return net;
        }
        else if (ext.equals("snap")) {
            net = (new SNAPNetFile()).load(filePath, directed);
            return net;
        }
        else if (ext.equals("gml")) {
            net = (new GMLNetFile()).load(filePath, directed);
            return net;
        }
        else if (ext.equals("mitab")) {
            net = (new MITAB25NetFile()).load(filePath, directed);
            return net;
        }
        else {
            return null;
        }
    }
    
    public static void saveNet(Net net, String filePath, NetFileType fileType) {
        switch (fileType) {
        case SNAP:
            (new SNAPNetFile()).save(net, filePath);
            break;
        case GML:
            (new GMLNetFile()).save(net, filePath);
            break;
        default:
            break;
        }
    }
}
