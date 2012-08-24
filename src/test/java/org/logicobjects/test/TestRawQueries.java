package org.logicobjects.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.fixture.MyRawQueries;

public class TestRawQueries extends LocalLogicTest {

	@Test
	public void testSimpleRawQueries() {
		MyRawQueries rawQueries = LogicObjectFactory.getDefault().create(MyRawQueries.class);
		assertEquals(rawQueries.returnsParameter(1), 1);
		/*
		assertEquals(rawQueries.intMethod1(), 1);
		assertEquals(rawQueries.intMethod2(), 1);
		assertTrue(rawQueries.trueMethod1());
		assertTrue(rawQueries.trueMethod2());
		assertFalse(rawQueries.falseMethod1());
		assertFalse(rawQueries.falseMethod2());
		assertTrue(rawQueries.shouldSucceed());
		assertFalse(rawQueries.shouldFail());
		assertNotNull(rawQueries.prologDialect());
		assertNotNull(rawQueries.currentPrologFlag("dialect"));
		//assertNotNull(rawQueries.currentPrologFlag("dialect", "yap"));
		//assertNotNull(rawQueries.scripting("current_prolog_flag(dialect, yap)"));
		*/
	}
}
