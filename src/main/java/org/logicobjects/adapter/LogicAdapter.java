package org.logicobjects.adapter;

import java.util.Collections;
import java.util.List;

import org.jpc.engine.prolog.driver.AbstractPrologEngineDriver;
import org.jpc.util.PrologUtil;

/**
 * Parent of all logic adapters (adapters that convert to/from terms) in the system
 * 
 */
public abstract class LogicAdapter<From, To> extends Adapter<From, To> {
	private List parameters;
	private AbstractPrologEngineDriver logicEngineConfiguration;
	private PrologUtil logicUtil;
	
	public LogicAdapter() {
		setParameters(Collections.emptyList());
	}

	public LogicAdapter(AbstractPrologEngineDriver logicEngineConfiguration) {
		setParameters(Collections.emptyList());
		setLogicEngineConfiguration(logicEngineConfiguration);
	}
	
	public void setParameters(List parameters) {
		this.parameters = parameters;
	}

	public AbstractPrologEngineDriver getLogicEngineConfiguration() {
		return logicEngineConfiguration;
	}

	public void setLogicEngineConfiguration(AbstractPrologEngineDriver logicEngineConfiguration) {
		this.logicEngineConfiguration = logicEngineConfiguration;
		logicUtil = new PrologUtil(logicEngineConfiguration.getEngine());
	}

	public PrologUtil getLogicUtil() {
		return logicUtil;
	}

}
