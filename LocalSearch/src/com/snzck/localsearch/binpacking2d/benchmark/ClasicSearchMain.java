package com.snzck.localsearch.binpacking2d.benchmark;

import java.io.IOException;

import com.snzck.localsearch.FileFormatException;
import com.snzck.localsearch.binpacking2d.initstrage.BpRandomInitMethod;
import com.snzck.localsearch.binpacking2d.io.BpData;
import com.snzck.localsearch.binpacking2d.io.BpDataManager;
import com.snzck.localsearch.binpacking2d.model.BpModelCombine;
import com.snzck.localsearch.system.ConfigManager;

public class ClasicSearchMain {
	public static void main(String[] args) throws IOException, FileFormatException {
		BpDataManager bdm = new BpDataManager(ConfigManager.INSTANT.getConfig());
		BpData data = bdm.getDataById(0);
		
		BpModelCombine model = new BpModelCombine(data);
		
		model.initVariables(new BpRandomInitMethod(model));
		
		model.oldSearchMethod();
	}
}
