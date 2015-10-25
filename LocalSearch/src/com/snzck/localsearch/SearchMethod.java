package com.snzck.localsearch;

public interface SearchMethod extends Runnable {

	final boolean VERBOSE = true;
	
	/**
	 * Stop search process
	 */
	void stop();
	
}
