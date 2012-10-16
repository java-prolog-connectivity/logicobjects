package org.logicobjects.adapter;

import org.logicobjects.term.Term;

public class TextToTermAdapter extends ObjectToTermAdapter<String> {
	@Override
	public Term adapt(String text) {
		return getLogicUtil().asTerm(text);
	}
}
