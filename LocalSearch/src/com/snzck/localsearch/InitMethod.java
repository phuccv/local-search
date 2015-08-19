package com.snzck.localsearch;

public interface InitMethod {
	/**
	 * Generate initial value for model
	 */
	void init();
	
	/**
	 * Get search model
	 * @return
	 */
	SearchModel getModel();
	
	/**
	 * Get short name of init method
	 * @return
	 */
	String getName();
}
