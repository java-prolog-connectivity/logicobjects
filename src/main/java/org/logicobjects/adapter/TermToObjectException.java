package org.logicobjects.adapter;

import java.lang.reflect.Type;

import org.logicobjects.term.Term;

public class TermToObjectException extends RuntimeException {

	private Term term;
	private Type type;
	
	public TermToObjectException(Term term, Type type) {
		this.term = term;
		this.type = type;
	}
	
	public TermToObjectException(Term term) {
		this.term = term;
	}
	
	@Override
	public String getMessage() {
		//System.out.println("*****"+LogicEngine.getDefault().termToText(term));		
		if(type!=null)
			return "LTerm "+term+" cannot be transformed into a logic object";
		else
			return "LTerm "+term+" cannot be transformed into an object of type "+type;
	}

}
