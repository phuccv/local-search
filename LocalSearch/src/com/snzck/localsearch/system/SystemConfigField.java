package com.snzck.localsearch.system;

public enum SystemConfigField {

	EVENT_QUEUE_SIZE("100");
	
	private String defaultValue;
	
	private SystemConfigField(String defaultValue){
		this.defaultValue = defaultValue;
	}
	
	public String getDefaultValue(){
		return defaultValue;
	}
	
}
