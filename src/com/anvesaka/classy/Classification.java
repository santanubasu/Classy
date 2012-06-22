package com.anvesaka.classy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Classification {
	
	final private String raw;
	final private Set<GroupAffinity> affinities;
	private SortedSet<GroupAffinity> sortedAffinities;
	private Double meanAffinity;
	private Double confidence;
	private GroupAffinity predictedGroup;
	private String summary;
	
	public Classification(String raw, List<Term> terms) {
		this.raw = raw;
		Map<String, GroupAffinity> workingAffinities = new HashMap<String, GroupAffinity>();
		affinities = new HashSet<GroupAffinity>();
		for (Term term:terms) {
			for (GroupAffinity affinity:term.getAffinities()) {
				if (workingAffinities.containsKey(affinity.group)) {
					workingAffinities.put(affinity.group, new GroupAffinity(affinity.group, affinity.score+workingAffinities.get(affinity.group).score));
				}
				else {
					workingAffinities.put(affinity.group, affinity);
				}
			}
		}
		int termCount = terms.size();
		for (String groupKey:workingAffinities.keySet()) {
			GroupAffinity affinity = workingAffinities.get(groupKey);
			affinities.add(new GroupAffinity(affinity.group, affinity.score/termCount));
		}
	}
	
	public double computeMeanAffinity() {
		if (affinities.isEmpty()) {
			return 0.0;
		}
		if (meanAffinity==null) {
			double sum = 0.0;
			for (GroupAffinity groupAffinity:affinities) {
				sum+=groupAffinity.score;
			}
			meanAffinity = sum/=affinities.size();
		}
		return meanAffinity;
	}
	
	public double computeConfidence() {
		if (affinities.isEmpty()) {
			return 0.0;
		}
		computePredictedGroup();
		computeMeanAffinity();
		if (confidence==null) {
			double sum = 0.0;
			for (GroupAffinity groupAffinity:affinities) {
				sum+=groupAffinity.score;
			}
			confidence = Math.pow(predictedGroup.score, 2)/sum;
		}
		return confidence;
	}
	
	public GroupAffinity computePredictedGroup() {
		if (affinities.isEmpty()) {
			return null;
		}
		if (predictedGroup==null) {
			computeSortedAffinities();
			predictedGroup = sortedAffinities.last();
		}
		return predictedGroup;
	}

	public Set<GroupAffinity> getAffinities() {
		return Collections.unmodifiableSet(affinities);
	}
	
	public SortedSet<GroupAffinity> computeSortedAffinities() {
		if (sortedAffinities==null) {
			sortedAffinities = new TreeSet<GroupAffinity>(new Comparator<GroupAffinity>() {
				@Override
				public int compare(GroupAffinity a1, GroupAffinity a2) {
					return a1.score.compareTo(a2.score);
				}
			});
			sortedAffinities.addAll(affinities);
		}
		return sortedAffinities;
	}

	public String toString() {
		if (affinities.isEmpty()) {
			return "Unknown";
		}
		if (summary==null) {
			summary = raw+"{";
			computeSortedAffinities();
			for (GroupAffinity groupAffinity:sortedAffinities) {
				summary+=groupAffinity;
			}
			summary+="}";
		}
		return summary;
	}

	public String getRaw() {
		return raw;
	}
	
}
