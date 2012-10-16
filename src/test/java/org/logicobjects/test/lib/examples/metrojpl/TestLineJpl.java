package org.logicobjects.test.lib.examples.metrojpl;

import static junit.framework.Assert.assertTrue;

import org.junit.BeforeClass;
import org.logicobjects.lib.examples.metrojpl.MetroJpl;
import org.logicobjects.test.configuration.TestSuiteJPLConfiguration;
import org.logicobjects.test.lib.examples.metro.TestLine;

public class TestLineJpl extends TestLine {
	
	@BeforeClass
    public static void oneTimeSetUp() {
		new TestSuiteJPLConfiguration().getEngine(); //will configure JPL and Logtalk
		assertTrue(MetroJpl.loadAll());
    }
	
	public TestLineJpl() {
		setMetroObjectProvider(new MetroJplObjectProvider());
	}
	

}
