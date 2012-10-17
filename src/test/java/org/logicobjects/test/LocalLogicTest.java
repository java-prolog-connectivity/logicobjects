package org.logicobjects.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.logicobjects.LogicObjects;
import org.logicobjects.core.LContext;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.logicobjects.test.configuration.TestSuiteJPLConfiguration;
import org.logicobjects.util.LogicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for logic tests.
 * It ensures that the engine has been properly configured.
 * @author scastro
 *
 */
public class LocalLogicTest /*extends TestCase*/ {

	private static Logger logger = LoggerFactory.getLogger(LocalLogicTest.class);
	public static LogicEngineConfiguration logicEngineConfig;
	public static LogicUtil logicUtil;
	
	@BeforeClass
    public static void oneTimeSetUp() {
		logger.info("*** Setting LogicObjects search context to System");
		//LogicObjects.setContext(new LContext(false)); //the search context is limited to the LogicObjects library
		logicEngineConfig = new TestSuiteJPLConfiguration(); 
		logicEngineConfig.getEngine();//this will trigger the configuration of JPL and the loading of Logtalk
		logicUtil = new LogicUtil(logicEngineConfig);
    }
	
	@Before
	public void setUp() {
			
	}
	
}
