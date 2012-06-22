package com.anvesaka.classy;

public class GroupAffinity {
	final public String group;
	final public Double score;
	public GroupAffinity(String group, double score) {
		this.group = group;
		this.score = score;
	}
	public String toString() {
		return "["+group+"="+score+"]";
	}
}
