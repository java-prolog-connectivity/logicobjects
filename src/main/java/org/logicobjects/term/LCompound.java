package org.logicobjects.term;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * DISCLAIMER: In the current version, most methods (and comments) in this class and subclasses have been copied or adapted from a corresponding class in the JPL library.
 * @author scastro
 *
 */
public class LCompound extends LTerm {

	/**
	 * the name of this Compound
	 */
	protected final String name;
	/**
	 * the arguments of this Compound
	 */
	protected final LTerm[] args;
	
	/**
	 * Creates a Compound with name but no args (i.e. an Atom).
	 * This condsructor is protected (from illegal public use) and is used
	 * only by Atom, which inherits it.
	 * 
	 * @param   name   the name of this Compound
	 * @param   args   the arguments of this Compound
	 */
	protected LCompound(String name) {
		checkNotNull(name);
		checkArgument(!name.isEmpty(), "The name of a logic variable cannot be an empty string");
		this.name = name;
		this.args = new LTerm[] {};
	}

	/**
	 * Creates a Compound with name and args.
	 * 
	 * @param   name   the name of this Compound
	 * @param   args   the (one or more) arguments of this Compound
	 */
	public LCompound(String name, LTerm[] args) {
		checkNotNull(name);
		checkNotNull(args);
		checkArgument(args.length > 0, "A compound term must have at least one argument");
		this.name = name;
		this.args = args;
	}
	
	/**
	 * Returns the ith argument (counting from 1) of this Compound;
	 * throws an ArrayIndexOutOfBoundsException if i is inappropriate.
	 * 
	 * @return the ith argument (counting from 1) of this Compound
	 */
	public final LTerm arg(int i) {
		return args[i - 1];
	}
	/**
	 * Tests whether this Compound's functor has (String) 'name' and 'arity'.
	 * 
	 * @return whether this Compound's functor has (String) 'name' and 'arity'
	 */
	public final boolean hasFunctor(String name, int arity) {
		return this.name.equals(name) && args.length == arity;
	}
	
	/**
	 * Returns the name (unquoted) of this Compound.
	 * 
	 * @return the name (unquoted) of this Compound
	 */
	public final String name() {
		return name;
	}
	/**
	 * Returns the arity (1+) of this Compound.
	 * 
	 * @return the arity (1+) of this Compound
	 */
	public final int arity() {
		return args.length;
	}
	
	/**
	 * Returns a prefix functional representation of a Compound of the form name(arg1,...),
	 * and each argument is represented according to its toString() method.
	 * 
	 * @return  string representation of an Compound
	 */
	public String toString() {
		return prologName() + (args.length > 0 ? "(" + LTerm.toString(args) + ")" : "");
	}
	
	/**
	 * Two Compounds are equal if they are identical (same object) or their names and arities are equal and their
	 * respective arguments are equal.
	 * 
	 * @param   obj  the Object to compare (not necessarily another Compound)
	 * @return  true if the Object satisfies the above condition
	 */
	public final boolean equals(Object obj) {
		return (this == obj || (obj instanceof LCompound && name.equals(((LCompound) obj).name) && equals(args, ((LCompound) obj).args)));
	}
	
	/**
	 * Sets the i-th (from 1) arg of this Compound to the given Term instance.
	 * This method, along with the Compound(name,arity) constructor, serves the new, native Prolog-term-to-Java-term routine,
	 * and is public only so as to be accessible via JNI: it is not intended for general use.
	 * 
	 * @param   i      the index (1+) of the arg to be set
	 * @param   arg    the Term which is to become the i-th (from 1) arg of this Compound
	 */
	public void setArg(int i, LTerm arg) {
		checkElementIndex(i, args.length);
		args[i - 1] = arg;
	}
	

	/**
	 *   This method should return the Prolog representation of an atom (the name of the compound)
	 * 	 for example, 'name' is quoted if necessary or escaping characters are added before special characters
	 *   by default this class just surrounds the name with single quotes (but this could be overridden if necessary).
	 *   This method affects only the string representation of the term
	 */
	protected String prologName() {
		return "'" + name + "'";
	}
}
