package com.snzck.localsearch.binpacking2d.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import com.snzck.localsearch.FileFormatException;
import com.snzck.localsearch.InitMethod;
import com.snzck.localsearch.SearchMethod;
import com.snzck.localsearch.SearchModel;
import com.snzck.localsearch.binpacking2d.io.BpData;
import com.snzck.localsearch.functions.basic.Divide;
import com.snzck.localsearch.functions.basic.Module;
import com.snzck.localsearch.search.SearchEventPool;
import com.snzck.localsearch.search.SearchEventType;
import com.snzck.localsearch.search.SearchEvent;

public class BpModelCombine implements SearchModel {

	private LocalSearchManager manager;
	private ConstraintSystem cs;
	
	private SearchEventPool pool;
	
	private BpData data;
	private int itemCount;
	private int binWidth;
	private int binHeight;
	private int[] itemWidths;
	private int[] itemHeights;
	
	private VarIntLS[] combinedPos;
	private VarIntLS[] rotated;
	
	private boolean isReady;
	
	public BpModelCombine(BpData data) throws IOException, FileFormatException {
		this.data = data;
		itemCount = data.getItemCount();
		binWidth = data.getBinWidth();
		binHeight = data.getBinHeight();
		itemWidths = data.getItemWidths();
		itemHeights = data.getItemHeights();
		
		manager = new LocalSearchManager();
		cs = new ConstraintSystem(manager);
		
		allocateVariable();
		
		loadConstraints();
		
		manager.close();
	}

	private void allocateVariable(){
		final int COMBINE_MAX = (binWidth - 1) * binHeight;
		combinedPos = new VarIntLS[itemCount];
		rotated = new VarIntLS[itemCount];
		for(int i = 0; i< itemCount; i++){
			combinedPos[i] = new VarIntLS(manager, 0, COMBINE_MAX - 1);
			rotated[i] = new VarIntLS(manager, 0, 1);
		}
	}
	
	private void loadConstraints(){
		// Calculate x position and y position of all items
		IFunction[] left = new IFunction[itemCount];
		IFunction[] top = new IFunction[itemCount];
		for(int i = 0; i< itemCount; i++){
			left[i] = new Module(combinedPos[i], binWidth);
			top[i] = new Divide(combinedPos[i], binWidth);
		}
		
		// Calculate bound of all item
		IFunction[] right = new IFunction[itemCount];
		IFunction[] bottom = new IFunction[itemCount];
		IFunction[] rightRot = new IFunction[itemCount];
		IFunction[] bottomRot = new IFunction[itemCount];
		for(int i = 0; i< itemCount; i++){
			right[i] = new FuncPlus(left[i], itemWidths[i]);
			bottom[i] = new FuncPlus(top[i], itemHeights[i]);
			rightRot[i] = new FuncPlus(left[i], itemHeights[i]);
			bottomRot[i] = new FuncPlus(top[i], itemWidths[i]);
		}
		
		// All items must not overlap
		for(int i = 0; i< itemCount; i++){
			for(int j = i + 1; j< itemCount; j++){
				IConstraint[] allCase = new IConstraint[4];
				// Case 0 item 1 NOT rotated, item 2 NOT rotated
				IConstraint[] tmp = new IConstraint[4];
				tmp[0] = new LessOrEqual(right[i], left[j]);
				tmp[1] = new LessOrEqual(right[j], left[i]);
				tmp[2] = new LessOrEqual(bottom[i], top[j]);
				tmp[3] = new LessOrEqual(bottom[j], top[i]);
				IConstraint[] tmpCase = new IConstraint[3];
				tmpCase[0] = new OR(tmp);
				tmpCase[1] = new IsEqual(rotated[i], 0);
				tmpCase[2] = new IsEqual(rotated[j], 0);
				allCase[0] = new AND(tmpCase);
				// Case 1 item 1 NOT rotated, item 2 rotated
				tmp = new IConstraint[4];
				tmp[0] = new LessOrEqual(right[i], left[j]);
				tmp[1] = new LessOrEqual(rightRot[j], left[i]);
				tmp[2] = new LessOrEqual(bottom[i], top[j]);
				tmp[3] = new LessOrEqual(bottomRot[j], top[i]);
				tmpCase = new IConstraint[3];
				tmpCase[0] = new OR(tmp);
				tmpCase[1] = new IsEqual(rotated[i], 0);
				tmpCase[2] = new IsEqual(rotated[j], 1);
				allCase[1] = new AND(tmpCase);
				// Case 2 item 1 rotated, item 2 NOT rotated
				tmp = new IConstraint[4];
				tmp[0] = new LessOrEqual(rightRot[i], left[j]);
				tmp[1] = new LessOrEqual(right[j], left[i]);
				tmp[2] = new LessOrEqual(bottomRot[i], top[j]);
				tmp[3] = new LessOrEqual(bottom[j], top[i]);
				tmpCase = new IConstraint[3];
				tmpCase[0] = new OR(tmp);
				tmpCase[1] = new IsEqual(rotated[i], 1);
				tmpCase[2] = new IsEqual(rotated[j], 0);
				allCase[2] = new AND(tmpCase);
				// Case 3 item 1 rotated, item 2 rotated
				tmp = new IConstraint[4];
				tmp[0] = new LessOrEqual(rightRot[i], left[j]);
				tmp[1] = new LessOrEqual(rightRot[j], left[i]);
				tmp[2] = new LessOrEqual(bottomRot[i], top[j]);
				tmp[3] = new LessOrEqual(bottomRot[j], top[i]);
				tmpCase = new IConstraint[3];
				tmpCase[0] = new OR(tmp);
				tmpCase[1] = new IsEqual(rotated[i], 1);
				tmpCase[2] = new IsEqual(rotated[j], 1);
				allCase[3] = new AND(tmpCase);
				
				// Combine all case
				cs.post(new OR(allCase));
			}
		}
		
		// All items must be not overlap with bin bound
		for(int i = 0; i< itemCount; i++){
			IConstraint[] allCase = new IConstraint[2];
			// Case 0 : item NOT rotated
			IConstraint[] tmp = new IConstraint[3];
			tmp[0] = new LessOrEqual(right[i], binWidth);
			tmp[1] = new LessOrEqual(bottom[i], binHeight);
			tmp[2] = new IsEqual(rotated[i], 0);
			allCase[0] = new AND(tmp);
			// Case 1 : item rotated
			tmp = new IConstraint[3];
			tmp[0] = new LessOrEqual(rightRot[i], binWidth);
			tmp[1] = new LessOrEqual(bottomRot[i], binHeight);
			tmp[2] = new IsEqual(rotated[i], 1);
			allCase[1] = new AND(tmp);
			
			// Combine all case
			cs.post(new OR(allCase));
		}
		
	}
	
	private List<Integer[]> generateState(){
		ArrayList<Integer[]> state = new ArrayList<>();
		Integer[] xPosState = new Integer[itemCount];
		Integer[] yPosState = new Integer[itemCount];
		Integer[] rotatedState = new Integer[itemCount];
		for(int i = 0; i< itemCount; i++){
			xPosState[i] = combinedPos[i].getValue() % binWidth;
			yPosState[i] = combinedPos[i].getValue() / binWidth;
			rotatedState[i] = rotated[i].getValue();
		}
		state.add(xPosState);
		state.add(yPosState);
		state.add(rotatedState);
		return state;
	}
	
	@Override
	public void foundLocal() {
		if(pool != null){
			pool.add(new SearchEvent(SearchEventType.LOCAL, generateState(), cs.violations(), 0));
		}
	}

	@Override
	public void foundGlobal() {
		if(pool != null){
			pool.add(new SearchEvent(SearchEventType.GLOBAL, generateState(), cs.violations(), 0));
		}

	}

	@Override
	public void localReset(int resetTimes) {
		if(pool != null){
			pool.add(new SearchEvent(SearchEventType.RESET, generateState(), cs.violations(), 0));
		}

	}

	@Override
	public void globalReset(int resetTimes) {
		if(pool != null){
			pool.add(new SearchEvent(SearchEventType.GLOBALRESET, generateState(), cs.violations(), 0));
		}

	}
	
	@Override
	public void interrupt() {
		if(pool != null){
			pool.add(new SearchEvent(SearchEventType.INTERRUPT, generateState(), cs.violations(), 0));
		}
	}


	@Override
	public void finish() {
		if(pool != null){
			pool.add(new SearchEvent(SearchEventType.FINISH, generateState(), cs.violations(), 0));
		}
	}

	@Override
	public LocalSearchManager getManager() {
		return manager;
	}



	@Override
	public ConstraintSystem getConstraintSystem() {
		return cs;
	}



	@Override
	public VarIntLS[] getVariables() {
		VarIntLS[] vars = new VarIntLS[combinedPos.length + rotated.length];
		for(int i = 0; i< combinedPos.length; i++){
			vars[i] = combinedPos[i];
		}
		for(int i = 0; i< rotated.length; i++){
			vars[i + combinedPos.length] = rotated[i];
		}
		return combinedPos;
	}

	@Override
	public VarIntLS[][] getStructuralVariables() {
		VarIntLS[][] vars = new VarIntLS[2][];
		vars[0] = combinedPos;
		vars[1] = rotated;
		return vars;
	}

	@Override
	public void initVariables(InitMethod method) {
		if(method.getModel() != this){
			throw new IllegalArgumentException();
		}
		method.init();
		if(pool != null){
			pool.add(new SearchEvent(SearchEventType.INIT, generateState(), cs.violations(), 0));
		}
		isReady = true;
	}

	public BpData getData() {
		return data;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public SearchEventPool getPool() {
		return pool;
	}

	public void setPool(SearchEventPool pool) {
		this.pool = pool;
	}

	@Override
	public void setEventPool(SearchEventPool pool) {
		this.pool = pool;
	}

}
