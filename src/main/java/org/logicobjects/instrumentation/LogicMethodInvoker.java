package org.logicobjects.instrumentation;

import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.MethodInvokerContext;
import org.logicobjects.adapter.methodparameters.TermParametersAdapter;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicMethod;
import org.logicobjects.core.LogicObject;

public class LogicMethodInvoker {

	public static Object invoke(Object targetObject, Method method, Object[] params) {
		LogicMethod logicMethod = new LogicMethod(method);

		LogicEngine engine = LogicEngine.getDefault();
		Query query = null;

		ObjectToTermAdapter[] parameterAdapters = logicMethod.getParameterAdapters();
		for(int i = 0; i <  parameterAdapters.length; i++) {
			ObjectToTermAdapter parameterAdapter = parameterAdapters[i];
			if(parameterAdapter != null) {
				params[i] = parameterAdapter.adapt(params[i]);
			}
		}
		if(!logicMethod.isRawQuery()) {
			String logicMethodName = logicMethod.getLogicName();
			LogicObject lo = new LogicObjectAdapter().adapt(targetObject, new MethodInvokerContext(targetObject.getClass()));
			if(logicMethod.getParameters().length > 0) {
				TermParametersAdapter paramsAdapter = new TermParametersAdapter(targetObject, method);
				paramsAdapter.setParameters(logicMethod.getParameters());
				params = paramsAdapter.adapt(params);
			}
			query = lo.invokeMethod(logicMethodName, params);
		} else {
			String queryString = logicMethod.getRawQuery();
			queryString = new TermParametersAdapter(targetObject, method).replaceSymbolsAndExpressions(queryString, params);
			query = new Query (engine.textToTerm(queryString) );
		}
		
		MethodResultAdapter resultAdapter = logicMethod.getMethodAdapter();
		Object result = resultAdapter.adapt(query);
		LogicEngine.getDefault().flushOutput(); //TODO maybe this should be customizable per method ?
		return result;	
	}
}
