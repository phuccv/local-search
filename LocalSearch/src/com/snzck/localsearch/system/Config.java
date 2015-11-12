package com.snzck.localsearch.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.SynchronousQueue;

import com.snzck.localsearch.binpacking2d.BpConfigField;
import com.snzck.localsearch.search.SearchConfigField;

public class Config extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Config() {
		setDefault();
	}
	
	protected Config(File configFile) throws FileNotFoundException, IOException{
		super();
		// Load default configuration
		setDefault();
		
		//TODO Load system configure
		
		// Load file configure
		load(new FileInputStream(configFile));
		
		//TODO Load parameter
	}
	
	private void setDefault(){
		// System field
		for(SystemConfigField f : SystemConfigField.values()){
			setProperty(f.name(), f.getDefaultValue());
		}
		
		// Search field
		for(SearchConfigField f : SearchConfigField.values()){
			setProperty(f.name(), f.getDefaultValue());
		}

		// Bp2D field
		for(BpConfigField f : BpConfigField.values()){
			setProperty(f.name(), f.getDefaultValue());
		}
		
		
	}
	
}
