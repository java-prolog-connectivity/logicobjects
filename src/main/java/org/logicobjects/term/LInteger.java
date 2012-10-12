package org.logicobjects.term;

import jpl.JPLException;

public abstract class LInteger extends LTerm {

	/**
	 * the Integer's immutable long value
	 */
	protected final long value;
	
	/**
	 * @param   value  This Integer's (long) value
	 */
	public LInteger(long value) {
		this.value = value;
	}
	
	/**
	 * Returns the value of this Integer as an int if possible, else throws a JPLException
	 * 
	 * @throws JPLException if the value of this Integer is too great to be represented as a Java int
	 * @return the int value of this Integer
	 */
	public final int intValue() {
		if (value < java.lang.Integer.MIN_VALUE || value > java.lang.Integer.MAX_VALUE) {
			throw new LException("cannot represent Integer value as an int");
		} else {
			return (int)value;
		}
	}

	/**
	 * Returns the value of this Integer as a long
	 * 
	 * @return the value of this Integer as a long
	 */
	public final long longValue() {
		return value;
	}
	
	/**
	 * Returns the value of this Integer converted to a float
	 * 
	 * @return the value of this Integer converted to a float
	 */
	public final float floatValue() {
		return value;
	}

	/**
	 * Returns the value of this Integer converted to a double
	 * 
	 * @return the value of this Integer converted to a double
	 */
	public final double doubleValue() {
		return value;
	}
	
}
