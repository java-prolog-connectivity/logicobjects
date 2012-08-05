package org.logicobjects.test.fixture;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.logicobjects.annotation.method.LSolution;

public abstract class MyLogicExpressions {

	@LSolution("text")
	public abstract String methodExpression1();
	
	@LSolution("${\"text\"}")
	public abstract String methodExpression2();

	@LSolution("true")
	public abstract boolean methodTrue1();
	
	@LSolution("${true}")
	public abstract boolean methodTrue2();	
	
	@LSolution("false")
	public abstract boolean methodFalse1();
	
	@LSolution("${false}")
	public abstract boolean methodFalse2();	
	
	@LSolution("$1")
	public abstract Boolean testBoolean1(boolean b);
	
	@LSolution("$1")
	public abstract boolean testBoolean2(Boolean b);
	
	@LSolution("$1")
	public abstract Character testChar1(char b);
	
	@LSolution("$1")
	public abstract char testChar2(Character b);
	
	@LSolution("$1")
	public abstract Byte testByte1(byte b);
	
	@LSolution("$1")
	public abstract byte testByte2(Byte b);
	
	@LSolution("$1")
	public abstract Short testShort1(short b);
	
	@LSolution("$1")
	public abstract Short testShort2(Short b);
	
	@LSolution("$1")
	public abstract Integer testInt1(int b);
	
	@LSolution("$1")
	public abstract int testInt2(Integer b);
	
	@LSolution("$1")
	public abstract Long testLong1(long b);
	
	@LSolution("$1")
	public abstract long testLong2(Long b);
	
	@LSolution("$1")
	public abstract Float testFloat1(float b);
	
	@LSolution("$1")
	public abstract float testFloat2(Float b);
	
	@LSolution("$1")
	public abstract Double testDouble1(double b);
	
	@LSolution("$1")
	public abstract double testDouble2(Double b);
	
	@LSolution("$1")
	public abstract AtomicInteger testAtomicInteger1(int b);
	
	@LSolution("$1")
	public abstract AtomicInteger testAtomicInteger2(String b);
	
	@LSolution("$1")
	public abstract AtomicInteger testAtomicInteger3(AtomicInteger b);
	
	@LSolution("$1")
	public abstract AtomicLong testAtomicLong1(int b);
	
	@LSolution("$1")
	public abstract AtomicLong testAtomicLong2(String b);
	
	@LSolution("$1")
	public abstract AtomicLong testAtomicLong3(AtomicLong b);
	
	@LSolution("$1")
	public abstract BigInteger testBigInteger1(int b);
	
	@LSolution("$1")
	public abstract BigInteger testBigInteger2(String b);
	
	@LSolution("$1")
	public abstract BigInteger testBigInteger3(BigInteger b);
	
	@LSolution("$1")
	public abstract BigDecimal testBigDecimal1(double b);
	
	@LSolution("$1")
	public abstract BigDecimal testBigDecimal2(String b);
	
	@LSolution("$1")
	public abstract BigDecimal testBigDecimal3(BigDecimal b);
	
}

