package org.logicobjects.util.termvisitor;

import org.logicobjects.term.Term;

public abstract class TermVisitor {

	public abstract boolean visit(Term term);
	
}
