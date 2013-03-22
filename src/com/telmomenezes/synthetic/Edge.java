package com.telmomenezes.synthetic;

public class Edge {
    private Node origin;
    private Node target;
    
    
    public Edge(Node origin, Node target) {
        this.origin = origin;
        this.target = target;
    }
    
    
    public Node getOrigin() {
        return origin;
    }

    
    public Node getTarget() {
        return target;
    }
    
    
    @Override
    public String toString() {
    	String str = "orig: " + origin;
    	str += "; targ: " + target;
    	return str;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
}