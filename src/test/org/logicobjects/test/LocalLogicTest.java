package org.logicobjects.test;

import junit.framework.TestCase;

import org.logicobjects.context.SystemLContext;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObjectFactory;

/**
 * Base class for logic tests.
 * It ensures that the engine has been properly configured.
 * @author sergioc78
 *
 */
public class LocalLogicTest extends TestCase {

	@Override
	public void setUp() {
		LogicObjectFactory.getDefault().setContext(new SystemLContext()); //the search context is limited to the LogicObjects library
		LogicEngine.getDefault(); //this will trigger the configuration of JPL and the loading of Logtalk
		
	}
}
