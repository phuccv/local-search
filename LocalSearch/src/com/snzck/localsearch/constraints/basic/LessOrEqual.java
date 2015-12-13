package com.snzck.localsearch.constraints.basic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.print.attribute.standard.MediaSize.Other;

import localsearch.model.AbstractInvariant;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class LessOrEqual extends AbstractInvariant implements IConstraint {

	/*
	 * For case:
	 * 0. two variable
	 * 1. two function
	 * 2. one function and one number
	 */
	int useCase;
	
	//for two Function
	private IFunction func;
	private IFunction funcOther;
	
	private Set<VarIntLS> vars;
	private Set<VarIntLS> varsOther;
	
	// for constant number
	private int value;
	private int valueOther;
	
	private LocalSearchManager manager;
	
	private int violation;
	private VarIntLS[] allVars;
	
	public LessOrEqual(VarIntLS var, VarIntLS other){
		
		useCase = 0;
		
		allVars = new VarIntLS[2];
		allVars[0] = var;
		allVars[1] = other;
		
		vars = new HashSet<>();
		vars.add(var);
		varsOther = new HashSet<>();
		varsOther.add(var);
		
		this.manager = var.getLocalSearchManager();
		manager.post(this);
	}
	
	public LessOrEqual(IFunction fun1, IFunction fun2) {
		useCase = 1;
		
		this.func = fun1;
		this.funcOther = fun2;
		
		this.manager = fun1.getLocalSearchManager();
		
		vars = new HashSet<>();
		vars.addAll(Arrays.asList(func.getVariables()));
		varsOther = new HashSet<>();
		varsOther.addAll(Arrays.asList(funcOther.getVariables()));
		
		Set<VarIntLS> allVarSet = new HashSet<>();
		allVarSet.addAll(vars);
		allVarSet.addAll(varsOther);
		
		allVars = allVarSet.toArray(new VarIntLS[0]);
		manager.post(this);
	}
	
	public LessOrEqual(IFunction func, int privot) {
		useCase = 2;
		
		this.func = func;
		this.valueOther = privot;
		
		vars = new HashSet<>();
		vars.addAll(Arrays.asList(func.getVariables()));
		varsOther = new HashSet<>();
		
		this.manager = func.getLocalSearchManager();
		
		this.allVars = func.getVariables();
		manager.post(this);
	}
	
	@Override
	public LocalSearchManager getLocalSearchManager() {
		return manager;
	}

	@Override
	public VarIntLS[] getVariables() {
		return allVars;
	}

	@Override
	public void initPropagate() {
		switch (useCase) {
		case 0:
			value = allVars[0].getValue();
			valueOther = allVars[1].getValue();
			break;
			
		case 1:
			value = func.getValue();
			valueOther = func.getValue();
			break;
			
		case 2:
			value = func.getValue();
			break;

		default:
			System.out.println("LessOrEqual: found bug");
			break;
		}
		
		if(value <= valueOther){
			violation = 0;
		} else {
			violation = value - valueOther;
		}
		
	}

	@Override
	public void propagateInt(VarIntLS varPro, int val) {
		switch (useCase) {
		case 0: // two VarIntLS
			value = allVars[0].getValue();
			valueOther = allVars[1].getValue();
			break;

		case 1: // two Function
			value = func.getValue();
			valueOther = funcOther.getValue();
			break;

		case 2:
			value = func.getValue();
			break;

		default:
			break;
		}
		
		if(value <= valueOther){
			violation = 0;
		} else {
			violation = value - valueOther;
		}
	}

	@Override
	public boolean verify() {
		return false;
	}

	@Override
	public int getAssignDelta(VarIntLS varGet, int val) {
		int delta = 0;
		int delta1 = 0;
		int delta2 = 0;
		switch (useCase) {
		case 0:
			if(varGet == allVars[0]){
				delta1 = val - value;
			}
			if(varGet == allVars[1]){
				delta2 = val - valueOther;
			}
			break;

		case 1:
			delta1 = func.getAssignDelta(varGet, val);
			delta2 = funcOther.getAssignDelta(varGet, val);
			break;

		case 2:
			delta1 = func.getAssignDelta(varGet, val);
			delta2 = 0;
			break;

		default:
			System.out.println("Not in case");
			break;
		}
		
		if(value + delta1 <= valueOther + delta2){
			delta = -violation;
		} else {
			delta = value + delta1 - valueOther - delta2;
		}
		
		return delta;
	}

	@Override
	public int getSwapDelta(VarIntLS v, VarIntLS vOther) {
		int delta = 0;
		int delta1 = 0;
		int delta2 = 0;
		switch (useCase) {
		case 0:
			if(v == allVars[0]){
				delta1 = vOther.getValue() - value;
			} else if(vOther == allVars[0]){
				delta1 = v.getValue() - value;
			}
			if(v == allVars[1]){
				delta2 = vOther.getValue() - valueOther;
			}else if(vOther == allVars[1]){
				delta2 = v.getValue() - valueOther;
			}
			break;

		case 1:
			delta1 = func.getSwapDelta(v, vOther);
			delta2 = funcOther.getSwapDelta(v, vOther);
			break;

		case 2:
			delta1 = func.getSwapDelta(v, vOther);
			delta2 = 0;
			break;

		default:
			System.out.println("Not in case");
			break;
		}

		if(value + delta1 <= valueOther + delta2){
			delta = -violation;
		} else {
			delta = value + delta1 - valueOther - delta2;
		}
		
		return delta;
	}

	@Override
	public int violations() {
		return violation;
	}

	@Override
	public int violations(VarIntLS query) {
		if(vars.contains(query) || varsOther.contains(query)){
			return violation;
		}
		return 0;
	}

}
