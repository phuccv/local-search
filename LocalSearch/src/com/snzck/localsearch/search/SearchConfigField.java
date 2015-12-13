package com.snzck.localsearch.search;

public enum SearchConfigField {

	TABU_LENGTH("100"),
	MAX_STABE("100"),
	MAX_TIME("120"),
	MAX_ITERATOR("100000"),
	MAX_LOCAL_RESET("10");
	
	private String defaultValue;
	
	private SearchConfigField(String val){
		this.defaultValue = val;
	}
	
	public String getDefaultValue(){
		return defaultValue;
	}
}
