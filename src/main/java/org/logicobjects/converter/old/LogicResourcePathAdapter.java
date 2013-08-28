package org.logicobjects.converter.old;

import java.net.URL;
import java.util.Arrays;

import org.jpc.engine.prolog.driver.AbstractPrologEngineDriver;
import org.jpc.resource.LogicResource;
import org.jpc.term.AbstractTerm;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.jpc.util.ResourceManager;
import org.logicobjects.methodadapter.LogicAdapter;

/*
 * Adapt a String path as a term representation
 * The path can be expressed in java style: dir1.dir2.dir3
 * or in Prolog style dir1(dir2(dir3))
 */

public class LogicResourcePathAdapter extends LogicAdapter<LogicResource, AbstractTerm> {

	private URL url;
	private ResourceManager resourceManager;
	
	public LogicResourcePathAdapter(AbstractPrologEngineDriver logicEngineConfig, URL url, ResourceManager resourceManager) {
		super(logicEngineConfig);
		this.url = url;
		this.resourceManager = resourceManager;
	}
	
	@Override
	public Term adapt(LogicResource resource) {
		//if(true)
			//return LogicEngine.getDefault().textToTerm(classPathFileName);
		
		//String[] atoms = classPathFileName.split("\\."); //escaping the dot
		
		String resourceName = resource.getNameWithoutLogicExtension();
		String[] tokens = resourceName.split("/");
		
		if(tokens.length > 1) {
			String resourcePath = resourceManager.getResourcePath(resourceName, url);
			//return makeCompound(atoms);
			return new Atom(resourcePath);
		}
		else {
			//URL url = getClass().getClassLoader().getResource(classPathFileName);
			Term term = getPrologEngine().asTerm(tokens[0]);
			if(term.isAtom()) {
				String resourcePath = resourceManager.getResourcePath(((Atom)term).getName(), url);
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
	
	private static AbstractTerm makeCompound(String[] symbols, int next) {
		if(symbols.length == 0) //in case the original array is empty
			return null;
		if(next == symbols.length-1) //no more symbols to process
			return new Atom(symbols[next]);
		else {
			return new Compound(symbols[next], Arrays.asList(makeCompound(symbols, next+1)));
		}
	}



}
