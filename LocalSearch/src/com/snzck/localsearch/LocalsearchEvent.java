package com.snzck.localsearch;

import java.util.HashMap;

public class LocalsearchEvent {
	public static final String[] EVENT_NAME_LIST = {
		"init",
		"foundLocal",
		"foundGlobal",
		"localReset",
		"globalReset",
		"finish",
		"invalid"
	};
	
	public static final int EVENT_INIT = 0;
	public static final int EVENT_FOUND_LOCAL = 1;
	public static final int EVENT_FOUND_GLOBAL = 2;
	public static final int EVENT_LOCAL_RESET = 3;
	public static final int EVENT_GLOBAL_RESET = 4;
	public static final int EVENT_FINISH = 5;
	public static final int EVENT_INVALID = 6;
	
	private int eventId;
	
	private HashMap<String, Integer[]> variablesValues;
	private int violations;
	private int objectiveValue;
	
	public LocalsearchEvent(int eventId, HashMap<String, Integer[]> variablesValues, int violations, int objective){
		
		this.eventId = eventId;
		this.variablesValues = variablesValues;
		this.violations = violations;
		this.objectiveValue = objective;
		
		if(eventId < 0 || eventId >= EVENT_INVALID){
			this.eventId = EVENT_INVALID;
		}
	}
	
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public HashMap<String, Integer[]> getVariablesValues() {
		return variablesValues;
	}
	public void setVariablesValues(HashMap<String, Integer[]> variablesValues) {
		this.variablesValues = variablesValues;
	}
	public int getViolations() {
		return violations;
	}
	public void setViolations(int violations) {
		this.violations = violations;
	}
	public int getObjectiveValue() {
		return objectiveValue;
	}
	public void setObjectiveValue(int objectiveValue) {
		this.objectiveValue = objectiveValue;
	}
	
}

