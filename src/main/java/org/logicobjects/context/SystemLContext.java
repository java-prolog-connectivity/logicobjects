package org.logicobjects.context;

import org.logicobjects.logicengine.LogicEngineConfiguration;


public class SystemLContext extends SimpleLContext {
	
	public SystemLContext() {
		addSearchUrlFromClass(this.getClass());
	}

	public LogicEngineConfiguration getLogicEngineConfiguration(Class clazz) {
		return null; //the system context do not define any engine configuration
	}
}
