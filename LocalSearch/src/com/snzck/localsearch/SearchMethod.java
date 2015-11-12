package com.snzck.localsearch;

public interface SearchMethod extends Runnable {

	final boolean VERBOSE = false;
	
	/**
	 * Stop search process
	 */
	void stop();
	
}
