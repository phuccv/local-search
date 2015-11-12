package com.snzck.localsearch.search.method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snzck.localsearch.SearchMethod;
import com.snzck.localsearch.SearchTraceable;
import com.snzck.localsearch.search.SearchConfigField;
import com.snzck.localsearch.system.Config;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

public class SearchTabuSwap implements SearchMethod {
	
	private static Logger logger = LoggerFactory.getLogger(SearchTabuSwap.class);
	
	private boolean isRunning;
	
	private IConstraint constraintSystem;
	
	private SearchTraceable tracer;
	private Config config;
	
	private VarIntLS[] variablesList;
	
	private int[] variablesValueDomain;
	
	/**
	 * Construct new TabuSearchImplement with constraint system
	 * @param constraintSystem constraint system
	 */
	public SearchTabuSwap(IConstraint cs, SearchTraceable tracer, Config config){
		// check constrain system valid
		if(cs == null){
			logger.error("Constructor with null constraint system");
			return;
		}
		
		// Assign to property
		this.tracer = tracer;
		this.config = config;
		this.constraintSystem = cs;
		this.variablesList = constraintSystem.getVariables();
		if(variablesList == null){
			logger.error("Constructor: constraint system invalid -- no vairable list");
			return;
		}
		variablesValueDomain = new int[variablesList.length];
		for(int i = 0; i< variablesList.length; i++){
			VarIntLS tmpVar = variablesList[i];
			variablesValueDomain[i] = tmpVar.getMaxValue() - tmpVar.getMinValue() + 1;
		}
	}
	
	
	public int searchSwap(int tabuLength, int maxTimeSeconds, 
			int maxIterator, int maxStable, int maxResetForOneLocal){
		
		int iterator = 0;	// iterator
		int nonStableCount = 0; // non-stable step counting
		long endTime = System.currentTimeMillis() 
				+ maxTimeSeconds * 1000; // end clock time
		int tabuTable[][] = new int[variablesList.length][]; // taboo list
		for(int i = 0; i< variablesList.length; i++){
			tabuTable[i] = new int[variablesList.length];
			for(int j = 0; j< variablesList.length; j++){
				tabuTable[i][j] = -tabuLength;
			}
		}
		int localBestViolation = constraintSystem.violations(); // local best
		int localBest[] = new int[variablesList.length]; // local best value list
		int globalBestViolation = localBestViolation;
		int globalBest[] = new int[variablesList.length];
		for(int i = 0; i< variablesList.length; i++){
			localBest[i] = variablesList[i].getValue();
			globalBest[i] = localBest[i];
		}
		PairListStore bestMove = new PairListStore();// save best move
		int minDelta = 0; // minimum delta on bestMove list
		Random random = new Random(); // random factor
		int numReset = 0; // number of reset that not reduce local best
		
		/*
		 * Main loop: while constraint > 0 and time run < maxTime, and loop count
		 * < maxLoopCount
		 */
		while(iterator++ < maxIterator && System.currentTimeMillis() < endTime
				&& globalBestViolation > 0 && isRunning){
			
			/*
			 * Reset all move, delta
			 */
			bestMove.clear();
			minDelta = Integer.MAX_VALUE;
			
			/*
			 * Get all movable
			 */
			for(int i = 0; i< variablesList.length; i++){ // for all variable list
				VarIntLS var = variablesList[i];
				for(int j = i + 1; j < variablesList.length; j++){ // for all other variable
					VarIntLS otherVar = variablesList[j];
					
					//Skip swap without effective
					if(var.getValue() == otherVar.getValue()){
						continue;
					}
					
					int checkDelta = constraintSystem.getSwapDelta(var, otherVar);
					
					// check variable swapable
					if((tabuTable[i][j] + tabuLength > iterator
							&& tabuTable[j][i] + tabuLength > iterator)){
						if(constraintSystem.violations() + checkDelta >= localBestViolation){
							continue; // search for other swapping
						}
						
					}
					
					if(checkDelta < minDelta ){
						minDelta = checkDelta;
						bestMove.clear();
						bestMove.add(i, j);
					} else if(checkDelta == minDelta){
						bestMove.add(i, j);
					}
				}
			}
			
			/*
			 * Check move legal
			 */
			if(bestMove.isEmpty()){// no movable
				numReset++;
				nonStableCount = 0;
				if(numReset > maxResetForOneLocal){ // do big reset
					logger.info("searchSwap: Big reseting...");
					ArrayList<Integer> varValues = new ArrayList<>();
					for(int i = 0; i< variablesList.length; i++){
						varValues.add(variablesList[i].getValue());
					}
					Collections.shuffle(varValues);
					for(int i = 0; i< variablesList.length; i++){
						variablesList[i].setValuePropagate(varValues.get(i));
						for(int j = 0; j< variablesValueDomain[i]; j++){
							tabuTable[i][j] = Integer.MIN_VALUE;
						}
					}
					localBestViolation = constraintSystem.violations();
					numReset = 0;
					continue;
				}
				
				logger.info("searchSwap: No more movable!  Reseting " + numReset + " ...");
				for(int i = 0; i< variablesList.length; i++){
					variablesList[i].setValuePropagate(localBest[i]);
				}
				continue;
			}
			if(minDelta >= 0){
				nonStableCount ++;
				if(nonStableCount > maxStable){ // non-stable over threshold reset
					numReset++;
					nonStableCount = 0;
					if(numReset > maxResetForOneLocal){ // do big reset
						logger.info("searchSwap: Big reseting...");
						ArrayList<Integer> varValues = new ArrayList<>();
						for(int i = 0; i< variablesList.length; i++){
							varValues.add(variablesList[i].getValue());
						}
						Collections.shuffle(varValues);
						for(int i = 0; i< variablesList.length; i++){
							variablesList[i].setValuePropagate(varValues.get(i));
							for(int j = 0; j< variablesValueDomain[i]; j++){
								tabuTable[i][j] = Integer.MIN_VALUE;
							}
						}
						localBestViolation = constraintSystem.violations();
						numReset = 0;
						continue;
					}
					
					logger.info("searchSwap: Reseting " + numReset + " ...");
					for(int i = 0; i< variablesList.length; i++){
						variablesList[i].setValuePropagate(localBest[i]);
					}
				}
			} else if(minDelta < 0) {
//				nonStableCount = 0;
			}
			
			/*
			 * Perform swap variable
			 */
			int randomSituation = random.nextInt(bestMove.size);
			int variableId = bestMove.values[randomSituation];
			int otherVariableId = bestMove.otherValues[randomSituation];
			VarIntLS var = variablesList[variableId];
			VarIntLS otherVar = variablesList[otherVariableId];
			int otherVarValue = otherVar.getValue();
			// setting value and update tabuList
			otherVar.setValuePropagate(var.getValue());
			tabuTable[otherVariableId][variableId] = iterator;
			var.setValuePropagate(otherVarValue);
			tabuTable[variableId][otherVariableId] = iterator;
			
			// update best
			if(constraintSystem.violations() < localBestViolation){// update local best
				numReset = 0; // set numReset = 0 when new local bester was founded
				localBestViolation = constraintSystem.violations();
				for(int i = 0; i< variablesList.length; i++){
					localBest[i] = variablesList[i].getValue();
				}
				if(localBestViolation < globalBestViolation){ // updat global
					globalBestViolation = localBestViolation;
					for(int i = 0; i< variablesList.length; i++){
						globalBest[i] = localBest[i];
					}
				}
			}
			
			/*
			 * Print move
			 */
			if(VERBOSE){
				logger.info("Iter: " + iterator 
						+ "\tViolation: " + constraintSystem.violations() 
						+ "\tLocalVio: " + localBestViolation
						+ "\tGlobalVio: " + globalBestViolation
						+ "\tDelta: " + minDelta
						+ "\tSwap: " + variableId + "-" + otherVariableId
						+ "\tNic: " + nonStableCount);
			}
			
		}
		
		/*
		 * set variable to global best
		 */
		for(int i = 0 ; i < variablesList.length; i++){
			variablesList[i].setValuePropagate(globalBest[i]);
		}
		
		return iterator;
		
	}
	
	public IConstraint getConstraintSystem() {
		return constraintSystem;
	}

	public void setConstraintSystem(IConstraint constraintSystem) {
		this.constraintSystem = constraintSystem;
	}


	@Override
	public void stop() {
		isRunning = false;
	}
	
	@Override
	public void run() {
		isRunning = true;
		int tabuLength = Integer.valueOf(config.getProperty(SearchConfigField.TABU_LENGTH.name(),
				SearchConfigField.TABU_LENGTH.getDefaultValue()));
		int maxTimeSeconds = Integer.valueOf(config.getProperty(SearchConfigField.MAX_TIME.name(),
				SearchConfigField.MAX_TIME.getDefaultValue()));
		int maxIterator = Integer.valueOf(config.getProperty(SearchConfigField.MAX_ITERATOR.name(),
				SearchConfigField.MAX_ITERATOR.getDefaultValue()));
		int maxStable = Integer.valueOf(config.getProperty(SearchConfigField.MAX_STABE.name(),
				SearchConfigField.MAX_STABE.getDefaultValue()));
		int maxResetForOneLocal = Integer.valueOf(config.getProperty(SearchConfigField.MAX_LOCAL_RESET.name(),
				SearchConfigField.MAX_LOCAL_RESET.getDefaultValue()));
		searchSwap(tabuLength, maxTimeSeconds, maxIterator, maxStable, maxResetForOneLocal);
	}


	/**
	 * Mark up variable moving
	 * @author phuc
	 *
	 */
	class PairListStore{
		
		public static final int MAX_MOVECOUNT = 10000;
		
		public int size;
		/**
		 * Variable index on variable array
		 */
		public int[] otherValues;
		
		/**
		 * Value want assign to variable 
		 */
		public int[] values;
		
		public PairListStore(){
			size = 0;
			this.otherValues = new int[MAX_MOVECOUNT];
			this.values = new int[MAX_MOVECOUNT];
		}
		
		public int getValue(int id){
			if(id < size){
				return values[id];
			}
			return 0;
		}
		
		public int getOtherValue(int id){
			if(id < size){
				return otherValues[id];
			}
			return 0;
		}
		
		public void clear(){
			size = 0;
		}
		
		public void add(int variableId, int value){
			if(size >= MAX_MOVECOUNT){
				return;
			}
			otherValues[size] = variableId;
			values[size++] = value;
		}
		
		public boolean isEmpty(){
			return size <= 0;
		}
	}

}
