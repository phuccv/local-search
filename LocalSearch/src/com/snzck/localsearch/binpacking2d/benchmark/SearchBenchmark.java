package com.snzck.localsearch.binpacking2d.benchmark;

import com.snzck.localsearch.InitMethod;
import com.snzck.localsearch.SearchMethod;
import com.snzck.localsearch.SearchModel;
import com.snzck.localsearch.system.Config;

public class SearchBenchmark implements Runnable{
	private Config config;
	private SearchMethod search;
	private SearchModel model;
	private InitMethod init;
	
	private int runCounts;
	private int current;
	
	private int[] finalViolations;
	private long[] timeRuns;
	
	private int violationAvg;
	private long timeAvg;
	
	private boolean isFinished;
	private boolean isRunning;
	
	public SearchBenchmark(Config config, SearchModel model, SearchMethod search,
			InitMethod init){
		this.model = model;
		this.search = search;
		this.init = init;
		this.config = config;
		
		runCounts = Integer.valueOf(config.getProperty(
				SearchBenchmarkConfigField.RUN_COUNTS.name()));
		
		finalViolations = new int[runCounts];
		timeRuns = new long[runCounts];
		
	}

	@Override
	public void run() {
		isFinished = false;
		for(int i = 0; i< runCounts && isRunning; i++){
			model.initVariables(init);
			long time = System.currentTimeMillis();
			search.run();
			time = time - System.currentTimeMillis();
			timeRuns[i] = time;
			current ++;
		}
		
		long totalTime = 0;
		int totalViolations = 0;
		for(int i = 0; i < current; i++){
			totalTime += timeRuns[i];
			totalViolations += finalViolations[i];
		}
		timeAvg = totalTime / current;
		violationAvg = totalViolations / current;
		
		isFinished = true;
	}
	
	public synchronized void stop(){
		isRunning = false;
	}
	

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public SearchMethod getSearch() {
		return search;
	}

	public void setSearch(SearchMethod search) {
		this.search = search;
	}

	public SearchModel getModel() {
		return model;
	}

	public void setModel(SearchModel model) {
		this.model = model;
	}

	public int getRunCounts() {
		return runCounts;
	}

	public void setRunCounts(int runCounts) {
		this.runCounts = runCounts;
	}

	public int getViolationAvg() {
		return violationAvg;
	}

	public void setViolationAvg(int violationAvg) {
		this.violationAvg = violationAvg;
	}

	public long getTimeAvg() {
		return timeAvg;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isFinished() {
		return isFinished;
	}

}
