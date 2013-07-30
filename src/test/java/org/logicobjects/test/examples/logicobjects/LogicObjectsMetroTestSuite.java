package org.logicobjects.test.examples.logicobjects;

import org.jpc.term.examples.metro.MetroTestSuite;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestMetroFactory.class, MetroTestSuite.class})
public class LogicObjectsMetroTestSuite {

	@Before
	public void setUp() {
		
	}
}
