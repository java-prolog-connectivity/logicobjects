package org.jpc.termvisitor;

import java.util.Collections;
import java.util.List;

import org.jpc.term.Compound;
import org.jpc.term.Term;

public abstract class TermTransformationVisitor<To> extends AbstractTransformationVisitor<Term, To> implements TermVisitor {

	protected To transformed;
	
	public boolean visit(Term term) {
		transformed = transform(term);
		return false;
	}
	
	@Override
	protected List<Term> getChildren(Term source) {
		return source.args();
		/*
		if(source.getClass().equals(Compound.class)) {
			Compound compound = (Compound) source;
			return compound.args();
		} else
			return Collections.emptyList();
		*/
	}
	
	public To getTransformed() {
		return transformed;
	}
}
