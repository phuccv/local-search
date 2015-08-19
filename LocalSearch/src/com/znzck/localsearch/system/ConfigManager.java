package com.znzck.localsearch.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public enum ConfigManager {
	INSTANT;
	
	private Config config;
	
	private ConfigManager(){
		try {
			this.config = new Config(new File("config.properties"));
		} catch (IOException e) {
			System.out.println("Not found config file. Load default.");
			config = new Config();
		}
	}
	
	public Config getConfig(){
		return config;
	}
	
	public void load(String file) throws FileNotFoundException, IOException{
		config.load(new FileInputStream(file));
	}
	
	public Config getConfigByFile(String file) throws FileNotFoundException, IOException{
		return new Config(new File(file));
	}
}
