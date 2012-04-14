package org.logicobjects.adapter.methodparameters;

import org.logicobjects.adapter.LogicAdapter;

/**
 * Adapt method parameters as terms, according to the parameters of the adapter
 */
public class ParametersAdapter extends LogicAdapter<Object[], Object[]> {

	@Override
	public Object[] adapt(Object[] source) {
		return source;
	}

}
