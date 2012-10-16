package org.logicobjects;

import java.util.HashMap;
import java.util.Map;

import org.logicobjects.logicengine.LogicEngine;
import org.logicobjects.logicengine.LogicEngineConfiguration;

//TODO delete ?
public class EnginePool {
	private Map<Class, LogicEngineConfiguration> enginePool;
	
	public EnginePool() {
		enginePool = new HashMap<>();
	}
	
	/**
	 * This method assumes that multiple instances of the same prolog engine can be active at the same time
	 * Then instead of caching instances of engines, this method caches engine configurations.
	 * An engine configuration knows, among other things, to which classes or packages a particular logic engine should be employed.
	 * 
	 * @param logicEngineConfigurationClass
	 * @return
	 */
	public <T extends LogicEngineConfiguration> LogicEngine getOrCreateLogicEngine(Class<T> logicEngineConfigurationClass) {
		LogicEngine logicEngine = null;
		LogicEngineConfiguration logicEngineConfiguration = enginePool.get(logicEngineConfigurationClass);
		if(logicEngineConfiguration != null) {
			logicEngine = logicEngineConfiguration.getEngine();
		} else {
			try {
				logicEngineConfiguration = logicEngineConfigurationClass.newInstance();
				enginePool.put(logicEngineConfigurationClass, logicEngineConfiguration);
				logicEngine = logicEngineConfiguration.getEngine(); 
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return logicEngine;
	}
}