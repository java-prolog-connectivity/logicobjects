package org.logicobjects.logicengine;

import org.logicobjects.term.LQuery;
import org.logicobjects.term.LTerm;

public abstract class LogicEngine {

	public abstract LTerm asTerm(String termString);
	
	public abstract LQuery createQuery(LTerm term);
	
	public LQuery createQuery(String termString) {
		return (createQuery(asTerm(termString)));
	}
	
}
