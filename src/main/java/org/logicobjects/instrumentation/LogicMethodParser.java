package org.logicobjects.instrumentation;

import java.lang.reflect.Method;

import org.logicobjects.core.LogicMethod;

public class LogicMethodParser extends AbstractLogicMethodParser<LogicMethod> {

	LogicMethodParser(Method method) {
		super(method);
	}

	@Override
	protected String[] getInputTokens() {
		String[] inputTokens = getLogicMethod().getParameters();
		if(inputTokens == null)
			inputTokens = new String[]{};
		return inputTokens;
	}

}

