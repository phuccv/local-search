package com.snzck.localsearch.binpacking2d.benchmark;

/**
 * Store config field for benchmark with default value
 * @author phuc
 *
 */
public enum SearchBenchmarkConfigField {
	
	RUN_COUNTS("10");
	
	private String defaultValue;
	
	private SearchBenchmarkConfigField(String value){
		this.defaultValue = value;
	}

	public String getDefaultValue(){
		return defaultValue;
	}
	
}
