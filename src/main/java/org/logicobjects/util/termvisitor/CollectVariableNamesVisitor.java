package org.logicobjects.util.termvisitor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.logicobjects.term.Term;
import org.logicobjects.term.Variable;

public class CollectVariableNamesVisitor implements TermVisitor {
	
	Set<String> variableNames;
	
	public CollectVariableNamesVisitor() {
		variableNames = new LinkedHashSet<String>(); //LinkedHashSet to preserve insertion order
	}
	
	@Override
	public boolean visit(Term term) {
		if(term.isVariable()) {
			Variable var = (Variable)term;
			variableNames.add(var.name);
			return false; //a Variable does not have more children to visit
		}
		return true;
	}
	
	/**
	 * 
	 * @return an array with all the variable names ordered from left to right in the term
	 */
	public List<String> getVariableNames() {
		return new ArrayList<>(variableNames);
		
	}

}
