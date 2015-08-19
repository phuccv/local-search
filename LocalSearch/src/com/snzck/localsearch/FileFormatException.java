package com.snzck.localsearch;


public class FileFormatException extends Exception{

	private static final long serialVersionUID = 1L;
	
	private String describe;
	
	public FileFormatException(String describe){
		this.describe = describe;
	}
	
	@Override
	public String getMessage() {
		return describe;
	}
	
}
