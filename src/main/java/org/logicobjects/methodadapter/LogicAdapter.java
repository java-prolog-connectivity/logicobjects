package org.logicobjects.methodadapter;

import java.util.Collections;
import java.util.List;

import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.provider.PrologEngineProvider;

/**
 * Parent of all logic adapters (adapters that interpret queries as objects) in the system
 * 
 * TODO it seems that this class should receive a PrologEngine in the constructor, instead of a provider
 * 
 */
public abstract class LogicAdapter<From, To> {
	
	private List parameters;
	private PrologEngineProvider<PrologEngine> prologEngineProvider;
	
	public LogicAdapter() {
		setParameters(Collections.emptyList());
	}

	public LogicAdapter(PrologEngineProvider<PrologEngine> prologEngineProvider) {
		setParameters(Collections.emptyList());
		setPrologEngineProvider(prologEngineProvider);
	}
	
	public void setParameters(List parameters) {
		this.parameters = parameters;
	}

	public PrologEngineProvider<PrologEngine> getPrologEngineProvider() {
		return prologEngineProvider;
	}

	public void setPrologEngineProvider(PrologEngineProvider<PrologEngine> prologEngineProvider) {
		this.prologEngineProvider = prologEngineProvider;
	}

	public PrologEngine getPrologEngine() {
		return prologEngineProvider.getPrologEngine();
	}

}
