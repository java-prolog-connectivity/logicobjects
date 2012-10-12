package org.logicobjects.term;


public class LFloat extends LTerm {

	protected final double value;
	
	/**
	 * This constructor creates a Float with the supplied 
	 * (double) value.
	 * 
	 * @param   value  this Float's value
	 */
	public LFloat(double value) {
		this.value = value;
	}
	

	/**
	 * returns the (double) value of this Float, converted to an int
	 * 
	 * @return the (double) value of this Float, converted to an int
	 */
	public final int intValue() {
		return (int) value;
	}

	/**
	 * returns the (double) value of this Float, converted to a long
	 * 
	 * @return the (double) value of this Float, converted to a long
	 */
	public final long longValue() {
		return (long) value;
	}

	/**
	 * returns the (double) value of this Float, converted to a float
	 * 
	 * @return the (double) value of this Float, converted to a float
	 */
	public final float floatValue() {
		return (float) value;
	}

	/**
	 * returns the (double) value of this Float
	 * 
	 * @return the (double) value of this Float
	 */
	public final double doubleValue() {
		return value;
	}
	
	/**
	 * Returns a Prolog source text representation of this Float
	 * 
	 * @return  a Prolog source text representation of this Float
	 */
	public String toString() {
		return "" + value + "";
	}

	/**
	 * Two Floats are equal if they are the same object, or their values are equal
	 * 
	 * @param   obj  The Object to compare
	 * @return  true if the Object satisfies the above condition
	 */
	public final boolean equals(Object obj) {
		return this == obj || (obj instanceof LFloat && value == ((LFloat) obj).value);
	}
	
}
