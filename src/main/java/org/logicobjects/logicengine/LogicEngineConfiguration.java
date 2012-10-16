package org.logicobjects.logicengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.logicobjects.LogicObjectsPreferences;
import org.logicobjects.util.LogicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LogicEngineConfiguration {

	private static Logger logger = LoggerFactory.getLogger(LogicEngineConfiguration.class);
	
	protected LogicObjectsPreferences preferences;
	protected LogicEngine logicEngine;
	protected LogicUtil logicUtil;
	protected boolean enabled = true;
	private boolean configured = false;
	
	protected List<String> preloadedPrologResources;
	protected List<String> preloadedLogtalkResources;
	protected List<String> scope;
	protected boolean logtalkRequired;
	protected boolean logtalkLoaded;
	
	public LogicEngineConfiguration() {
		this(new LogicObjectsPreferences()); //default preferences
	}
	
	public LogicEngineConfiguration(LogicObjectsPreferences preferences) {
		this.preferences = preferences;
		preloadedPrologResources = new ArrayList<String>();
		preloadedLogtalkResources = new ArrayList<String>();
		scope = new ArrayList<String>();
		addScope(""); //the root package
	}

	public LogicObjectsPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(LogicObjectsPreferences preferences) {
		this.preferences = preferences;
	}

	public boolean isLogtalkLoaded() {
		return logtalkLoaded;
	}
	
	public boolean isLogtalkRequired() {
		return logtalkRequired;
	}

	public void setLogtalkRequired(boolean logtalkRequired) {
		this.logtalkRequired = logtalkRequired;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

	public List<String> getPreloadedPrologResources() {
		return preloadedPrologResources;
	}

	public void setPreloadedPrologResources(List<String> preloadedPrologResources) {
		this.preloadedPrologResources = preloadedPrologResources;
	}

	public void addPreloadedPrologResources(String ...newPreloadedPrologResources) {
		preloadedPrologResources.addAll(Arrays.asList(newPreloadedPrologResources));
	}
	
	public List<String> getPreloadedLogtalkResources() {
		return preloadedLogtalkResources;
	}

	public void setPreloadedLogtalkResources(List<String> preloadedLogtalkResources) {
		this.preloadedLogtalkResources = preloadedLogtalkResources;
	}

	public void addPreloadedLogtalkResources(String ...newPreloadedLogtalkResources) {
		preloadedLogtalkResources.addAll(Arrays.asList(newPreloadedLogtalkResources));
	}

	public List<String> getScope() {
		return scope;
	}

	public void setScope(List<String> scope) {
		this.scope = scope;
	}

	public void setScope(String ...newScopes) {
		scope = new ArrayList<>(Arrays.asList(newScopes));
	}
	
	public void addScope(String ...newScopes) {
		scope.addAll(Arrays.asList(newScopes));
	}
	
	public LogicEngine getEngine() {
		if(logicEngine == null) {
			logger.info("Initializing logic engine");
			long startTime = System.nanoTime();
			if(!isConfigured()) {
				configure();
			}
			logicEngine = createLogicEngine();
			logicUtil = new LogicUtil(logicEngine);
			if(isLogtalkRequired()) {
				logger.info("Attempting to load logtalk ...");
				try {
					String logtalkIntegrationScript = preferences.logtalkIntegrationScript(logicEngine.prologDialect()); //will throw an exception if a logtalk integration script cannot be found for a given engine
					logtalkLoaded = logicUtil.ensureLoaded(logtalkIntegrationScript);
					if(!logtalkLoaded)
						throw new RuntimeException();
					else
						logger.info("Logtalk loaded successfully");
				} catch(Exception ex) {
					logger.warn("Impossible to load Logtalk in the " + logicEngine.prologDialect() + " Logic Engine");
				}
			}
			loadPreloadedResources();
			long endTime = System.nanoTime();
			long total = (endTime - startTime)/1000000;
			logger.info("A " + logicEngine.prologDialect() + " logic engine has been initialized in " + total + " milliseconds");
		}
		return logicEngine;
	}

	private void loadPreloadedResources() {
		logicUtil.ensureLoaded(preloadedPrologResources.toArray(new String[]{}));
		logicUtil.logtalkLoad(preloadedLogtalkResources.toArray(new String[]{}));
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
