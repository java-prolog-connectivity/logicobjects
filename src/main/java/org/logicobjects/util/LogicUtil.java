package org.logicobjects.util;


import java.util.Hashtable;
import java.util.Map;

import org.logicobjects.core.LogicEngine;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;


/**
 * Utility class for interacting with a Prolog engine
 * @author sergioc78
 *
 */
public class LogicUtil {

	
	protected static String surround(String atom, String functor) {
		return functor+"("+atom+")";
	}
	
	public static boolean usePrologNativeModule(String moduleName) {
		//e.g., lists, charsio
		 Query useModule = new Query(surround(surround(moduleName, "library"), "use_module"));
		 return useModule.hasSolution();
	}
	

	
	public static String[] solutionsForVars(Hashtable[] solutions, Variable var) {
		String[][] allVarSolutionsAux = solutionsForVars(solutions, new Variable[] {var});
		String[] allVarSolutions = new String[allVarSolutionsAux.length];
		
		for(int i=0; i<allVarSolutions.length; i++) {
			allVarSolutions[i] = allVarSolutionsAux[i][0];
		}
		return allVarSolutions;
	}
	
	public static String[][] solutionsForVars(Hashtable[] solutions, Variable[] vars) {
		int numberOfSolutions = solutions.length;
		int numberOfVars = vars.length;
		String[][] solutionsTable = new String[numberOfSolutions][numberOfVars];
		
		for(int i = 0; i<numberOfSolutions; i++) 
			for(int j = 0; j<numberOfVars; j++)
				solutionsTable[i][j] = solutionForVar(solutions[i], vars[j]);
		
		return solutionsTable;
	}
	
	protected static String solutionForVar(Hashtable solution, Variable var) {
		return solution.get(var.toString()).toString();
	}
	
	protected static String[] unquote(String quotedStrings[]) {
		String[] unquotedStrings = new String[quotedStrings.length];
		for(int i = 0; i<quotedStrings.length; i++) {
			unquotedStrings[i] = unquote(quotedStrings[i]);
		}
		return unquotedStrings;
	}
	
	protected static String unquote(String s) {
		return s.substring(1, s.length()-1);
	}
	

	
	
	
	
	public static Term termArrayToList(Term[] terms) {
		return Util.termArrayToList(terms);
	}
	
	public static Term textToTerm(String text) {
		return Util.textToTerm(text);
	}
	
	public static Term stringArrayToList(String[] a) {
		return Util.stringArrayToList(a);
	}
	
	public static Term intArrayToList(int[] a) {
		return Util.intArrayToList(a);
	}
	
	public static int listToLength(Term t) {
		return Util.listToLength(t);
	}
	
	public static Term[] listToTermArray(Term t) {
		return Util.listToTermArray(t);
	}
	
	public static String[] atomListToStringArray(Term t) {
		return Util.atomListToStringArray(t);
	}

}
