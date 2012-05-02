package org.logicobjects.instrumentation;

import java.lang.reflect.Method;

import org.logicobjects.core.RawLogicQuery;

public class RawQueryParser extends AbstractLogicMethodParser<RawLogicQuery> {

	RawQueryParser(Method method) {
		super(method);
	}

	@Override
	protected String[] getInputTokens() {
		return new String[] {getLogicMethod().getRawQuery()};
	}
	
	public String resolveQuery(Object targetObject, Object[] params) {
		return resolveInputTokens(targetObject, params)[0];
	}
		

}
