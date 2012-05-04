package org.logicobjects.test;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.logicobjects.context.SystemLContext;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for logic tests.
 * It ensures that the engine has been properly configured.
 * @author sergioc78
 *
 */
public class LocalLogicTest /*extends TestCase*/ {

	private static Logger logger = LoggerFactory.getLogger(LocalLogicTest.class);
	
	@BeforeClass
    public static void oneTimeSetUp() {
		logger.info("*** Setting LogicObjects search context to System");
		LogicObjectFactory.getDefault().setContext(new SystemLContext()); //the search context is limited to the LogicObjects library
		LogicEngine.getDefault(); //this will trigger the configuration of JPL and the loading of Logtalk
    }
	
	@Before
	public void setUp() {
			
	}
	
}
