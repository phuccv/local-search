package com.snzck.localsearch.constraints.basic;

import localsearch.model.AbstractInvariant;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class IsEqual extends AbstractInvariant implements IConstraint {

	private VarIntLS var;
	private int privot;
	
	private VarIntLS[] vars;
	private LocalSearchManager manager;
	private int violation;
	
	public IsEqual(VarIntLS var, int privot){
		this.var = var;
		this.manager = var.getLocalSearchManager();
		this.privot = privot;
		
		vars = new VarIntLS[1];
		vars[0] = var;
		
		manager.post(this);
	}
	
	@Override
	public LocalSearchManager getLocalSearchManager() {
		return manager;
	}

	@Override
	public VarIntLS[] getVariables() {
		return vars;
	}

	@Override
	public void initPropagate() {
		violation = var.getValue() - privot;
		if(violation < 0){
			violation = -violation;
		}
	}

	@Override
	public void propagateInt(VarIntLS varPro, int val) {
		if(varPro == var){
			violation = val - privot;
			if(violation < 0){
				violation = -violation;
			}
		}

	}

	@Override
	public boolean verify() {
		return false;
	}

	@Override
	public int getAssignDelta(VarIntLS varGet, int val) {
		int delta = 0;
		if(var == varGet){
			delta = val - privot;
			if(delta < 0){
				delta = -delta;
			}
			delta = delta - violation;
		}
		return delta;
	}

	@Override
	public int getSwapDelta(VarIntLS var, VarIntLS other) {
		int delta = 0;
		if(this.var == var){
			delta = other.getValue() - privot;
			if(delta < 0){
				delta = -delta;
			}
			delta = delta - violation;
		} else if(this.var == other){
			delta = var.getValue() -privot;
			if(delta < 0){
				delta = -delta;
			}
			delta = delta - violation;
		}
		return delta;
	}

	@Override
	public int violations() {
		return violation;
	}

	@Override
	public int violations(VarIntLS varGet) {
		if(varGet == var){
			return violation;
		}
		return 0;
	}

}
