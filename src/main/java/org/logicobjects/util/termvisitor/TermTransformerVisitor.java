package org.logicobjects.util.termvisitor;

import org.logicobjects.term.Term;

public abstract class TermTransformerVisitor extends TermVisitor {

	protected Term transformedTerm;
	
	@Override
	public boolean visit(Term term) {
		transformedTerm = transform(term);
		return false;
	}

	protected abstract Term transform(Term term);
	
	public Term getTransformedTerm() {
		return transformedTerm;
	}

}
