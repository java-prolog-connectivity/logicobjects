package org.logicobjects.adapter.methodparameters;

import java.util.List;

import org.logicobjects.adapter.LogicAdapter;

/**
 * Adapt method arguments as terms, according to the arguments of the adapter
 */
public class MethodArgumentsAdapter extends LogicAdapter<List, List> {

	@Override
	public List adapt(List source) {
		return source;
	}

}
