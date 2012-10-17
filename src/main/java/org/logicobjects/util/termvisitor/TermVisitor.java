package org.logicobjects.util.termvisitor;

import org.logicobjects.term.Term;

public interface TermVisitor {

	public boolean visit(Term term);
	
}
