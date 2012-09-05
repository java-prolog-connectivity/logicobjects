package org.logicobjects.test.lib.examples.metrojpl;

import static junit.framework.Assert.assertTrue;

import org.junit.BeforeClass;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.lib.examples.metrojpl.MetroJpl;
import org.logicobjects.test.lib.examples.metro.TestStation;

public class TestStationJpl extends TestStation {
	
	@BeforeClass
    public static void oneTimeSetUp() {
		LogicEngine.getDefault(); //will configure JPL and Logtalk
		assertTrue(MetroJpl.loadAll());
    }
	
	public TestStationJpl() {
		setMetroObjectProvider(new MetroJplObjectProvider());
	}
}
