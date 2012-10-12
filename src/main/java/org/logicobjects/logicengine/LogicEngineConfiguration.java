package org.logicobjects.logicengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LogicEngineConfiguration {

	protected boolean enabled = true;
	//protected boolean configured = false;
	
	protected List<String> preloadedResources;
	protected List<String> scope;
	
	public LogicEngineConfiguration() {
		preloadedResources = new ArrayList<String>();
		scope = new ArrayList<String>();
		addScope(""); //the root package
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isConfigured() {
		return true;
	}
/*
	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}
*/
/*
	public List<String> getPreloadedResources() {
		return preloadedResources;
	}

	public void setPreloadedResources(List<String> preloadedResources) {
		this.preloadedResources = preloadedResources;
	}

	public void addPreloadedResources(String ...newPreloadedResources) {
		preloadedResources.addAll(Arrays.asList(newPreloadedResources));
	}
*/
	public List<String> getScope() {
		return scope;
	}

	public void setScope(List<String> scope) {
		this.scope = scope;
	}

	public void addScope(String ...newScopes) {
		scope.addAll(Arrays.asList(newScopes));
	}
	
	public LogicEngine getEngine() {
		if(!isConfigured()) {
			configure();
		}
		return createLogicEngine();
	}

	
	public void configure() {
		//empty by default
	}
	
	/**
	 * The defaults configuration tasks of the engine
	 * @return
	 */
	protected abstract LogicEngine createLogicEngine();

}
