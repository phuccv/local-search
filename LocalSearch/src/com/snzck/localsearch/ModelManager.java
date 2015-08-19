package com.snzck.localsearch;

import java.io.IOException;

import com.snzck.localsearch.binpacking2d.io.BpData;
import com.snzck.localsearch.binpacking2d.model.BpModelCombine;
import com.snzck.localsearch.binpacking2d.model.BpModelType;

public class ModelManager {
	
	
	public SearchModel getModel(BpModelType type, BpData data) throws IOException, FileFormatException{
		if(type == BpModelType.COMBINED){
			return new BpModelCombine(data);
		}
		return null;//TODO
	}
}
