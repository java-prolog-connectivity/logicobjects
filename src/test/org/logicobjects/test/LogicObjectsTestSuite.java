package org.logicobjects.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.logicobjects.lib.example.metro.MetroTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestParsingExpressions.class, TestLogicExpressions.class, TestRawQueries.class, TestDelegationObject.class, MetroTestSuite.class})
public class LogicObjectsTestSuite {}
