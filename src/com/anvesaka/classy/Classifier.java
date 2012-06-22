package com.anvesaka.classy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classifier {

	private int shingleSize;
	
	public Classifier(int shingleSize) {
		this.shingleSize = shingleSize;
	}
	
	public List<Classification> classify(List<String> raw, int passes, Seed... seeds) {
		Model model =  buildInitialModel(seeds);
		List<Classification> classifications = null;
		int passCount = 0;
		double feedbackIncrement = 0.5/passes;
		double feedbackFraction = feedbackIncrement;
		while (passCount<passes) {
			model.reduce();
			Classification classification;
			classifications = new ArrayList<Classification>();
			for (String text:raw) {
				classification = model.classifyContent(text);
				classifications.add(classification);
			}
			passCount++;
			model = new Model(shingleSize);
			Collections.sort(classifications, new Comparator<Classification>() {
				public int compare(Classification c1, Classification c2) {
					Double confidence1 = c1.computeConfidence();
					Double confidence2 = c2.computeConfidence();
					return confidence2.compareTo(confidence1);
				}
			});
			int n = (int)(classifications.size()*feedbackFraction);
			for (int i=0; i<n; i++) {
				classification = classifications.get(i);
				model.addContent(classification.getRaw(), classification);
			}
			feedbackFraction+=feedbackIncrement;
		}
		return classifications;
	}
	
	
	protected Model buildInitialModel(Seed[] seeds) {
		Model model = new Model(shingleSize);
		for (Seed seed:seeds) {
			model.addContent(seed.text, seed.groupKey, 1.0);
		}
		return model;
	}
	
	public static void main(String argv[]) throws Exception {
		BufferedReader reader;
		String line;
		List<String> raw = new ArrayList<String>();
		Map<String, String> truth = new HashMap<String, String>();
		raw.add("This is an english sentence.");
		truth.put("This is an english sentence.", "eng");
		raw.add("Il s'agit d'une phrase française.");
		truth.put("Il s'agit d'une phrase française.", "fra");
		
		reader = new BufferedReader(new FileReader("data/leipzig/news/eng/eng_news_2010_10K-sentences.txt"));
		while ((line=reader.readLine())!=null) {
			String[] parts = line.split("\\t");
			raw.add(parts[1]);
			truth.put(parts[1], "eng");
		}

		reader = new BufferedReader(new FileReader("data/leipzig/news/fra/fra_news_2010_10K-sentences.txt"));
		while ((line=reader.readLine())!=null) {
			String[] parts = line.split("\\t");
			raw.add(parts[1]);
			truth.put(parts[1], "fra");
		}
		
		Classifier classifier = new Classifier(7);
		List<Classification> classifications = classifier.classify(raw, 6, 
				new Seed("This is an english sentence.", "eng"),
				new Seed("Il s'agit d'une phrase française.", "fra"),
				new Seed("Detta är en svensk mening", "swe"));
		int correct = 0;
		for (Classification classification:classifications) {
			if (classification.computePredictedGroup()!=null&&truth.get(classification.getRaw()).equals(classification.computePredictedGroup().group)) {
				correct++;
			}
			else {
				System.out.println(classification.toString());
			}
		}
		System.out.println(correct);
	}
}
