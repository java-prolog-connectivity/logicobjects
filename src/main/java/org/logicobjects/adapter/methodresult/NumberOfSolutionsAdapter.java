package org.logicobjects.adapter.methodresult;

import org.logicobjects.term.Query;

public class NumberOfSolutionsAdapter extends MethodResultAdapter<Integer> {

	
	@Override
	public Integer adapt(Query query) {
		return query.allSolutions().size();
	}

}
