package org.logicobjects.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.fixture.MyLogicExpressions;

public class TestLogicExpressions extends LocalLogicTest {

	@Test
	public void testSimpleLogicExpressions() {
		MyLogicExpressions logicExpressions = LogicObjectFactory.getDefault().create(MyLogicExpressions.class);
		assertEquals("text", logicExpressions.methodExpression1());
		assertEquals("text", logicExpressions.methodExpression2());
		assertTrue(logicExpressions.methodTrue1());
		assertTrue(logicExpressions.methodTrue2());
		assertFalse(logicExpressions.methodFalse1());
		assertFalse(logicExpressions.methodFalse2());
	}

}
