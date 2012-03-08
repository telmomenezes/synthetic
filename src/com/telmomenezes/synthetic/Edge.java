package com.telmomenezes.synthetic;

public class Edge {
    private Node orig;
    private Node targ;
    private Edge nextOrig;
    private Edge nextTarg;
    private long timestamp;

    Node getOrig() {
        return orig;
    }

    void setOrig(Node orig) {
        this.orig = orig;
    }

    Node getTarg() {
        return targ;
    }

    void setTarg(Node targ) {
        this.targ = targ;
    }

    Edge getNextOrig() {
        return nextOrig;
    }

    void setNextOrig(Edge nextOrig) {
        this.nextOrig = nextOrig;
    }

    Edge getNextTarg() {
        return nextTarg;
    }

    void setNextTarg(Edge nextTarg) {
        this.nextTarg = nextTarg;
    }

    long getTimestamp() {
        return timestamp;
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}