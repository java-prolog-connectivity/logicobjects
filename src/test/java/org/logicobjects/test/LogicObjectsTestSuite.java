package org.logicobjects.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.logicobjects.test.lib.examples.metro.MetroTestSuite;
import org.reflectiveutils.test.ReflectiveUtilsTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ReflectiveUtilsTestSuite.class, 
	TestParsingExpressions.class, 
	TestLogicObjectsUtil.class, 
	TestMutatorsAndAccessorsGeneration.class, 
	TestMutatorsAndAccessorsGeneration2.class,
	TestLogicExpressions.class, 
	TestWrapperAdapter.class, 
	TestRawQueries.class, 
	TestDelegationObject.class, 
	MetroTestSuite.class, 
	TestReflections.class})
public class LogicObjectsTestSuite {}
