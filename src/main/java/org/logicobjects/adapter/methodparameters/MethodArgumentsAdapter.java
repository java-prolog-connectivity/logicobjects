package org.logicobjects.adapter.methodparameters;

import org.logicobjects.adapter.LogicAdapter;

/**
 * Adapt method arguments as terms, according to the arguments of the adapter
 */
public class MethodArgumentsAdapter extends LogicAdapter<Object[], Object[]> {

	@Override
	public Object[] adapt(Object[] source) {
		return source;
	}

}
