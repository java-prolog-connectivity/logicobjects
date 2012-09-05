package org.logicobjects.test.lib.examples.metrojpl;

import org.junit.BeforeClass;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.lib.examples.metrojpl.MetroJpl;
import org.logicobjects.test.lib.examples.metro.TestLine;
import static junit.framework.Assert.assertTrue;

public class TestLineJpl extends TestLine {
	
	@BeforeClass
    public static void oneTimeSetUp() {
		LogicEngine.getDefault(); //will configure JPL and Logtalk
		assertTrue(MetroJpl.loadAll());
    }
	
	public TestLineJpl() {
		setMetroObjectProvider(new MetroJplObjectProvider());
	}
	

}
