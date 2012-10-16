package org.logicobjects.adapter;

import java.util.Collections;
import java.util.List;

import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.logicobjects.util.LogicUtil;

/**
 * Parent of all logic adapters (adapters that convert to/from terms) in the system
 * 
 */
public abstract class LogicAdapter<From, To> extends Adapter<From, To> {
	private List parameters;
	private LogicEngineConfiguration logicEngineConfiguration;
	private LogicUtil logicUtil;
	
	public LogicAdapter() {
		setParameters(Collections.emptyList());
	}

	public LogicAdapter(LogicEngineConfiguration logicEngineConfiguration) {
		setParameters(Collections.emptyList());
		setLogicEngineConfiguration(logicEngineConfiguration);
	}
	
	public void setParameters(List parameters) {
		this.parameters = parameters;
	}

	public LogicEngineConfiguration getLogicEngineConfiguration() {
		return logicEngineConfiguration;
	}

	public void setLogicEngineConfiguration(LogicEngineConfiguration logicEngineConfiguration) {
		this.logicEngineConfiguration = logicEngineConfiguration;
		logicUtil = new LogicUtil(logicEngineConfiguration.getEngine());
	}

	public LogicUtil getLogicUtil() {
		return logicUtil;
	}

}
