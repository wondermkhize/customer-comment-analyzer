package com.ikhokha.techcheck;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class Metric implements Measure {
    
	public Metric() {
	}
	
	@Override
	public void Compare(String line, Map<String, Integer> resultsMap) {
		
		for (Map.Entry<String, String> entry:  getAllMetrics().entrySet()) {
			 
			if (entry.getKey().equals("SHORTER_THAN_15") && (line.length() < (Integer.parseInt(entry.getValue())))) {
				incOccurrence(resultsMap, entry.getKey());
			}
			
			if (entry.getKey().equals("SPAM") && checkForUrl(entry.getValue(), line)) {
				incOccurrence(resultsMap, entry.getKey());
				continue;
			}
			
			if (line.contains(entry.getValue())) {
				incOccurrence(resultsMap, entry.getKey());
			}
			
		}
	}
	
	private void incOccurrence(Map<String, Integer> countMap, String key) {
		
		countMap.putIfAbsent(key, 0);
		countMap.put(key, countMap.get(key) + 1);
	}
	
	private Map<String, String> getAllMetrics() {
		
		Map<String, String> metrics = new HashMap<>();
		
		metrics.put("SHORTER_THAN_15", "15");
		metrics.put("MOVER_MENTIONS", "Mover");
		metrics.put("SHAKER_MENTIONS", "Shaker");
		metrics.put("QUESTIONS", "?");
		metrics.put("SPAM", "\\b((?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:, .;]*[-a-zA-Z0-9+&@#/%=~_|])");
		
		return metrics;
	}
	
	private boolean checkForUrl(String regex, String line) {
		
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(line);
		
		return match.find();
	}
}
