package org.logicobjects.test.lib.examples.metrojpl;

import static junit.framework.Assert.assertTrue;
import jpl.JPL;

import org.junit.BeforeClass;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.lib.examples.metrojpl.MetroJpl;
import org.logicobjects.test.lib.examples.metro.TestMetro;
import org.logicobjects.util.LogicObjectsPreferences;

public class TestMetroJpl extends TestMetro {
	
	@BeforeClass
    public static void oneTimeSetUp() {
		LogicEngine.getDefault(); //will configure JPL and Logtalk
		assertTrue(MetroJpl.loadAll());
    }
	
	public TestMetroJpl() {
		setMetroObjectProvider(new MetroJplObjectProvider());
	}
}
