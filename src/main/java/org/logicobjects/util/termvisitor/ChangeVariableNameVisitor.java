package org.logicobjects.util.termvisitor;

import java.util.Map;

import jpl.Compound;
import jpl.Term;
import jpl.Variable;

/**
 * This visitor replace variable names for another one
 * Given that there is not a mutator for the name attribute at the class Variable, this visitor returns a term with new Variable objects
 * (in the current implementation the original term is also changed, this will be fixed some day when having time ...)
 */
public class ChangeVariableNameVisitor extends TermVisitor {
	Map<String, String> map;
	
	public ChangeVariableNameVisitor(Map<String, String> map) {
		this.map = map;
	}
	
	@Override
	public Term visit(Term term) {
		if(term instanceof Variable) {
			return replacementVariable((Variable)term);
		} else
			return (Term) super.visit(term);
	}
	
	
	@Override
	protected boolean doVisit(Term term) {
		if(term instanceof Compound) {
			Compound compound = (Compound)term;
			Term[] args = compound.args();
			for(int i = 0; i<args.length; i++) {
				if(args[i] instanceof Variable) {
					args[i] = replacementVariable((Variable)args[i]);
				}
			}
			return true;
		} 
		return false;
	}
	
	private Variable replacementVariable(Variable var) {
		String newName = map.get(var.name());
		if(newName != null)
			return new Variable(newName);
		else
			return var;
	}

}
