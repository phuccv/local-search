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

public class RequireAll extends AbstractInvariant implements IConstraint {

	private int violations;
	
	private IConstraint[] constraints;
	
	private LocalSearchManager manager;
	private VarIntLS[] allVars;
	
	private Set<VarIntLS> allVarSet;
	private List<Set<VarIntLS>> consVarSet;
	
	public RequireAll(IConstraint[] cons){
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
		
		allVars = allVarSet.toArray(new VarIntLS[0]);
		
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
		violations = 0;
		for(IConstraint c : constraints){
			violations += c.violations();
		}
	}

	@Override
	public void propagateInt(VarIntLS varPro, int val) {
		if(allVarSet.contains(varPro)){
			violations = 0;
			for(IConstraint c : constraints){
				violations += c.violations();
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
		for(int i = 0; i< consVarSet.size(); i++){
			if(consVarSet.get(i).contains(varGet)){
				delta += constraints[i].getAssignDelta(varGet, val);
			}
		}
		return delta;
	}

	@Override
	public int getSwapDelta(VarIntLS var, VarIntLS other) {
		int delta = 0;
		for(int i = 0; i< consVarSet.size(); i++){
			Set<VarIntLS> s = consVarSet.get(i);
			if(s.contains(var) || s.contains(other)){
				delta += constraints[i].getSwapDelta(var, other);
			}
		}
		return delta;
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
