package com.snzck.localsearch.search;

import java.util.List;

public class SearchEvent {
	private SearchEventType type;
	private List<Integer[]> state;
	private int violations;
	private int objective;
	
	public SearchEvent(SearchEventType type, List<Integer[]> state, int vio, int obj){
		this.type = type;
		this.state = state;
		this.violations = vio;
		this.objective = obj;
	}
	
	public SearchEventType getType(){
		return type;
	}
	
	public List<Integer[]> getState(){
		return state;
	}
	
	public int getViolations(){
		return violations;
	}
	
	public int getObjective(){
		return objective;
	}
	
}
