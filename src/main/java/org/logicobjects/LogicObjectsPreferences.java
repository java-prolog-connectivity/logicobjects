package org.logicobjects;

import java.util.Properties;

import org.jpc.JpcPreferences;
import org.minitoolbox.Preferences;

/**
 * Keep a hash of preferences
 * Provides functionality for setting preferences in the hash and for reading them
 * Alternatively, provides functionality for querying preferences from environmental variables
 */
public class LogicObjectsPreferences extends JpcPreferences {
	
	public static final String LOGIC_OBJECTS_NAME = "Logic Objects";

	public final static String IMPLICIT_RETURN_VARIABLE = "LSolution";

	public static Properties defaultJPCProperties() {
		Properties properties = JpcPreferences.defaultJpcProperties();
		properties.put(org.jpc.JpcPreferences.TEMP_SUBDIRECTORY_NAME_ENV, LOGIC_OBJECTS_NAME);
		return properties;
	}

	

}
