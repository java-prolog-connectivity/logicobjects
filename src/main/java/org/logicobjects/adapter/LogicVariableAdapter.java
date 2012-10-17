package org.logicobjects.adapter;

import org.jpc.term.Term;
import org.jpc.term.Variable;



public class LogicVariableAdapter extends ObjectToTermAdapter<String> {

	@Override
	public Term adapt(String object) {
		return new Variable(object.toString());
	}
}
