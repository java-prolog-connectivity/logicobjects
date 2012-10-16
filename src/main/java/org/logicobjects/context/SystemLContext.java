package org.logicobjects.context;

import org.logicobjects.logicengine.LogicEngineConfiguration;


public class SystemLContext extends SimpleLContext {
	
	public SystemLContext() {
	}

	public LogicEngineConfiguration getLogicEngineConfiguration(String packageName) {
		return null; //the system context do not define any engine configuration
	}

	@Override
	protected void loadDefaultSearchUrl() {
		addSearchUrlFromClass(this.getClass());
	}
	
}
