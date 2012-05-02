package org.logicobjects.instrumentation;

import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.MethodInvokerContext;
import org.logicobjects.adapter.methodparameters.TermParametersAdapter;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.core.AbstractLogicMethod;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObject;

public class LogicMethodInvoker {

	public static Object invoke(Object targetObject, Method method, Object[] params) {
		AbstractLogicMethod logicMethod = AbstractLogicMethod.create((method));

		ObjectToTermAdapter[] parameterAdapters = logicMethod.getParameterAdapters();
		for(int i = 0; i <  parameterAdapters.length; i++) {
			ObjectToTermAdapter parameterAdapter = parameterAdapters[i];
			if(parameterAdapter != null) {
				params[i] = parameterAdapter.adapt(params[i]);
			}
		}
		
		Query query = logicMethod.asQuery(targetObject, params);
		MethodResultAdapter resultAdapter = logicMethod.getMethodAdapter(targetObject, params);
		Object result = resultAdapter.adapt(query);
		LogicEngine.getDefault().flushOutput(); //TODO maybe this should be customizable per method ?
		return result;	
	}
}
