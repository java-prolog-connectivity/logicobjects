package org.logicobjects.converter.old;

import org.jpc.term.Term;

public class TextToTermAdapter extends ObjectToTermConverter<String> {
	@Override
	public Term adapt(String text) {
		return getPrologEngine().asTerm(text);
	}
}
