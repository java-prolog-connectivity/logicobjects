package org.logicobjects.adapter;

import org.jpc.term.Term;

public class TextToTermAdapter extends ObjectToTermAdapter<String> {
	@Override
	public Term adapt(String text) {
		return getLogicUtil().asTerm(text);
	}
}
