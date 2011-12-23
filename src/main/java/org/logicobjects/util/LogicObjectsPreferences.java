package org.logicobjects.util;

import java.util.Map;
import java.util.Properties;

import org.logicobjects.core.LogicEngine;

public class LogicObjectsPreferences {
	public static final String LOGIC_OBJECTS_NAME = "Logic Objects";
	public static final String[] SUPPORTED_ENGINES = new String[] {"yap", "swi"};
	public static final String JPLPATH = "JPLPATH";
	public final static String LOGTALKHOME = "LOGTALKHOME";
	public final static String LOGTALKUSER = "LOGTALKUSER";
	//public final static String PROLOG_DIALECT = "PL";

	private Properties properties;
	
	public LogicObjectsPreferences() {
		properties = new Properties();
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
	
	public String getPreferenceOrEnvironment(String key) {
		String value = null;
		try {
			value = getPreference(key);
		} catch(Exception e) {
		} finally {
			if(value == null || value.equals("")) {
				value = getEnvironmentVar(key);
				System.out.println("WARNING: Property " + key +" has not been set. Attempting to obtain its value from environment variable with same name: " + value);
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
