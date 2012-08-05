package org.logicobjects.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

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
		
		assertTrue(logicExpressions.testBoolean1(true));
		assertTrue(logicExpressions.testBoolean2(true));
		assertEquals(logicExpressions.testChar1('a'), 'a');
		assertEquals(logicExpressions.testChar2('a'), 'a');
		assertEquals(logicExpressions.testByte1((byte)1), (byte)1);
		assertEquals(logicExpressions.testByte2((byte)1), (byte)1);
		assertEquals(logicExpressions.testShort1((short)1), (short)1);
		assertEquals(logicExpressions.testShort2((short)1), (short)1);
		assertEquals(logicExpressions.testInt1(1), 1);
		assertEquals(logicExpressions.testInt2(1), 1);
		assertEquals(logicExpressions.testLong1(1), (long)1);
		assertEquals(logicExpressions.testLong2((long)1), (long)1);
		assertEquals(logicExpressions.testFloat1((float)1.0), (float)1.0);
		assertEquals(logicExpressions.testFloat2((float)1.0), (float)1.0);
		assertEquals(logicExpressions.testDouble1(1.0), 1.0);
		assertEquals(logicExpressions.testDouble2(1.0), 1.0);
		
		AtomicInteger atomicInt = new AtomicInteger(1);
		assertEquals(logicExpressions.testAtomicInteger1(1).intValue(), 1);
		assertEquals(logicExpressions.testAtomicInteger2("1").intValue(), 1);
		assertEquals(logicExpressions.testAtomicInteger3(atomicInt).intValue(), 1); //remember that AtomicInteger with two equivalent two values are not considered equals
		
		BigInteger bigInteger = BigInteger.valueOf(1);
		assertEquals(logicExpressions.testBigInteger1(1).longValue(), (long)1);
		assertEquals(logicExpressions.testBigInteger2("1").longValue(), (long)1);
		assertEquals(logicExpressions.testBigInteger3(bigInteger).longValue(), (long)1);
		
		BigDecimal bigDecimal = BigDecimal.valueOf(1.0);
		assertEquals(logicExpressions.testBigDecimal1(1.0).doubleValue(), 1.0);
		assertEquals(logicExpressions.testBigDecimal2("1.0").doubleValue(), 1.0);
		assertEquals(logicExpressions.testBigDecimal3(bigDecimal).doubleValue(), 1.0);
	}

}
