package org.reflectiveutils.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestTypeWrapper.class, TestGenericsUtils.class, TestPackagePropertiesTree.class})
public class ReflectiveUtilsTestSuite {}
