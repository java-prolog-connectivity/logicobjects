package org.jpc.termvisitor;

import org.jpc.term.Term;

public interface TermVisitor {

	public boolean visit(Term term);
	
}
