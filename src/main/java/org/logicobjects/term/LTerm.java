package org.logicobjects.term;


/**
 * DISCLAIMER: In the current version, most methods (and comments) in this class and subclasses have been copied or adapted from a corresponding class in the JPL library.
 * @author scastro
 *
 */
public abstract class LTerm {
	
	/**
	 * whether this Term represents an atom
	 * 
	 * @return whether this Term represents an atom
	 */
	public boolean isAtom() {
		return this instanceof LAtom;
	}

	/**
	 * whether this Term represents a compound term
	 * 
	 * @return whether this Term represents a compound atom
	 */
	public boolean isCompound() {
		return this instanceof LCompound;
	}

	/**
	 * whether this Term represents an atom
	 * 
	 * @return whether this Term represents an atom
	 */
	public boolean isFloat() {
		return this instanceof LFloat;
	}

	/**
	 * whether this Term represents an atom
	 * 
	 * @return whether this Term represents an atom
	 */
	public boolean isInteger() {
		return this instanceof LInteger;
	}

	/**
	 * whether this Term is a variable
	 * 
	 * @return whether this Term is a variable
	 */
	public boolean isVariable() {
		return this instanceof LVariable;
	}
	
	/**
	 * whether this Term is a list
	 * 
	 * @return whether this Term is a list
	 */
	public boolean isList() {
		try {
			listLength(); //will throw an exception if the list is not well formed
			return true;
		} catch(Exception e) {
			return false;
		}
		
	}
	
	/**
	 * the length of this list, iff it is one, else an exception is thrown
	 * 
	 * @throws LException
	 * @return the length (as an int) of this list, iff it is one
	 */
	public int listLength() {
		LCompound compound = (LCompound) this;
		if (compound.hasFunctor(".", 2)) {
			return 1 + compound.arg(2).listLength();
		} else if (compound.hasFunctor("[]", 0)) {
			return 0;
		} else {
			throw new LException("term" + compound.toString() + "is not a list");
		}
	}
	
	
	
	/**
	 * @param   t1  an array of Terms
	 * @param   t2  another array of Terms
	 * @return  true if all of the Terms in the (same-length) arrays are pairwise equal
	 */
	protected static boolean equals(LTerm[] t1, LTerm[] t2) {
		if (t1.length != t2.length) {
			return false;
		}

		for (int i = 0; i < t1.length; ++i) {
			if (!t1[i].equals(t2[i])) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Converts a list of Terms to a String.
	 * 
	 * @param   args    An array of Terms to convert
	 * @return  String representation of a list of Terms
	 */
	public static String toString(LTerm[] args) {
		String s = "";
		for (int i = 0; i < args.length; ++i) {
			s += args[i].toString();
			if (i != args.length - 1) {
				s += ", ";
			}
		}
		return s;
	}


}
