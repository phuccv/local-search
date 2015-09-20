package com.snzck.localsearch.binpacking2d;

public enum BpConfigField {
	BIN_PACKING_2D_DATA_FOLDER("E:\\phuc\\data\\binpacking2d");
	
	private String defaultValue;
	
	private BpConfigField(String defaultValue){
		this.defaultValue = defaultValue;
	}
	
	public String getDefaultValue(){
		return defaultValue;
	}
}
