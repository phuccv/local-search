package com.snzck.localsearch.binpacking2d.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.snzck.localsearch.binpacking2d.BpConfigField;
import com.snzck.localsearch.system.Config;

public class BpDataManager {
	private HashMap<String, BpData> data;
	private ArrayList<BpData> dataList;
	
	private Config config;
	
	public BpDataManager(Config config){
		this.config = config;
	
		data = new HashMap<>();
		dataList = new ArrayList<>();
		
		String dataFolder = config.getProperty(BpConfigField.BIN_PACKING_2D_DATA_FOLDER.name());
		
		File folder = new File(dataFolder);
		for(File f : folder.listFiles()){
			BpData bpd =  new BpData(f);
			data.put(f.getName(), bpd);
			dataList.add(bpd);
		}
	}
	
	public BpData getDataByName(String fileName){
		return data.get(fileName);
	}
	
	public BpData getDataById(int id){
		return dataList.get(id);
	}
	
	public int countData(){
		return data.size();
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	
	
	
}
