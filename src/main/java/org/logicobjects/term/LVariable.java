package org.logicobjects.term;

import static com.google.common.base.Preconditions.*;
import jpl.Variable;

import com.google.common.base.Preconditions;

public class LVariable extends LTerm {

	public final String name; // the name of this Variable
	
	public LVariable(String name) {
		checkNotNull(name);
		checkArgument(isValidVariableName(name), "The variable name " + name + " is not valid");
		this.name = name;
	}
	
	/**
	 * returns the lexical name of this Variable
	 * 
	 * @return the lexical name of this Variable
	 */
	public final String name() {
		return this.name;
	}
	
	/**
	 * Returns a Prolog source text representation of this Variable
	 * 
	 * @return  a Prolog source text representation of this Variable
	 */
	public String toString() {
		return this.name;
	}
	
	protected boolean isValidVariableName(String variableName) {
		return !variableName.isEmpty(); //additional checks could be added here
	}
	
	/**
	 * A Variable is equal to another if their names are the same and they are not anonymous.
	 * 
	 * @param   obj  The Object to compare.
	 * @return  true if the Object is a Variable and the above condition apply.
	 */
	public final boolean equals(Object obj) {
		return obj instanceof Variable && !this.name.equals("_") && this.name.equals(((Variable) obj).name);
	}
	
}
