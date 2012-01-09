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
		String[] atoms = classPathFileName.split("\\."); //escaping the dot
		if(atoms.length > 1)
			return makeCompound(atoms);
		else
			return LogicEngine.getDefault().textToTerm(atoms[0]);
	}

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
