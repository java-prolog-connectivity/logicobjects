package org.logicobjects.util.termvisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.logicobjects.term.Compound;
import org.logicobjects.term.Term;
import org.logicobjects.term.Variable;

/**
 * Replace variables for a given term
 */
public class ReplaceVariableVisitor extends TermTransformationVisitor<Term> {
	Map<String, Term> map;
	
	public ReplaceVariableVisitor(Map<String, Term> map) {
		this.map = map;
	}

	@Override
	protected Term doTransform(Term source, List<Term> transformedChildren) {
		Term transformed;
		if(source.isVariable()) {
			transformed = map.get(((Variable)source).name());
			if(transformed == null)
				transformed = source;
		} else
			transformed = source;
		return transformed;
	}



	
	/*
	private Term replacementTerm(Variable var) {
		Term newTerm = map.get(var.name());
		if(newTerm != null)
			return newTerm;
		else
			return var;
	}

	@Override
	protected Term transform(Term term) {
		Term transformed;
		if(term.isCompound()) {
			Compound compound = (Compound) term;
			transformed = new Compound(compound.name(), transform(compound.args()));
		} else if(term.isVariable()) {
			transformed = replacementTerm((Variable)term);
		} else {
			transformed = term;
		}
		return transformed;
	}
	
	private List<Term> transform(List<Term> terms) {
		List<Term> transformedTerms = new ArrayList<>();
		for(Term term : terms) {
			transformedTerms.add(transform(term));
		}
		return transformedTerms;
	}
*/
}
