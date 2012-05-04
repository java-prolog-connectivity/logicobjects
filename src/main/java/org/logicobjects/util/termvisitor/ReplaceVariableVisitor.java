package org.logicobjects.util.termvisitor;

import java.util.Map;

import jpl.Compound;
import jpl.Term;
import jpl.Variable;

/**
 * Replace variables for a given term
 */
public class ReplaceVariableVisitor extends TermVisitor {
	Map<String, Term> map;
	
	@Override
	public Term visit(Term term) {
		if(term instanceof Variable) {
			return replacementTerm((Variable)term);
		} else
			return (Term) super.visit(term);
	}
	
	public ReplaceVariableVisitor(Map<String, Term> map) {
		this.map = map;
	}
	
	@Override
	protected boolean doVisit(Term term) {
		if(term instanceof Compound) {
			Compound compound = (Compound)term;
			Term[] args = compound.args();
			
			for(int i = 0; i<args.length; i++) {
				if(args[i] instanceof Variable) {
					args[i] = replacementTerm((Variable)args[i]);	
				}
			}
			return true;
		} else
			return false;
	}

	private Term replacementTerm(Variable var) {
		Term newTerm = map.get(var.name());
		if(newTerm != null)
			return newTerm;
		else
			return var;
	}
}
