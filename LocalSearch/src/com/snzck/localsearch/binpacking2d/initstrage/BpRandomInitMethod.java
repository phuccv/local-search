package com.snzck.localsearch.binpacking2d.initstrage;

import java.util.Random;

import localsearch.model.VarIntLS;

import com.snzck.localsearch.InitMethod;
import com.snzck.localsearch.SearchModel;

public class BpRandomInitMethod implements InitMethod {

	private SearchModel model;
	private Random random;
	
	private static String name = "Random";
	
	public BpRandomInitMethod(SearchModel model) {
		this.model = model;
		random = new Random();
	}
	
	@Override
	public void init() {
		VarIntLS[] vars = model.getVariables();
		for(int i = 0; i < vars.length; i++){
			int len = vars[i].getMaxValue() - vars[i].getMinValue();
			vars[i].setValuePropagate(random.nextInt(len) + vars[i].getMinValue());
		}
	}

	@Override
	public SearchModel getModel() {
		return model;
	}

	@Override
	public String getName() {
		return name;
	}

}
