package org.logicobjects.adapter.methodresult;

import org.jpc.query.Query;

public class NumberOfSolutionsAdapter extends MethodResultAdapter<Integer> {

	
	@Override
	public Integer adapt(Query query) {
		return query.allSolutions().size();
	}

}
