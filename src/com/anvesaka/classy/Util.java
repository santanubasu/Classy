package com.anvesaka.classy;

import java.util.ArrayList;
import java.util.List;

public class Util {
	
	public static List<String> generateShingles(String raw, int shingleSize) {
		List<String> shingles = new ArrayList<String>(raw.length()-shingleSize);
		for (int i=0; i<raw.length()-shingleSize; i++) {
			shingles.add(raw.substring(i, i+shingleSize));
		}
		return shingles;
	}

}
