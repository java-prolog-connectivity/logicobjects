package org.logicobjects.term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.logicobjects.logicengine.LogicEngine;
import org.logicobjects.util.termvisitor.ChangeVariableNameVisitor;
import org.logicobjects.util.termvisitor.CollectVariableNamesVisitor;
import org.logicobjects.util.termvisitor.ReplaceVariableVisitor;
import org.logicobjects.util.termvisitor.TermVisitor;


/**
 * A class reifying a logic term
 * DISCLAIMER: In the current version many methods in this class have been copied or adapted from the class jpl.Term in the JPL library.
 * @author scastro
 *
 */
public abstract class Term {

	/**
	 * Returns the ith argument (counting from 1) of this Compound;
	 * throws an ArrayIndexOutOfBoundsException if i is inappropriate.
	 * 
	 * @return the ith argument (counting from 1) of this Compound
	 */
	public Term arg(int i) {
		return args().get(i-1);
	}
	
	public abstract List<Term> args();
	
	public abstract boolean hasFunctor(String name, int arity);
	
	/**
	 * whether this Term represents an atom
	 * 
	 * @return whether this Term represents an atom
	 */
	public boolean isAtom() {
		return this instanceof Atom;
	}

	/**
	 * whether this Term represents a compound term
	 * 
	 * @return whether this Term represents a compound atom
	 */
	public boolean isCompound() {
		return this instanceof Compound;
	}

	public boolean isNumber() {
		return isInteger() || isFloat();
	}
	
	/**
	 * whether this Term represents an atom
	 * 
	 * @return whether this Term represents an atom
	 */
	public boolean isFloat() {
		return this instanceof FloatTerm;
	}

	/**
	 * whether this Term represents an atom
	 * 
	 * @return whether this Term represents an atom
	 */
	public boolean isInteger() {
		return this instanceof IntegerTerm;
	}

	/**
	 * whether this Term is a variable
	 * 
	 * @return whether this Term is a variable
	 */
	public boolean isVariable() {
		return this instanceof Variable;
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
	
	public boolean isUnification(Term term) {
		boolean isUnification = false;
		if (term.isCompound()) {
			Compound compound = (Compound) term;
			if(compound.name.equals("="))
				isUnification = true;
		}
		return isUnification;
	}
	
	public boolean bound() {
		return getVariablesNames().isEmpty();
	}
	
	public void accept(TermVisitor termVisitor) {
		termVisitor.visit(this);
	}
	
	/**
	 * the length of this list, iff it is one, else an exception is thrown
	 * 
	 * @throws LException
	 * @return the length (as an int) of this list, iff it is one
	 */
	public int listLength() {
		Compound compound = (Compound) this;
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
	protected static boolean equals(Term[] t1, Term[] t2) {
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
	
	protected static boolean equals(List<Term> t1, List<Term> t2) {
		return equals(t1.toArray(new Term[]{}), t2.toArray(new Term[]{}));
	}
	
	/**
	 * Converts a list of Terms to a String.
	 * 
	 * @param   args    An array of Terms to convert
	 * @return  String representation of a list of Terms
	 */
	public static String toString(Term... args) {
		String s = "";
		for (int i = 0; i < args.length; ++i) {
			s += args[i].toString();
			if (i != args.length - 1) {
				s += ", ";
			}
		}
		return s;
	}
	
	public static String toString(List<Term> terms) {
		return toString(terms.toArray(new Term[]{}));
	}


	public Term replaceVariables(Map<String, Term> map) {
		ReplaceVariableVisitor visitor = new ReplaceVariableVisitor(map);
		accept(visitor);
		return visitor.getTransformed();
	}
	
	public Term changeVariablesNames(Map<String, String> map) {
		ChangeVariableNameVisitor visitor = new ChangeVariableNameVisitor(map);
		accept(visitor);
		return visitor.getTransformed();
	}
	
	public List<String> getVariablesNames() {
		CollectVariableNamesVisitor visitor = new CollectVariableNamesVisitor();
		accept(visitor);
		return visitor.getVariableNames();
	}
	
	public boolean hasVariable(String variableName) {
		return getVariablesNames().contains(variableName);
	}
	
	public List<String> nonAnonymousVariablesNames() {
		List<String> nonAnonymousVariablesNames = new ArrayList<>();
		for(String variableName : getVariablesNames()) {
			if(!LogicEngine.isAnonymousVariableName(variableName))
				nonAnonymousVariablesNames.add(variableName);
		}
		return nonAnonymousVariablesNames;
	}
	

}
