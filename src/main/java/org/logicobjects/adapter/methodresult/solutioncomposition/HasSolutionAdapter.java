package org.logicobjects.adapter.methodresult.solutioncomposition;

import jpl.Query;

/*
 * Adapt a logic query has a boolean indicating if the query has or not a solution
 */
public class HasSolutionAdapter extends SolutionCompositionAdapter<Boolean, Object> {

	@Override
	public Boolean adapt(Query source) {
		return source.hasSolution();
	}

}
