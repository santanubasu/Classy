package com.anvesaka.classy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Group {
	
	final private String name;
	private double totalTermAffinity;
	private Map<String, Double> rawTermAffinities;
	
	public Group(String name) {
		this.name = name;
		totalTermAffinity = 0;
		rawTermAffinities = new HashMap<String, Double>();
	}
	
	public void addTerm(String term) {
		addTerm(term, 1.0);
	}
	
	public void addTerm(String term, double affinity) {
		totalTermAffinity+=affinity;
		if (rawTermAffinities.containsKey(term)) {
			rawTermAffinities.put(term, rawTermAffinities.get(term)+affinity);
		}
		else {
			rawTermAffinities.put(term, affinity);
		}
	}
	
	public double getRawTermAffinity(String term) {
		if (rawTermAffinities.containsKey(term)) {
			return rawTermAffinities.get(term);
		}
		else {
			return 0;
		}
	}

	public Set<TermAffinity> computeTermAffinities() {
		Set<TermAffinity> termAffinities = new HashSet<TermAffinity>();
		for (String term:rawTermAffinities.keySet()) {
			termAffinities.add(new TermAffinity(term, rawTermAffinities.get(term)/totalTermAffinity));
		}
		return termAffinities;
	}
	
	public String getName() {
		return name;
	}
	
}
