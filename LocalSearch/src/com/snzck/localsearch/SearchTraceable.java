package com.snzck.localsearch;

public interface SearchTraceable {
	
	/**
	 * Method call when search routine found new local best.
	 */
	public void foundLocal();
	
	/**
	 * Method call when search routine found new global best
	 */
	public void foundGlobal();
	
	/**
	 * When search routine reset it result
	 * @param resetTimes times that search method was reseted
	 */
	public void localReset(int resetTimes);
	
	/**
	 * When search routine reset it to new random result
	 * @param resetTimes
	 */
	public void globalReset(int resetTimes);
	
	/**
	 * When search routine was interrupt.
	 */
	void interrupt();
	
	/**
	 * Call when search routine reached finish state
	 */
	void finish();
	
}
