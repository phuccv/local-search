package com.snzck.localsearch.binpacking2d.initstrage;

import localsearch.model.VarIntLS;

import com.snzck.localsearch.InitMethod;
import com.snzck.localsearch.SearchModel;

public class BpAllOutOfBinInitMethod implements InitMethod {

	private SearchModel model;
	
	public BpAllOutOfBinInitMethod(SearchModel model) {
		this.model = model;
	}
	
	@Override
	public void init() {
		VarIntLS[][] allVars = model.getStructuralVariables();
		
		// initial for all item position
		for(VarIntLS v : allVars[0]){
			v.setValuePropagate(v.getMaxValue());
		}
		
		// initial for all item rotation to NOT rotated
		for(VarIntLS v : allVars[1]){
			v.setValuePropagate(0);
		}
		
	}

	@Override
	public SearchModel getModel() {
		return model;
	}

	@Override
	public String getName() {
		return "All Out Bin";
	}

}
