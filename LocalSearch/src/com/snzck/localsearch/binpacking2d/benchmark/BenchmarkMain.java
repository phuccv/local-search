package com.snzck.localsearch.binpacking2d.benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.snzck.localsearch.FileFormatException;
import com.snzck.localsearch.InitMethod;
import com.snzck.localsearch.SearchMethod;
import com.snzck.localsearch.SearchModel;
import com.snzck.localsearch.binpacking2d.initstrage.BpAllOutOfBinInitMethod;
import com.snzck.localsearch.binpacking2d.initstrage.BpAllZeroInitMethod;
import com.snzck.localsearch.binpacking2d.initstrage.BpRandomInitMethod;
import com.snzck.localsearch.binpacking2d.io.BpData;
import com.snzck.localsearch.binpacking2d.io.BpDataManager;
import com.snzck.localsearch.binpacking2d.model.BpModelCombine;
import com.snzck.localsearch.search.method.SearchTabuAssign;
import com.snzck.localsearch.search.method.SearchTabuMixAssignSwap;
import com.snzck.localsearch.system.Config;
import com.snzck.localsearch.system.ConfigManager;

public class BenchmarkMain {
	public static void main(String[] args) throws IOException, FileFormatException {

		// Benchmark 
		Config config = ConfigManager.INSTANT.getConfig();
		BpDataManager dataMng = new BpDataManager(config);
		
		FileWriter fw = new FileWriter("result.csv");
		BufferedWriter writer = new BufferedWriter(fw);
		final int RUN_COUNT = Integer.parseInt(config.getProperty("RUN_COUNTS"));
		
		int fileCount = dataMng.countData();
		for(int i = 0; i< fileCount; i++){
			BpData data = dataMng.getDataById(i);

			BpModelCombine model = new BpModelCombine(data);
			SearchMethod[] searchs = new SearchMethod[2];
			searchs[0] = new SearchTabuAssign(model.getConstraintSystem(), model, config);
			searchs[1] = new SearchTabuMixAssignSwap(model.getConstraintSystem(), model, config);
			InitMethod[] inits = new InitMethod[3];
			inits[0] = new BpAllZeroInitMethod(model);
			inits[1] = new BpAllOutOfBinInitMethod(model);
			inits[2] = new BpRandomInitMethod(model);
			System.out.println("[====================================================]\n"
					+ data.getFileName());
			for(int k = 0; k < 2; k++){
				SearchMethod search = searchs[k];
				System.out.println("Search: " + search.getClass().getSimpleName());
				for(int l = 0; l < 3; l++){
					long[] times = new long[RUN_COUNT];
					int[] violations = new int[RUN_COUNT]; 
					InitMethod init = inits[l];
					System.out.println("\tInit: " + init.getClass().getSimpleName());
					
					for(int j = 0; j< RUN_COUNT; j++){
						model.initVariables(init);
						long startTime = System.currentTimeMillis();
						search.run();
						times[j] = System.currentTimeMillis() - startTime;
						System.out.println("\t\t[-] Finished Run, time: " + times[j]);
						violations[j] = model.getConstraintSystem().violations();
					}
					writer.write(data.getFileName() + ",");
					writer.write(search.getClass().getSimpleName() + ",");
					writer.write(init.getClass().getSimpleName());
					for(int j = 0; j< RUN_COUNT; j++){
						writer.write("," + times[j]);
						writer.write("," + violations[j]);
					}
					writer.write("\n");
				}
			}
		}
		writer.close();
		System.out.println("OK");
	}
	
}
