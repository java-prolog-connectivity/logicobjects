package org.logicobjects.instrumentation;

import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.core.AbstractLogicMethod;
import org.logicobjects.core.LogicEngine;

public class LogicMethodInvoker {

	public static Object invoke(Object targetObject, Method method, Object[] params) {
		AbstractLogicMethod logicMethod = AbstractLogicMethod.create((method));
		ParsedLogicMethod parsedLogicMethod = logicMethod.parse(targetObject, params);
		Query query = parsedLogicMethod.asQuery();
		MethodResultAdapter resultAdapter = logicMethod.getMethodAdapter(parsedLogicMethod);
		Object result = resultAdapter.adapt(query);
		LogicEngine.getDefault().flushOutput(); //TODO maybe this should be customizable per method ?
		return result;	
	}
}
