package org.logicobjects.adapter;

import org.logicobjects.term.Term;
import org.logicobjects.term.Variable;



public class LogicVariableAdapter extends ObjectToTermAdapter<String> {

	@Override
	public Term adapt(String object) {
		return new Variable(object.toString());
	}
}
