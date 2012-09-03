package org.logicobjects.test.fixture;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.datatype.XMLGregorianCalendar;

import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LSolution;

public abstract class MyLogicExpressions {
	
	@LExpression
	@LSolution("text")
	public abstract String methodExpression1();
	
	@LExpression
	@LSolution("${\"text\"}")
	public abstract String methodExpression2();

	@LExpression
	@LSolution("true")
	public abstract boolean methodTrue1();
	
	@LExpression
	@LSolution("${true}")
	public abstract boolean methodTrue2();	
	
	@LExpression
	@LSolution("false")
	public abstract boolean methodFalse1();
	
	@LExpression
	@LSolution("${false}")
	public abstract boolean methodFalse2();	
	
	@LExpression
	@LSolution("$1")
	public abstract Boolean testBoolean1(boolean b);
	
	@LExpression
	@LSolution("$1")
	public abstract boolean testBoolean2(Boolean b);
	
	@LExpression
	@LSolution("$1")
	public abstract Character testChar1(char b);
	
	@LExpression
	@LSolution("$1")
	public abstract char testChar2(Character b);
	
	@LExpression
	@LSolution("$1")
	public abstract Byte testByte1(byte b);
	
	@LExpression
	@LSolution("$1")
	public abstract byte testByte2(Byte b);
	
	@LExpression
	@LSolution("$1")
	public abstract Short testShort1(short b);
	
	@LExpression
	@LSolution("$1")
	public abstract Short testShort2(Short b);
	
	@LExpression
	@LSolution("$1")
	public abstract Integer testInt1(int b);
	
	@LExpression
	@LSolution("$1")
	public abstract int testInt2(Integer b);
	
	@LExpression
	@LSolution("$1")
	public abstract Long testLong1(long b);
	
	@LExpression
	@LSolution("$1")
	public abstract long testLong2(Long b);
	
	@LExpression
	@LSolution("$1")
	public abstract Float testFloat1(float b);
	
	@LExpression
	@LSolution("$1")
	public abstract float testFloat2(Float b);
	
	@LExpression
	@LSolution("$1")
	public abstract Double testDouble1(double b);
	
	@LExpression
	@LSolution("$1")
	public abstract double testDouble2(Double b);
	
	@LExpression
	@LSolution("$1")
	public abstract AtomicInteger testAtomicInteger1(int b);
	
	@LExpression
	@LSolution("$1")
	public abstract AtomicInteger testAtomicInteger2(String b);
	
	@LExpression
	@LSolution("$1")
	public abstract AtomicInteger testAtomicInteger3(AtomicInteger b);
	
	@LExpression
	@LSolution("$1")
	public abstract AtomicLong testAtomicLong1(int b);
	
	@LExpression
	@LSolution("$1")
	public abstract AtomicLong testAtomicLong2(String b);
	
	@LExpression
	@LSolution("$1")
	public abstract AtomicLong testAtomicLong3(AtomicLong b);
	
	@LExpression
	@LSolution("$1")
	public abstract BigInteger testBigInteger1(int b);
	
	@LExpression
	@LSolution("$1")
	public abstract BigInteger testBigInteger2(String b);
	
	@LExpression
	@LSolution("$1")
	public abstract BigInteger testBigInteger3(BigInteger b);
	
	@LExpression
	@LSolution("$1")
	public abstract BigDecimal testBigDecimal1(double b);
	
	@LExpression
	@LSolution("$1")
	public abstract BigDecimal testBigDecimal2(String b);
	
	@LExpression
	@LSolution("$1")
	public abstract BigDecimal testBigDecimal3(BigDecimal b);
	
	@LExpression
	@LSolution("$1")
	public abstract Calendar testCalendar(Calendar cal);
	
	@LExpression
	@LSolution("$1")
	public abstract XMLGregorianCalendar testXMLGregorianCalendar(XMLGregorianCalendar cal);

}

