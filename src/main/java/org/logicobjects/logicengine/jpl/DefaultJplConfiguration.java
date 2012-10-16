package org.logicobjects.logicengine.jpl;

import jpl.JPL;

import org.logicobjects.LogicObjects;
import org.logicobjects.LogicObjectsPreferences;
import org.logicobjects.logicengine.LogicEngine;
import org.logicobjects.logicengine.LogicEngineConfiguration;

public class DefaultJplConfiguration extends LogicEngineConfiguration {

	public static final String JPLPATH = "JPLPATH"; //path of the JPL library in the host computer. This will determine if the prolog engine is SWI or YAP
	
	@Override
	public void configure() {
		//configuring the JPL path according to an environment variable. So a JPL Prolog engine can be started
		JPL.setNativeLibraryDir(LogicObjects.getPreferences().findOrDie(JPLPATH)); 
	}

	@Override
	protected LogicEngine createLogicEngine() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isConfigured() {
		/**
		 * According to the JPL documentation, the getActualInitArgs() method returns null if the JPL Prolog engine has not been initialized 
		 */
		return JPL.getActualInitArgs() != null;
	}
	

	
}
