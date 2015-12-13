package com.snzck.localsearch.binpacking2d.io;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.snzck.localsearch.FileFormatException;

/**
 * Storage data for bin packing 2d problem
 * @author phuc
 *
 */
public class BpData {
	private File file;
	private boolean isReady;
	private int binWidth;
	private int binHeight;
	private int itemCount;
	
	private int[] itemWidths;
	private int[] itemHeights;
	
	public BpData(File file){
		this.file = file;
	}
	
	private void readData() throws IOException, FileFormatException{
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		// read first line
		String line = reader.readLine();
		if(line == null){
			reader.close();
			throw new FileFormatException("Empty file");
		}
		String[] splited = line.split(" ");
		if(splited.length < 2){
			reader.close();
			throw new FileFormatException("Require width and height on first line");
		}
		try {
			binWidth = Integer.valueOf(splited[0]);
			binHeight = Integer.valueOf(splited[1]);
		} catch (NumberFormatException e) {
			reader.close();
			throw new FileFormatException("Invalid width or height");
		}
		
		// Read item list.
		ArrayList<Integer> widthList = new ArrayList<>();
		ArrayList<Integer> heightList = new ArrayList<>();
		while(true){
			line = reader.readLine();
			if(line == null){
				break;
			}
			splited = line.split(" ");
			if(splited.length < 2){
				reader.close();
				throw new FileFormatException("Invalid item size");
			}
			try {
				widthList.add(Integer.valueOf(splited[0]));
				heightList.add(Integer.valueOf(splited[1]));
			} catch (NumberFormatException e) {
				reader.close();
				throw new FileFormatException("Invalid width or height");
			}
		}
		itemCount = widthList.size();
		itemWidths = new int[itemCount];
		itemHeights = new int[itemCount];
		for(int i = 0; i< itemCount; i++){
			itemWidths[i] = widthList.get(i);
			itemHeights[i] = heightList.get(i);
		}
		
		// sort item list by square
		int tmpSwap = 0;
		int[] squareItems = new int[itemCount];
		for(int i = 0; i< itemCount; i++){
			squareItems[i] = itemWidths[i] * itemHeights[i];
		}
		for(int i =0; i< itemCount; i++){
			for(int j=i + 1; j< itemCount; j++){
				if(squareItems[i] < squareItems[j]){
					tmpSwap = itemWidths[i];
					itemWidths[i] = itemWidths[j];
					itemWidths[j] = tmpSwap;
					tmpSwap = itemHeights[i];
					itemHeights[i] = itemHeights[j];
					itemHeights[j] = tmpSwap;
				}
			}
		}
		
		
		reader.close();
		isReady = true;
	}

	public String getFileName(){
		return file.getName();
	}

	public boolean isReady() throws IOException, FileFormatException {
		if(! isReady){
			readData();
		}
		return isReady;
	}

	public int getBinWidth() throws IOException, FileFormatException {
		if(! isReady){
			readData();
		}
		return binWidth;
	}

	public int getBinHeight() throws IOException, FileFormatException {
		if(! isReady){
			readData();
		}
		return binHeight;
	}

	public int getItemCount() throws IOException, FileFormatException {
		if(! isReady){
			readData();
		}
		return itemCount;
	}

	public int[] getItemWidths() {
		return itemWidths;
	}

	public int[] getItemHeights() {
		return itemHeights;
	}

	
}
