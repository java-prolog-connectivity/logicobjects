package org.logicobjects.adapter;

import jpl.Term;
import jpl.Variable;

public class VariableAdapter extends ObjectToTermAdapter<String> {

	@Override
	public Term adapt(String object) {
		return new Variable(object.toString());
	}
}
