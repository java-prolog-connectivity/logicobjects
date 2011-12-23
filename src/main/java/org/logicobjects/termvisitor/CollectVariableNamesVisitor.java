package org.logicobjects.termvisitor;

import java.util.HashSet;
import java.util.Set;

import jpl.Term;
import jpl.Variable;

public class CollectVariableNamesVisitor extends TermVisitor {
	
	Set<String> variableNames;
	
	public CollectVariableNamesVisitor() {
		variableNames = new HashSet<String>();
	}
	
	@Override
	protected boolean doVisit(Term term) {
		if(term instanceof Variable) {
			Variable var = (Variable)term;
			variableNames.add(var.name);
			return false; //a Variable does not have more children to visit
		}
		return true;
	}
	
	public String[] getVariableNames() {
		return variableNames.toArray(new String[] {});
	}

}
