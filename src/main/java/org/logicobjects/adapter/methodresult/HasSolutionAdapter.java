package org.logicobjects.adapter.methodresult;

import org.jpc.term.Query;


/*
 * Adapt a logic query has a boolean indicating if the query has or not a solution
 */
public class HasSolutionAdapter extends MethodResultAdapter<Boolean> {

	
	@Override
	public Boolean adapt(Query source) {
		return source.hasSolution();
	}

}
