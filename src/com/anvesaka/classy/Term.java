package com.anvesaka.classy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Term {

	final private String text;
	final private Set<GroupAffinity> groupAffinities;
	private String summary;
	
	public Term(String text, Map<String, Double> affinities, int groupDimension) {
		this.text = text;
		groupAffinities = new HashSet<GroupAffinity>();
		double meanAffinity = 0.0;
		for (String groupKey:affinities.keySet()) {
			meanAffinity+=affinities.get(groupKey);
		}
		meanAffinity/=groupDimension;
		for (String groupKey:affinities.keySet()) {
			groupAffinities.add(new GroupAffinity(groupKey, Math.pow(affinities.get(groupKey)/meanAffinity, 0.75)));
		}
	}
	
	public Set<GroupAffinity> getAffinities() {
		return Collections.unmodifiableSet(groupAffinities);
	}

	public String getText() {
		return text;
	}
	
	public String toString() {
		if (summary==null) {
			SortedSet<GroupAffinity> sortedAffinities = new TreeSet<GroupAffinity>(new Comparator<GroupAffinity>() {
				@Override
				public int compare(GroupAffinity a1, GroupAffinity a2) {
					return a2.score.compareTo(a1.score);
				}
			});
			sortedAffinities.addAll(groupAffinities);
			summary = "{";
			for (GroupAffinity affinity:sortedAffinities) {
				summary+=affinity.toString();
			}
			summary+="}";
		}
		return summary;
	}
}
