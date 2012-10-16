package org.logicobjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keep a hash of preferences
 * Provides functionality for setting preferences in the hash and for reading them
 * Alternatively, provides functionality for querying preferences from environmental variables
 */
public class LogicObjectsPreferences {
	private static Logger logger = LoggerFactory.getLogger(LogicObjectsPreferences.class);
	
	public static final String LOGIC_OBJECTS_NAME = "Logic Objects";

	//Properties configuring the framework behaviour
	public final static String LOGTALKHOME_ENV = "LOGTALKHOME";  //needed by the framework to find the integration scripts
	public final static String LOGTALKUSER_ENV = "LOGTALKUSER"; //logtalk user directory environment variable
	public final static String SYSTEM_TEMP_DIRECTORY_ENV = "tmp";
	public final static String IMPLICIT_RETURN_VARIABLE = "LSolution";

	private Properties properties;
	
	public LogicObjectsPreferences() {
		properties = new Properties();
	}
	
	
	public String getTmpDirectory() {
		String tmp = getEnvironmentVar(LOGTALKUSER_ENV);
		if(tmp == null)
			tmp = getEnvironmentVar(SYSTEM_TEMP_DIRECTORY_ENV);
		return tmp;
	}
	
	public String logtalkIntegrationScript(String engineName) {
		checkNotNull(engineName);
		checkArgument(!engineName.isEmpty());
		engineName = engineName.toLowerCase();
		String logtalkHome = findOrDie(LOGTALKHOME_ENV);
		String scriptPath = logtalkHome + "/integration/";
		String fileName = "logtalk_" + engineName + ".pl";
		scriptPath += fileName;
		File file = new File(scriptPath);
		if(!file.exists())
			throw new RuntimeException("The Logtalk installation at " + logtalkHome + " does not support the Prolog engine " + engineName);
		return scriptPath;
	}
	
	
	public String getPreference(String key) {
		return properties.getProperty(key);
	}
	
	public String setPreference(String key, String value) {
		return (String) properties.setProperty(key, value);
	}
	
	public String findOrDie(String key) {
		String preference = getPreferenceOrEnvironment(key);
		if(preference==null || preference.equals(""))
			throw new MissingEnvironmentVariableException(key);
		return preference;
	}
	/**
	 * Will answer an environment property from the in-memory hash.
	 * If it is not found it will try to find it from an environment variable
	 * @param key
	 * @return
	 */
	public String getPreferenceOrEnvironment(String key) {
		String value = null;
		value = getPreference(key);
		if(value == null || value.equals("")) {
			logger.info("Property " + key +" has not been set. Attempting to obtain its value from environment variable with same name: " + value);
			value = getEnvironmentVar(key);
		}
		return value;
	}
	/*
	public static String getEnvironmentVarOrDie(String name) {
		String value = getEnvironmentVar(name);
		if(value==null || value.equals(""))
			throw new MissingEnvironmentVariableException(name);
		return value;
	}
	*/
	public static String getEnvironmentVar(String name) {
		Map<String, String> env = System.getenv();
		return env.get(name);
	}
	
	
	
	
	public static class MissingEnvironmentVariableException extends RuntimeException{
		private String varName;
		public MissingEnvironmentVariableException(String varName) {
			this.varName = varName;
		}
		
		@Override
		public String getMessage() {
			return "The environment variable "+varName+" has not been set";
		}
	}
	
}
