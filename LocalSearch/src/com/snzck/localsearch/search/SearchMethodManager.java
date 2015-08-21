package com.snzck.localsearch.search;

import localsearch.model.ConstraintSystem;

import com.snzck.localsearch.SearchMethod;
import com.snzck.localsearch.SearchTraceable;
import com.snzck.localsearch.search.method.SearchTabuAssign;
import com.snzck.localsearch.search.method.SearchTabuMixAssignSwap;
import com.snzck.localsearch.search.method.SearchTabuSwap;
import com.snzck.localsearch.system.Config;

public class SearchMethodManager {
	
	private Config config;
	
	public SearchMethodManager(Config config){
		this.config = config;
	}
	
	public SearchMethod getSearchMethod(ConstraintSystem cs, SearchTraceable tracer, SearchMethodType type){
		SearchMethod method = null;
		switch (type) {
		case TABU_ASSIGN_METHOD:
			method = new SearchTabuAssign(cs, tracer, config); 
			break;
		case TABU_SWAP_METHOD:
			method = new SearchTabuSwap(cs, tracer, config);
			break;
		case TABU_MIX_METHOD:
			method = new SearchTabuMixAssignSwap(cs, tracer, config);
			break;
		default:
			break;
		}
		return method;
	}
}
