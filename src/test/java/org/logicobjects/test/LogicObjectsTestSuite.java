package org.logicobjects.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.logicobjects.lib.example.metro.MetroTestSuite;
import org.reflectiveutils.test.ReflectiveUtilsTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ReflectiveUtilsTestSuite.class, TestParsingExpressions.class, TestLogicExpressions.class, TestWrapperAdapter.class, TestRawQueries.class, TestDelegationObject.class, MetroTestSuite.class})
public class LogicObjectsTestSuite {}
