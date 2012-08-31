package org.logicobjects.adapter;

import jpl.Atom;
import jpl.Compound;
import jpl.Term;

import org.logicobjects.core.LogicEngine;

/*
 * Adapt a String path as a term representation
 * The path can be expressed in java style: dir1.dir2.dir3
 * or in Prolog style dir1(dir2(dir3))
 */

public class LogicResourcePathAdapter extends LogicAdapter<String, Term> {

	@Override
	public Term adapt(String classPathFileName) {
		//if(true)
			//return LogicEngine.getDefault().textToTerm(classPathFileName);
		
		//String[] atoms = classPathFileName.split("\\."); //escaping the dot
		String[] atoms = classPathFileName.split("/");
		
		if(atoms.length > 1) {
			//return makeCompound(atoms);
			return new Atom(classPathFileName);
		}
		else {
			//URL url = getClass().getClassLoader().getResource(classPathFileName);
			return LogicEngine.getDefault().textToTerm(atoms[0]);
		}
			
	}

	/**
	 * TODO delete ?
	 * Transforms an array of Strings in a Compound term.
	 * Answers an Atom if there is only one element in the array
	 * @param symbols
	 * @return
	 */
	public static Term makeCompound(String[] symbols) {
		return makeCompound(symbols, 0);
	}
	
	private static Term makeCompound(String[] symbols, int next) {
		if(symbols.length == 0) //in case the original array is empty
			return null;
		if(next == symbols.length-1) //no more symbols to process
			return new Atom(symbols[next]);
		else {
			return new Compound(symbols[next], new Term[] {makeCompound(symbols, next+1) });
		}
	}



}
