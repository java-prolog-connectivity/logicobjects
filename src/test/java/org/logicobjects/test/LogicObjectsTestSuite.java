package org.logicobjects.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.logicobjects.lib.examples.metro.MetroTestSuite;
import org.reflectiveutils.test.ReflectiveUtilsTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ReflectiveUtilsTestSuite.class, TestParsingExpressions.class, TestLogicObjectsUtil.class, 
	TestLogicExpressions.class, TestWrapperAdapter.class, TestRawQueries.class, TestDelegationObject.class, 
	MetroTestSuite.class, TestReflections.class})
public class LogicObjectsTestSuite {}
