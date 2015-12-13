package com.snzck.localsearch.constraints.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import localsearch.model.AbstractInvariant;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class RequireLeastOne extends AbstractInvariant implements IConstraint {

	private int violations;
	
	private IConstraint[] constraints;
	
	private LocalSearchManager manager;
	private VarIntLS[] allVars;
	
	private Set<VarIntLS> allVarSet;
	private List<Set<VarIntLS>> consVarSet;
	
	public RequireLeastOne(IConstraint[] cons){
		this.constraints = cons;
		manager = cons[0].getLocalSearchManager();

		allVarSet = new HashSet<>();
		consVarSet = new ArrayList<>();
		for(int i = 0; i< cons.length; i++){
			Set<VarIntLS> s = new HashSet<>();
			s.addAll(Arrays.asList(cons[i].getVariables()));
			allVarSet.addAll(s);
			consVarSet.add(s);
		}
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
		violations = Integer.MAX_VALUE;
		for(IConstraint c : constraints){
			if(violations > c.violations()){
				violations = c.violations();
				if(violations == 0){
					break;
				}
			}
		}
	}

	@Override
	public void propagateInt(VarIntLS varPro, int val) {
		if(allVarSet.contains(varPro)){
			violations = Integer.MAX_VALUE;
			for(IConstraint c : constraints){
				if(violations > c.violations()){
					violations = c.violations();
					if(violations == 0){
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean verify() {
		return false;
	}

	@Override
	public int getAssignDelta(VarIntLS varGet, int val) {
		int newVio = Integer.MAX_VALUE;
		for(int i = 0; i< consVarSet.size(); i++){
			if(consVarSet.get(i).contains(varGet)){
				int d = constraints[i].getAssignDelta(varGet, val);
				
				if(newVio < d){
					newVio = d;
					if(newVio == 0){
						break;
					}
				}
			}
		}
		return newVio -violations;
	}

	@Override
	public int getSwapDelta(VarIntLS var, VarIntLS other) {
		int newVio = Integer.MAX_VALUE;
		for(int i = 0; i< consVarSet.size(); i++){
			Set<VarIntLS> vars = consVarSet.get(i);
			if(vars.contains(var) || vars.contains(other)){
				int d = constraints[i].getSwapDelta(var, other);
				
				if(newVio < d){
					newVio = d;
					if(newVio == 0){
						break;
					}
				}
			}
		}
		return newVio -violations;
	}

	@Override
	public int violations() {
		return violations;
	}

	@Override
	public int violations(VarIntLS arg0) {
		if(allVarSet.contains(arg0)){
			return violations;
		}
		return 0;
	}

}
