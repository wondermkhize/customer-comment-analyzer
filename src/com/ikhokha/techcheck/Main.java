package com.ikhokha.techcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		
		Map<String, Integer> totalResults = new HashMap<>();
		
		File docPath = new File("docs");
		Measure metric = new Metric();
		File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));
		int numberOfThreads = commentFiles.length;
		Thread[] threads = new Thread[numberOfThreads];
		
		final int filesPerThread = commentFiles.length / numberOfThreads;
		final int remainingFiles = commentFiles.length % numberOfThreads;
		
		for (int process = 0; process < numberOfThreads; process++) {
			final int thread = process;
			
			threads[process] = new Thread(){
				@Override public void run(){
					runThread(totalResults, commentFiles, metric, numberOfThreads, thread, filesPerThread, remainingFiles);
				}
			};
		}
		
		for (Thread threadOne : threads) 
			 threadOne.start();
		for (Thread threadTwo : threads)
			try {
				threadTwo.join();
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted: ->");
				e.printStackTrace();
			}
		
		System.out.println("RESULTS\n=======");
		totalResults.forEach((k,v) -> System.out.println(k + " : " + v));
	}
	
	private static void runThread(Map<String, Integer> totalResults, File[] commentFiles, Measure metric, int numberOfThreads, int thread, int filesPerThread, int remainingFiles) {
		
		List<File> fileList = new ArrayList<>();
		
		for (int process = thread * filesPerThread; process < (thread + 1) * filesPerThread; process++)
		{
			fileList.add(commentFiles[process]);
		}
		
		if (thread == numberOfThreads - 1 && remainingFiles > 0)
		{
			for (int processTwo = commentFiles.length - remainingFiles; processTwo < commentFiles.length; processTwo++)
			{
				fileList.add(commentFiles[processTwo]);
			}
		}
		
		for (File commentFile : fileList) {
			
			CommentAnalyzer commentAnalyzer = new CommentAnalyzer(commentFile, metric);
			Map<String, Integer> fileResults = commentAnalyzer.analyze();
			addReportResults(fileResults, totalResults);
		}
	}
	
	/**
	 * This method adds the result counts from a source map to the target map 
	 * @param source the source map
	 * @param target the target map
	 */
	private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			
			if (!target.containsKey(entry.getKey())) {
				target.put(entry.getKey(), entry.getValue());
				continue;
			}
			
			target.put(entry.getKey(), (entry.getValue() + target.get(entry.getKey())));
		}
		
	}

}
