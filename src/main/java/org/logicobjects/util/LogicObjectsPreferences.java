package org.logicobjects.util;

import java.util.Map;
import java.util.Properties;

import org.logicobjects.core.LogicEngine;
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
	public static final String[] SUPPORTED_ENGINES = new String[] {"yap", "swi"};  //supported prolog engines
	//Properties configuring the framework behaviour
	
	public final static String LOGTALKHOME = "LOGTALKHOME";  //needed by the framework to find the integration scripts
	public final static String LOGTALKUSER = "LOGTALKUSER"; //logtalk environment variable TODO: remembering what this variable was for ...
	//public final static String PROLOG_DIALECT = "PL";  //defines the prolog engine to use (DEPRECATED since this is decided by the JPLPATH environment variable
	public final static String SYSTEM_TEMP_DIRECTORY = "tmp";
	public final static String IMPLICIT_RETURN_VARIABLE = "LSolution";

	private Properties properties;
	
	public LogicObjectsPreferences() {
		properties = new Properties();
	}
	
	
	public String getTmpDirectory() {
		String tmp = getEnvironmentVar(LOGTALKUSER);
		if(tmp == null)
			tmp = getEnvironmentVar(SYSTEM_TEMP_DIRECTORY);
		return tmp;
	}
	
	public String logtalkIntegrationScript() {
		String logtalkHome = getEnvironmentVarOrDie(LOGTALKHOME);
		String scriptPath = logtalkHome + "/integration/";
		
		//String prologDialect = findOrDie(PROLOG_DIALECT);
		String prologDialect = LogicEngine.getDefault().prologDialect();
		if(prologDialect.equalsIgnoreCase("yap")) {
			scriptPath += "logtalk_yap.pl";
		} else if(prologDialect.equalsIgnoreCase("swi")) {
			scriptPath += "logtalk_swi.pl";
		} else {
			throw new RuntimeException("Unsopported Prolog dialect: "+prologDialect);
		}
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
		try {
			value = getPreference(key);
		} catch(Exception e) {
		} finally {
			if(value == null || value.equals("")) {
				logger.info("WARNING: Property " + key +" has not been set. Attempting to obtain its value from environment variable with same name: " + value);
				value = getEnvironmentVar(key);
			}
		}
		return value;
	}
	
	public static String getEnvironmentVarOrDie(String name) {
		String value = getEnvironmentVar(name);
		if(value==null || value.equals(""))
			throw new MissingEnvironmentVariableException(name);
		return value;
	}
	
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
