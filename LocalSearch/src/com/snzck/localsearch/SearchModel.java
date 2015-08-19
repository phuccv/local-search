package com.snzck.localsearch;

import com.snzck.localsearch.search.SearchEventPool;

import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public interface SearchModel extends SearchTraceable{
	/**
	 * Get model manager
	 * @return A manager of model
	 */
	LocalSearchManager getManager();
	
	/**
	 * Get model constraint system
	 * @return
	 */
	ConstraintSystem getConstraintSystem();
	
	/**
	 * Get all variables by one array
	 * @return
	 */
	VarIntLS[] getVariables();
	
	/**
	 * Get all variable by structural
	 * @return
	 */
	VarIntLS[][] getStructuralVariables();
	
	/**
	 * Initialize variable
	 * @param method
	 */
	void initVariables(InitMethod method);
	
	/**
	 * Set event pool
	 * @param pool
	 */
	void setEventPool(SearchEventPool pool);
	
}
