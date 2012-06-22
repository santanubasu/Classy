package com.anvesaka.classy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
	
	private int shingleSize;
	private Map<String, Group> groups;
	private Map<String, Term> terms;
	
	public Model(int shingleSize) {
		this.shingleSize = shingleSize;
		groups = new HashMap<String, Group>();
		terms = new HashMap<String, Term>();
	}
	
	public void addContent(String raw, String groupKey, double affinity) {
		raw = raw.toLowerCase();
		List<String> shingles = Util.generateShingles(raw, shingleSize);
		Group group = getOrCreateGroup(groupKey);
		for (String shingle:shingles) {
			group.addTerm(shingle, affinity);
		}
	}
	
	public void addContent(String raw, Classification classification) {
		raw = raw.toLowerCase();
		List<String> shingles = Util.generateShingles(raw, shingleSize);
		for (GroupAffinity groupAffinity:classification.getAffinities()) {
			Group group = getOrCreateGroup(groupAffinity.group);
			for (String shingle:shingles) {
				group.addTerm(shingle, groupAffinity.score);
			}
		}
	}
	
	public void reduce() {
		//  Term        Group   Affinity
		Map<String, Map<String, Double>> affinities = new HashMap<String, Map<String, Double>>();
		for (String groupKey:groups.keySet()) {
			Group group = groups.get(groupKey);
			for (TermAffinity termAffinity:group.computeTermAffinities()) {
				Map<String, Double> affinityMap;
				if (affinities.containsKey(termAffinity.term)) {
					affinityMap = affinities.get(termAffinity.term);
				}
				else {
					affinityMap = new HashMap<String, Double>();
					affinities.put(termAffinity.term, affinityMap);
				}
				affinityMap.put(groupKey, termAffinity.affinity);
			}
		}
		int groupDimension = groups.size();
		for (String term:affinities.keySet()) {
			terms.put(term, new Term(term, affinities.get(term), groupDimension));
		}
	}
	
	public Classification classifyContent(String raw) {
		String rawLower = raw.toLowerCase();
		List<String> shingles = Util.generateShingles(rawLower, shingleSize);
		List<Term> contentTerms = new ArrayList<Term>();
		for (String shingle:shingles) {
			Term term = terms.get(shingle);
			if (term!=null) {
				contentTerms.add(term);
			}
		}
		Classification classification = new Classification(raw, contentTerms);
		return classification;
	}
	
	protected Group getOrCreateGroup(String key) {
		if (groups.containsKey(key)) {
			return groups.get(key);
		}
		else {
			Group group = new Group(key);
			groups.put(key, group);
			return group;
		}
	}
	
	public static void main(String argv[]) throws Exception {
		BufferedReader reader;
		Model model = new Model(6);
		String line;

		reader = new BufferedReader(new FileReader("data/leipzig/news/eng/eng_news_2010_10K-sentences.txt"));
		while ((line=reader.readLine())!=null) {
			String[] parts = line.split("\\t");
			model.addContent(parts[1], "eng", 1.0);
		}

		reader = new BufferedReader(new FileReader("data/leipzig/news/fra/fra_news_2010_10K-sentences.txt"));
		while ((line=reader.readLine())!=null) {
			String[] parts = line.split("\\t");
			model.addContent(parts[1], "fra", 1.0);
		}
		
		model.reduce();
		
		Classification english = model.classifyContent("This is an english sentence.");
		Classification french = model.classifyContent("Il s'agit d'une phrase française.");
		Classification frenchEnglish = model.classifyContent("Il s'agit d'une phrase en anglais.  This is an english sentence.");
		Classification bengali = model.classifyContent("এটা একটা বাংলা বাক্য");
		Classification swedish = model.classifyContent("Detta är en svensk mening");
		return;
	}
}
