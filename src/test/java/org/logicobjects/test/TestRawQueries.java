package org.logicobjects.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.fixture.MyRawQueries;
import static org.logicobjects.LogicObjects.*;

public class TestRawQueries extends LocalLogicTest {

	@Test
	public void testSimpleRawQueries() {
		MyRawQueries rawQueries = newLogicObject(MyRawQueries.class);
		assertEquals(rawQueries.returnsParameter(1), 1);
		assertEquals(rawQueries.intMethod1(), 1);
		assertEquals(rawQueries.intMethod2(), 1);
		assertTrue(rawQueries.trueMethod1());
		assertTrue(rawQueries.trueMethod2());
		assertFalse(rawQueries.falseMethod1());
		assertFalse(rawQueries.falseMethod2());
		assertTrue(rawQueries.shouldSucceed());
		assertFalse(rawQueries.shouldFail());
		assertNotNull(rawQueries.prologDialect());
		assertNotNull(rawQueries.customMethodNamePrologDialect("current_prolog_flag"));
		assertNotNull(rawQueries.currentPrologFlag("dialect"));
		assertTrue(rawQueries.scripting("true, true"));
	}
}
