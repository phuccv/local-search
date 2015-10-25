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

public class SearchTabuMixAssignSwap implements SearchMethod {
	
	private static Logger logger = LoggerFactory.getLogger(SearchTabuMixAssignSwap.class);
	
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
	public SearchTabuMixAssignSwap(IConstraint cs, SearchTraceable tracer, Config config){
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
	
	/**
	 * Execute taboo search
	 * @param tabuLength number of step avoid move again
	 * @param maxTimeSeconds maximum time run
	 * @param maxIterator maximum iterator
	 * @param maxStable maximum stable state before restart
	 * @return 
	 */
	private int searchMix(int tabuLength, int maxTimeSeconds, 
			int maxIterator, int maxStable, int maxResetForOneLocal){
		int iterator = 0;	// iterator
		int nonStableCount = 0; // non-stable step counter
		long endTime = System.currentTimeMillis() 
				+ maxTimeSeconds * 1000; // end clock time
		int tabuSwap[][] = new int[variablesList.length][]; // taboo list assign
		int tabuAssign[][] = new int[variablesList.length][]; // taboo list assign
		for(int i = 0; i< variablesList.length; i++){
			tabuSwap[i] = new int[variablesList.length];
			tabuAssign[i] = new int[variablesValueDomain[i]];
			for(int j = 0; j< variablesList.length; j++){
				tabuSwap[i][j] = -tabuLength;
			}
			for(int j = 0; j< variablesValueDomain[i]; j++){
				tabuAssign[i][j] = -tabuLength;
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
		
		boolean isSwap = false;
		
		
		/*
		 *  main loop: loop while constraint violation not equal zero and time
		 *  less than max time, and iterator count less than max iterator
		 */
		while(iterator++ < maxIterator && System.currentTimeMillis() < endTime
				&& globalBestViolation > 0 && isRunning){
			
			/*
			 * Reset all delta, bestMove
			 */
			minDelta = Integer.MAX_VALUE;
			isSwap = false;
			bestMove.clear();
			
			/*
			 * Search all possible swap
			 */
			for(int i = 0; i < variablesList.length; i++){
				for(int j = i+1 ; j < variablesList.length; j++){
					
					// Check taboo
					if(tabuSwap[i][j] + tabuLength > iterator){
						continue;
					}
					
					// check delta
					int deltaCheck = constraintSystem.getSwapDelta(variablesList[i], variablesList[j]);
					if( deltaCheck < minDelta){
						bestMove.clear();
						minDelta = deltaCheck;
						bestMove.add(i, j);
						isSwap = true;
					} else if(deltaCheck == minDelta && isSwap){
						bestMove.add(i, j);
					}
				}
			}

			/*
			 *  search all legal neighborhood for assign
			 */
			for(int i = 0; i< variablesList.length; i++){
				VarIntLS variable = variablesList[i];
				/*
				 *  search all value assignable to variable
				 */
				for(int j = 0; j< variablesValueDomain[i]; j++){
					int checkValue = variable.getMinValue() + j;
					
					/*
					 * Check movable from taboo list: if not movable then 
					 * check on  other value
					 */
					if(tabuAssign[i][checkValue -variable.getMinValue()] + tabuLength > iterator){
						continue;
					}
					
					/*
					 * Check delta of this assign, if new best then generate
					 * new list of movable.
					 */
					int deltaCheck = 
							constraintSystem.getAssignDelta(variable, checkValue);
					if( deltaCheck < minDelta){ // found new best assign
						System.out.println("new best: " + deltaCheck);
						bestMove.clear();
						minDelta = deltaCheck;
						bestMove.add(i, checkValue);
						isSwap = false;
					} else if (deltaCheck == minDelta && !isSwap){ // found same best assign
						bestMove.add(i, checkValue);
					}
				}
			}
			
			/*
			 * Check legal move, nic
			 */
			if(bestMove.isEmpty()){
				numReset ++;
				if(numReset > maxResetForOneLocal){
					logger.info("Big reseting...");
					tracer.globalReset(0); // FIXME reset times
					for(int i = 0; i< variablesList.length; i++){
						VarIntLS var = variablesList[i];
						int value = random.nextInt(variablesValueDomain[i])
								+ var.getMinValue();
						var.setValuePropagate(value);
						localBest[i] = value;
					}
					localBestViolation = constraintSystem.violations();
					numReset = 0;
					continue; // search continue;
				}
				logger.info("No more Movable! Restarting " + numReset +  " ..");
				tracer.localReset(numReset);
				for(int i = 0; i< variablesList.length; i++){
					variablesList[i].setValuePropagate(localBest[i]);
					for(int j = 0; j< tabuSwap[i].length; j++){
						tabuSwap[i][j] = -tabuLength;
					}
				}
				nonStableCount =0;
				continue;
			}
			if(minDelta >= 0){
				nonStableCount ++;
				if(nonStableCount > maxStable){
					numReset ++;
					if(numReset > maxResetForOneLocal){
						logger.info("Big reseting...");
						tracer.globalReset(0);// FIXME reset times
						for(int i = 0; i< variablesList.length; i++){
							VarIntLS var = variablesList[i];
							int value = random.nextInt(variablesValueDomain[i])
									+ var.getMinValue();
							var.setValuePropagate(value);
							localBest[i] = value;
						}
						localBestViolation = constraintSystem.violations();
						numReset = 0;
						continue; // search continue;
					}
					logger.info("Restarting..");
					tracer.localReset(numReset);
					for(int i = 0; i< variablesList.length; i++){
						variablesList[i].setValuePropagate(localBest[i]);
						for(int j = 0; j< tabuSwap[i].length; j++){
							tabuSwap[i][j] = -tabuLength;
						}
					}
					nonStableCount = 0;
					
				}
			} else if(minDelta < 0){
				nonStableCount = 0;
			}
			
			/*
			 * Perform move
			 */
			int randomSituation = random.nextInt(bestMove.size);
			if(isSwap){
				int variableId = bestMove.values[randomSituation];
				int otherVariableId = bestMove.otherValues[randomSituation];
				System.out.println("Swap " + variableId + " " + otherVariableId);
				VarIntLS var = variablesList[variableId];
				VarIntLS otherVar = variablesList[otherVariableId];
				int otherVarValue = otherVar.getValue();
				// setting value and update tabuList
				otherVar.setValuePropagate(var.getValue());
				tabuSwap[variableId][otherVariableId] = iterator;
				var.setValuePropagate(otherVarValue);
				
				// update best
				if(constraintSystem.violations() < localBestViolation){// update local best
					numReset = 0; // set numReset = 0 when new local bester was founded
					localBestViolation = constraintSystem.violations();
					for(int i = 0; i< variablesList.length; i++){
						localBest[i] = variablesList[i].getValue();
					}
					if(localBestViolation < globalBestViolation){ // update global
						globalBestViolation = localBestViolation;
						for(int i = 0; i< variablesList.length; i++){
							globalBest[i] = localBest[i];
						}
					}
				}
			} else {
				int variableId = bestMove.values[randomSituation];
				int value = bestMove.otherValues[randomSituation];
				System.out.println("Assign " + variableId + " to " + value);
				VarIntLS variable = variablesList[variableId];
				variable.setValuePropagate(value);
				if(constraintSystem.violations() < localBestViolation){ // update local best
					tracer.foundLocal();
					localBestViolation = constraintSystem.violations();
					for(int i = 0; i< variablesList.length; i++){
						localBest[i] = variablesList[i].getValue();
					}
					numReset = 0; // update numReset after founded new bester local
					
					// update global mark
					if(localBestViolation < globalBestViolation){
						tracer.foundGlobal();
						globalBestViolation = localBestViolation;
						for(int i = 0; i< variablesList.length; i++){
							globalBest[i] = localBest[i];
						}
					}
				}
				// update tabuList
				tabuAssign[variableId][value] = iterator;
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
						+ "\tNic: " + nonStableCount);
			}
			
		}

		/*
		 * set variable to global best
		 */
		for(int i = 0 ; i < variablesList.length; i++){
			variablesList[i].setValuePropagate(globalBest[i]);
		}
		
		/*
		 * Trace finish
		 */
		tracer.finish();
		
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
		searchMix(tabuLength, maxTimeSeconds, maxIterator, maxStable, maxResetForOneLocal);
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
			values[size] = variableId;
			otherValues[size++] = value;
		}
		
		public boolean isEmpty(){
			return size <= 0;
		}
	}

}
