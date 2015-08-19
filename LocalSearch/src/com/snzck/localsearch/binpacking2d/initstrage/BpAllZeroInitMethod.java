package com.snzck.localsearch.binpacking2d.initstrage;

import localsearch.model.VarIntLS;

import com.snzck.localsearch.InitMethod;
import com.snzck.localsearch.SearchModel;

public class BpAllZeroInitMethod implements InitMethod{

	private SearchModel model;
	
	public BpAllZeroInitMethod(SearchModel model){
		this.model = model;
	}
	
	@Override
	public void init() {
		VarIntLS[] vars = model.getVariables();
		for(VarIntLS v : vars){
			v.setValuePropagate(0);
		}
	}

	@Override
	public SearchModel getModel() {
		return model;
	}

	@Override
	public String getName() {
		return "ALL_ZERO";
	}
	
}
