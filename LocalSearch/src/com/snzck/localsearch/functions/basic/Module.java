package com.snzck.localsearch.functions.basic;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Module extends AbstractInvariant implements IFunction {

	private LocalSearchManager manager;
	
	private VarIntLS variable;
	
	private int value;
	
	private int mod;
	
	public Module(VarIntLS var, int mod) {
		this.variable = var;
		this.mod = mod;
		
		this.manager = var.getLocalSearchManager();
		manager.post(this);
	}
	
	@Override
	public LocalSearchManager getLocalSearchManager() {
		return manager;
	}

	@Override
	public VarIntLS[] getVariables() {
		VarIntLS[] vars = new VarIntLS[1];
		vars[0] = variable;
		return vars;
	}

	@Override
	public void initPropagate() {
		value = variable.getValue() % mod;
	}

	@Override
	public void propagateInt(VarIntLS var, int val) {
		if(var == variable){
			value = val % mod;
		}
	}

	@Override
	public boolean verify() {
		return false;
	}

	@Override
	public int getAssignDelta(VarIntLS var, int val) {
		int delta = 0;
		if(var == variable){
			delta = val % mod - value;
		}
		return delta;
	}

	@Override
	public int getMaxValue() {
		return variable.getMaxValue() % mod;
	}

	@Override
	public int getMinValue() {
		return variable.getMinValue() % mod;
	}

	@Override
	public int getSwapDelta(VarIntLS var, VarIntLS other) {
		int delta = 0;
		if(var == variable){
			delta = other.getValue() % mod - value;
		} else if(other == variable){
			delta = var.getValue() %mod - value;
		}
		return delta;
	}

	@Override
	public int getValue() {
		return value;
	}

}
