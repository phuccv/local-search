package com.snzck.localsearch.binpacking2d;

public enum BpConfigField {
	BIN_PACKING_2D_DATA_FOLDER("C:\\Users\\phuc\\workspace\\LocalSearch\\data\\binpacking2d");
	
	private String defaultValue;
	
	private BpConfigField(String defaultValue){
		this.defaultValue = defaultValue;
	}
	
	public String getDefaultValue(){
		return defaultValue;
	}
}
