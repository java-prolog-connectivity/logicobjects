package org.logicobjects;

import org.jpc.util.JpcPreferences;

/**
 * Manages the LogicObjects preferences
 */
public class LogicObjectsPreferences extends JpcPreferences {

	public static final String LOGIC_OBJECTS_NAME = "Logic Objects";

	public final static String IMPLICIT_RETURN_VARIABLE = "LSolution";

	@Override
	protected String getTmpSubdirectoryName() {
		return LOGIC_OBJECTS_NAME;
	}
	
}
