package org.logicobjects.converter.old;

import org.jpc.term.Term;
import org.jpc.term.Var;



public class LogicVariableAdapter extends ObjectToTermConverter<String> {

	@Override
	public Term adapt(String object) {
		return new Var(object.toString());
	}
}
