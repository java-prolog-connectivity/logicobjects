package org.logicobjects.util.termvisitor;

import jpl.Term;

public abstract class TermVisitor {
	
	
	/**
	 * given that Term is part of an external library, 
	 * an accept method cannot be implemented there.
	 * Then, this visitor could visit the children of Term without it explicitly accepting it
	 * @param term
	 * @return
	 */
	public Object visit(Term term) {
		if(doVisit(term)) {
			Term[] children = term.args();
			for(Term child : children) {
				visit(child);
			}
		}
		return term;
	}

	protected abstract boolean doVisit(Term term);
	
	
}
