package org.logicobjects.test;

import jpl.Atom;
import jpl.Compound;
import jpl.Term;
import jpl.Util;
import jpl.Variable;
import org.junit.Test;
import org.logicobjects.core.LogicEngine;
import static junit.framework.TestCase.*;

/**
 * This class test some Logtalk functionality (to be completed)
 * @author scastro
 *
 */
public class TestLogtalk extends LocalLogicTest {

	@Test
	public void testLoadingLogtalk() {
		LogicEngine.getDefault().loadLogtalk();
	}
	
	/**
	 * verify that the Logtalk operators have been defined in the logic engine
	 */
	@Test
	public void testLogtalkOperators() {
		assertTrue(LogicEngine.getDefault().isBinaryOperator("::"));
		assertTrue(LogicEngine.getDefault().isUnaryOperator("::"));
	}

	
}
