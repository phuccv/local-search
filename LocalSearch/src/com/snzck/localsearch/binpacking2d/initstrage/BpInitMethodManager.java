package com.snzck.localsearch.binpacking2d.initstrage;

import com.snzck.localsearch.InitMethod;
import com.snzck.localsearch.SearchModel;
import com.snzck.localsearch.system.Config;
import com.snzck.localsearch.system.ConfigManager;

public class BpInitMethodManager {
	
	private Config config;
	
	public BpInitMethodManager(Config config){
		this.config = config;
	}
	
	public InitMethod getInitMethod(SearchModel model, BpInitMethodType type){
		InitMethod method = null;
		switch (type) {
		case RANDOM:
			method = new BpRandomInitMethod(model);
			break;

		case ALL_ZERO:
			method = new BpAllZeroInitMethod(model);
			break;
			
		case ALL_OUT:
			method = new BpAllOutOfBinInitMethod(model);
			break;
			
		default:
			break;
		}
		return method;
	}
	
}
