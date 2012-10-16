package org.logicobjects.test;

import static junit.framework.TestCase.*;
import org.junit.Test;
import org.logicobjects.LogicObjectsPreferences;

public class TestEnvironment extends LocalLogicTest {
	
	@Test
	public void testLOGTALKHOME() {
		verifyEnvironmentVar(LogicObjectsPreferences.LOGTALKHOME_ENV);
	}
	
	@Test
	public void testLOGTALKUSER() {
		verifyEnvironmentVar(LogicObjectsPreferences.LOGTALKUSER_ENV);
	}
	
	/*
	@Test
	public void testJPLPATH() {
		verifyEnvironmentVar(LogicObjectsPreferences.JPLPATH);
	}
	*/
	
	/*
	@Test
	public void testPL() {
		verifyEnvironmentVar(LogicObjectsPreferences.PROLOG_DIALECT);
	}
	*/
	private void verifyEnvironmentVar(String var) {
		LogicObjectsPreferences preferences = new LogicObjectsPreferences();
		String value = preferences.getPreferenceOrEnvironment(var);
		assertNotNull(value);
		assertNotSame(value, "");
		System.out.println(var+": "+value);
	}
	
}
