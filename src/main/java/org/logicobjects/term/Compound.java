package org.logicobjects.term;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.logicobjects.util.termvisitor.TermVisitor;

/**
 * A class reifying a logic compound term
 * DISCLAIMER: In the current version many methods in this class have been copied or adapted from the class jpl.Compound in the JPL library.
 * @author scastro
 *
 */
public class Compound extends Term {

	public static Compound newCompound(String functor, Term... parameters) {
		Compound term = parameters.length > 0 ? new Compound(functor, Arrays.asList(parameters)) : new Atom(functor);
		return term;
	}
	
	/**
	 * the name of this Compound
	 */
	protected final String name;
	/**
	 * the arguments of this Compound
	 */
	protected final List<Term> args;
	
	/**
	 * Creates a Compound with name but no args (i.e. an Atom).
	 * This condsructor is protected (from illegal public use) and is used
	 * only by Atom, which inherits it.
	 * 
	 * @param   name   the name of this Compound
	 * @param   args   the arguments of this Compound
	 */
	protected Compound(String name) {
		checkNotNull(name);
		checkArgument(!name.isEmpty(), "The name of a logic variable cannot be an empty string");
		this.name = name;
		this.args = new ArrayList<>();
	}

	/**
	 * Creates a Compound with name and args.
	 * 
	 * @param   name   the name of this Compound
	 * @param   args   the (one or more) arguments of this Compound
	 */
	public Compound(String name, List<Term> args) {
		checkNotNull(name);
		checkNotNull(args);
		checkArgument(!args.isEmpty(), "A compound term must have at least one argument");
		this.name = name;
		this.args = args;
	}
	
	/**
	 * Tests whether this Compound's functor has (String) 'name' and 'arity'.
	 * 
	 * @return whether this Compound's functor has (String) 'name' and 'arity'
	 */
	@Override
	public boolean hasFunctor(String name, int arity) {
		return this.name.equals(name) && args.size() == arity;
	}
	
	/**
	 * Returns the name (unquoted) of this Compound.
	 * 
	 * @return the name (unquoted) of this Compound
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Returns the arguments of this Compound (1..arity) of this Compound as an array[0..arity-1] of Term.
	 * 
	 * @return the arguments (1..arity) of this Compound as an array[0..arity-1] of Term
	 */
	@Override
	public List<Term> args() {
		return args;
	}
	
	/**
	 * Returns the arity (1+) of this Compound.
	 * 
	 * @return the arity (1+) of this Compound
	 */
	public int arity() {
		return args.size();
	}
	
	/**
	 * Returns a prefix functional representation of a Compound of the form name(arg1,...),
	 * and each argument is represented according to its toString() method.
	 * 
	 * @return  string representation of an Compound
	 */
	public String toString() {
		return prologName() + (args.size() > 0 ? "(" + Term.toString(args) + ")" : "");
	}
	
	/**
	 * Two Compounds are equal if they are identical (same object) or their names and arities are equal and their
	 * respective arguments are equal.
	 * 
	 * @param   obj  the Object to compare (not necessarily another Compound)
	 * @return  true if the Object satisfies the above condition
	 */
	public boolean equals(Object obj) {
		return (this == obj || (obj instanceof Compound && name.equals(((Compound) obj).name) && equals(args, ((Compound) obj).args)));
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
	
	public void accept(TermVisitor termVisitor) {
		if(termVisitor.visit(this))
			for(Term child: args) {
				termVisitor.visit(child);
			}
	}
}
