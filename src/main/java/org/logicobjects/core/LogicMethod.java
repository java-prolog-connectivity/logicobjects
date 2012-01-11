package org.logicobjects.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.methodresult.HasSolutionAdapter;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.OneSolutionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LParameter;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LWrapper;
import org.logicobjects.annotation.method.NO_ADAPTER;
import org.logicobjects.util.LogicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicMethod {
	private Method method;
	private Logger logger = LoggerFactory.getLogger(LogicMethod.class);
	
	public LogicMethod(Method method) {
		this.method = method;
	}
	
	public Method getWrappedMethod() {
		return method;
	}
	
	public String getLogicName() {
		LMethod lMethodAnnotation = (LMethod) method.getAnnotation(LMethod.class);
		String name = lMethodAnnotation.name();
		if(!name.isEmpty())
			return name;
		else
			return LogicUtil.javaNameToProlog(method.getName());
	}
	
	public String[] getParameters() {
		LMethod lMethodAnnotation = (LMethod) method.getAnnotation(LMethod.class);
		return lMethodAnnotation.parameters();
	}
	
	public MethodResultAdapter getMethodAdapter() {
		LWrapper lWrapperAnnotation = (LWrapper) method.getAnnotation(LWrapper.class);
		MethodResultAdapter resultAdapter = null;
		LSolution lSolutionAnnotation = (LSolution) method.getAnnotation(LSolution.class);
		if(lSolutionAnnotation == null) {
			if(lWrapperAnnotation == null) {
				Class returnType = method.getReturnType();
				if(returnType.equals(Void.class) || returnType.equals(Boolean.class) || returnType.equals(boolean.class))
					return new HasSolutionAdapter(method);
				if(returnType.equals(Query.class))
					return new MethodResultAdapter.DefaultMethodResultAdapter(method);
			}
		}
		return getCompositionAdapter();
	}
	
	public SolutionCompositionAdapter getCompositionAdapter() {
		SolutionCompositionAdapter compositionAdapter = null;
		LWrapper lWrapperAnnotation = (LWrapper) method.getAnnotation(LWrapper.class);
		try {
			if(lWrapperAnnotation == null) {
				compositionAdapter = new OneSolutionAdapter(method);
			} else {
				compositionAdapter = (WrapperAdapter)lWrapperAnnotation.adapter().newInstance();
				compositionAdapter.setParameters(lWrapperAnnotation.value());
				compositionAdapter.setMethod(method);
			}
			compositionAdapter.setEachSolutionAdapter(getEachSolutionAdapter());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return compositionAdapter;
	}
	
	public EachSolutionAdapter getEachSolutionAdapter() {
		EachSolutionAdapter eachSolutionAdapter = null;
		try {
			LSolution lSolutionAnnotation = (LSolution) method.getAnnotation(LSolution.class);
			if(lSolutionAnnotation == null) {
				eachSolutionAdapter = EachSolutionAdapter.DefaultEachSolutionAdapter.class.newInstance(); //will answer a map of logic variable bindings
			} else {
				eachSolutionAdapter = (EachSolutionAdapter)lSolutionAnnotation.adapter().newInstance();
				eachSolutionAdapter.setParameters(lSolutionAnnotation.value());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return eachSolutionAdapter;
	}
	
	public ObjectToTermAdapter[] getParameterAdapters() {
		Annotation[][] parameterAnnotationsTable = method.getParameterAnnotations();
		ObjectToTermAdapter[] parametersAdapters = new ObjectToTermAdapter[parameterAnnotationsTable.length];
		for(int i = 0; i < parameterAnnotationsTable.length; i++) {
			Annotation[] parameterAnnotations = parameterAnnotationsTable[i];
			for(Annotation parameterAnnotation : parameterAnnotations) {
				LParameter lParameterAnnotation =  null;
				try {
					lParameterAnnotation = LParameter.class.cast(parameterAnnotation);
				} catch (ClassCastException e) {} //annotations others than LParameter will be ignored here
				if(lParameterAnnotation != null && !lParameterAnnotation.adapter().equals(NO_ADAPTER.class)) {
					Class parameterAdapterClass = lParameterAnnotation.adapter();
					ObjectToTermAdapter parameterAdapter = null;
					try {
						parameterAdapter = (ObjectToTermAdapter)parameterAdapterClass.newInstance(); //the parameters should be transformed using an instance of the parametersAdapterClass
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
					parameterAdapter.setParameters(lParameterAnnotation.value());
					parametersAdapters[i] = parameterAdapter;
				}
			}
		}
		return parametersAdapters;
	}
	
	public boolean isRawQuery() {
		return method.getAnnotation(LQuery.class) != null;
	}
	
	public String getRawQuery() {
		LQuery rawQueryAnnotation = (LQuery) method.getAnnotation(LQuery.class);
		if(rawQueryAnnotation == null)
			throw new RuntimeException("No raw query has been defined for method "+method.getName());
		return rawQueryAnnotation.value();
	}
	
}
