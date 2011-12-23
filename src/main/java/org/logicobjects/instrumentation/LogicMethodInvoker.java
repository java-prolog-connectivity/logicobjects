package org.logicobjects.instrumentation;

import java.lang.reflect.Method;

import jpl.Query;
import org.logicobjects.adapter.methodparameters.ParametersAdapter;
import org.logicobjects.adapter.methodparameters.TermParametersAdapter;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;

import org.logicobjects.adapter.LogicAdapter;
import org.logicobjects.adapter.LogtalkObjectAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.OneAnswerAdapter;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LParametersAdapter;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolutionAdapter;
import org.logicobjects.annotation.method.LWrapperAdapter;
import org.logicobjects.annotation.method.NO_ADAPTER;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogtalkObject;

public class LogicMethodInvoker {

	public static Object invoke(Object targetObject, Method method, Object[] params) {

		LogicEngine engine = LogicEngine.getDefault();
		Query query = null;
		//annotations
		LQuery rawQueryAnnotation;
		LMethod logicMethodAnnotation;
		LParametersAdapter parametersAdapterAnnotation;
		LSolutionAdapter methodResultAdapterAnnotation;
		LWrapperAdapter wrapperResultAdapterAnnotation;
		
		//adapter classes
		Class parametersAdapterClass = null;
		Class methodResultAdapterClass = null;
		Class wrapperResultAdapterClass = null;
		
		String logicMethodName = null;
		MethodResultAdapter resultAdapter = null;

		try {
			parametersAdapterAnnotation = (LParametersAdapter) method.getAnnotation(LParametersAdapter.class);
			if (parametersAdapterAnnotation != null && !parametersAdapterAnnotation.adapter().equals(NO_ADAPTER.class)) {
				parametersAdapterClass = parametersAdapterAnnotation.adapter();
				ParametersAdapter parametersAdapter = (ParametersAdapter)parametersAdapterClass.newInstance(); //the parameters should be transformed using an instance of the parametersAdapterClass
				parametersAdapter.setParameters(parametersAdapterAnnotation.args()); 
				params = parametersAdapter.adapt(params);
			}

			methodResultAdapterAnnotation = (LSolutionAdapter) method.getAnnotation(LSolutionAdapter.class);
			if (methodResultAdapterAnnotation != null && !methodResultAdapterAnnotation.adapter().equals(NO_ADAPTER.class)) {
					methodResultAdapterClass = methodResultAdapterAnnotation.adapter();
			}
			
			wrapperResultAdapterAnnotation = (LWrapperAdapter) method.getAnnotation(LWrapperAdapter.class);
			if (wrapperResultAdapterAnnotation != null && !wrapperResultAdapterAnnotation.adapter().equals(NO_ADAPTER.class)) {
				wrapperResultAdapterClass = wrapperResultAdapterAnnotation.adapter();
			}

			if( (methodResultAdapterAnnotation != null || wrapperResultAdapterAnnotation != null) ) {  //then an adapter is needed. We need to find out if it is a wrapped one, or non wrapped
				LogicAdapter adapterAtMethodResultAnnotation = null; //we do not know yet if this is a wrapped adapter or not 
				if(methodResultAdapterAnnotation != null) { //this annotation knows an adapter, if there is not a declared wrapper adapter, this will be the main one. Otherwise, it will be wrapped by the declared wrapper adapter.
					adapterAtMethodResultAnnotation = (LogicAdapter)methodResultAdapterClass.newInstance();
					adapterAtMethodResultAnnotation.setParameters(methodResultAdapterAnnotation.args());
				} else  //there is not provided an initial adapter, but there is a wrapper. So we initialize a default adapter to be wrapped.
					adapterAtMethodResultAnnotation = EachSolutionAdapter.DefaultEachSolutionAdapter.class.newInstance();
				
				if( wrapperResultAdapterAnnotation==null && !(adapterAtMethodResultAnnotation instanceof EachSolutionAdapter) ) { //then a wrapper instance is NOT needed
					resultAdapter = (MethodResultAdapter) adapterAtMethodResultAnnotation;
				}
				else { //we do need a wrapper
					if(wrapperResultAdapterAnnotation!=null) { //the wrapper is indicated in the wrapper annotation
						resultAdapter = (MethodResultAdapter)wrapperResultAdapterClass.newInstance();
						resultAdapter.setParameters(wrapperResultAdapterAnnotation.args());
					} else { //there is no wrapper adapter, so we need to create one by default
						resultAdapter = (MethodResultAdapter)OneAnswerAdapter.class.newInstance();   //this wrapper adapter returns the first result of the query
					}
					//set the each solution adapter at the composition adapter. This method will also set the composition adapter at the each solution adapter (so they both know about each other).
					((SolutionCompositionAdapter)resultAdapter).setEachSolutionAdapter((EachSolutionAdapter) adapterAtMethodResultAnnotation);    
				}
			}
			
			
			logicMethodAnnotation = (LMethod) method.getAnnotation(LMethod.class);  //this annotation contain properties that will guide the logic method invocation		
			if(logicMethodAnnotation != null) {
				if (!logicMethodAnnotation.name().equals(""))
					logicMethodName = logicMethodAnnotation.name();  //the name of the logtalk method to invoke
				else
					logicMethodName = method.getName();  //if there is not defined a name, then use the name of the java method
				
				LogtalkObject lo = null;
				lo = new LogtalkObjectAdapter().adapt(targetObject);	

				query = lo.invokeMethod(logicMethodName, params);
			} else {
				rawQueryAnnotation = (LQuery) method.getAnnotation(LQuery.class);
				String queryString = rawQueryAnnotation.value();
				queryString = TermParametersAdapter.replaceParameters(queryString, params);
				query = new Query (engine.textToTerm(queryString) );
			}

			if(resultAdapter == null) {
				return query;
			}
			else {
				resultAdapter.setMethod(method);
				Object result = resultAdapter.adapt(query);
				LogicEngine.getDefault().flushOutput(); //TODO maybe this should be customizable per method ?
				return result;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
