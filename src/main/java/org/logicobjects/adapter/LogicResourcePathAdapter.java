package org.logicobjects.adapter;

import java.net.URL;

import jpl.Atom;
import jpl.Compound;
import jpl.Term;

import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.resource.LogicResource;

/*
 * Adapt a String path as a term representation
 * The path can be expressed in java style: dir1.dir2.dir3
 * or in Prolog style dir1(dir2(dir3))
 */

public class LogicResourcePathAdapter extends LogicAdapter<LogicResource, Term> {

	private URL url;
	
	public LogicResourcePathAdapter(URL url) {
		this.url = url;
	}
	
	@Override
	public Term adapt(LogicResource resource) {
		//if(true)
			//return LogicEngine.getDefault().textToTerm(classPathFileName);
		
		//String[] atoms = classPathFileName.split("\\."); //escaping the dot
		
		String resourceName = resource.normalizedFileName();
		String[] tokens = resourceName.split("/");
		
		if(tokens.length > 1) {
			String resourcePath = LogicObjectFactory.getDefault().getResourceManager().getResourcePath(resourceName, url);
			//return makeCompound(atoms);
			return new Atom(resourcePath);
		}
		else {
			//URL url = getClass().getClassLoader().getResource(classPathFileName);
			Term term = LogicEngine.getDefault().textToTerm(tokens[0]);
			if(term.isAtom()) {
				String resourcePath = LogicObjectFactory.getDefault().getResourceManager().getResourcePath(term.name(), url);
				term = new Atom(resourcePath);
			}
			return term;
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
