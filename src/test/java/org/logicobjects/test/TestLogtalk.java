package org.logicobjects.test;

import static junit.framework.Assert.assertTrue;

import java.util.List;

import jpl.Atom;
import jpl.Term;

import org.junit.Test;
import org.logicobjects.core.LogicEngine;

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

	@Test
	public void testCurrentObjects() {
		List<Term> currentObjects = LogicEngine.getDefault().currentObjects();
		assertTrue(currentObjects.contains(new Atom("logtalk")));
	}
	
}
