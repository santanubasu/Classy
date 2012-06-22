package com.anvesaka.classy;

public class TermAffinity {
	final public String term;
	final public Double affinity;
	public TermAffinity(String term, double affinity) {
		this.term = term;
		this.affinity = affinity;
	}
	public String toString() {
		return "["+term+"="+affinity+"]";
	}
}
