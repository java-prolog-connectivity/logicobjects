package org.logicobjects.adapter;

import jpl.Term;

import org.logicobjects.core.LogicEngine;

public class TextToTermAdapter extends ObjectToTermAdapter<String> {
	@Override
	public Term adapt(String text) {
		return LogicEngine.getDefault().textToTerm(text);
	}
}
