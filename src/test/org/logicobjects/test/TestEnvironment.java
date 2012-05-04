package org.logicobjects.test;

import org.junit.Test;
import org.logicobjects.util.LogicObjectsPreferences;

public class TestEnvironment extends LocalLogicTest {
	
	@Test
	public void testLOGTALKHOME() {
		verifyEnvironmentVar(LogicObjectsPreferences.LOGTALKHOME);
	}
	
	@Test
	public void testLOGTALKUSER() {
		verifyEnvironmentVar(LogicObjectsPreferences.LOGTALKUSER);
	}
	
	
	@Test
	public void testJPLPATH() {
		verifyEnvironmentVar(LogicObjectsPreferences.JPLPATH);
	}
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
