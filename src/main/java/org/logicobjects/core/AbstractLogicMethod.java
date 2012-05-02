package org.logicobjects.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.methodresult.HasSolutionAdapter;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.adapter.methodresult.NumberOfSolutionsAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.OneSolutionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.method.LParameter;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LWrapper;
import org.logicobjects.util.AnnotationConstants.NO_ADAPTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLogicMethod {

	private Method method;
	private Logger logger = LoggerFactory.getLogger(AbstractLogicMethod.class);
	
	public AbstractLogicMethod(Method method) {
		this.method = method;
	}
	
	public static AbstractLogicMethod create(Method method) {
		if(isRawQuery(method))
			return new RawLogicQuery(method);
		else
			return new LogicMethod(method);
			
	}
	
	public static boolean isRawQuery(Method method) {
		return method.getAnnotation(LQuery.class) != null;
	}
	
	public Method getWrappedMethod() {
		return method;
	}
	

	public MethodResultAdapter getMethodAdapter(Object targetObject, Object[] javaMethodParams) {
		LWrapper aLWrapper = (LWrapper) getWrappedMethod().getAnnotation(LWrapper.class);
		LSolution aLSolution = (LSolution) getWrappedMethod().getAnnotation(LSolution.class);
		/**
		 * if this is true, the user has not specified the solution of the method
		 * By default, the solution will be a Map of variables names to bindings
		 * Before choosing this default solution, we take a look to the return types of the method to see if another behaviour is probably the desireble one
		 */
		if(aLSolution == null) {
			if(aLWrapper == null) {
				Class returnType = getWrappedMethod().getReturnType();
				if(returnType.equals(Query.class))
					return new MethodResultAdapter.DefaultMethodResultAdapter(getWrappedMethod(), targetObject, javaMethodParams);
				if(returnType.equals(Void.class) || returnType.equals(Boolean.class) || returnType.equals(boolean.class))
					return new HasSolutionAdapter(getWrappedMethod(), targetObject, javaMethodParams);
				if(Number.class.isAssignableFrom(returnType))
					return new NumberOfSolutionsAdapter(getWrappedMethod(), targetObject, javaMethodParams);
			}
		}
		return getCompositionAdapter(targetObject, javaMethodParams);
	}
	
	public SolutionCompositionAdapter getCompositionAdapter(Object targetObject, Object[] javaMethodParams) {
		SolutionCompositionAdapter compositionAdapter = null;
		LWrapper aLWrapper = (LWrapper) getWrappedMethod().getAnnotation(LWrapper.class);
		try {
			if(aLWrapper == null) {
				compositionAdapter = new OneSolutionAdapter(getWrappedMethod(), targetObject, javaMethodParams);
			} else {
				compositionAdapter = (WrapperAdapter)aLWrapper.adapter().getConstructor(Method.class, Object.class, Object[].class).newInstance(getWrappedMethod(), targetObject, javaMethodParams);
				//compositionAdapter = (WrapperAdapter)aLWrapper.adapter().newInstance();
				//compositionAdapter.setMethod(getWrappedMethod());
				compositionAdapter.setParameters(aLWrapper.value());
			}
			compositionAdapter.setEachSolutionAdapter(getEachSolutionAdapter());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return compositionAdapter;
	}
	
	public String getEachSolutionValue() {
		String eachSolutionValue = null;
		LSolution aLSolution = (LSolution) getWrappedMethod().getAnnotation(LSolution.class);
		if(aLSolution != null)
			eachSolutionValue = aLSolution.value();
		return eachSolutionValue;
	}
	
	public EachSolutionAdapter getEachSolutionAdapter() {
		EachSolutionAdapter eachSolutionAdapter = null;
		try {
			LSolution aLSolution = (LSolution) getWrappedMethod().getAnnotation(LSolution.class);
			if(aLSolution == null) {
				eachSolutionAdapter = EachSolutionAdapter.DefaultEachSolutionAdapter.class.newInstance(); //will answer a map of logic variable bindings
			} else {
				eachSolutionAdapter = (EachSolutionAdapter)aLSolution.adapter().newInstance();
				eachSolutionAdapter.setParameters(aLSolution.value());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return eachSolutionAdapter;
	}
	
	public ObjectToTermAdapter[] getParameterAdapters() {
		Annotation[][] parameterAnnotationsTable = getWrappedMethod().getParameterAnnotations(); //a bidimensional array with all the annotations in the method parameters
		ObjectToTermAdapter[] parametersAdapters = new ObjectToTermAdapter[parameterAnnotationsTable.length];
		for(int i = 0; i < parameterAnnotationsTable.length; i++) {
			Annotation[] parameterAnnotations = parameterAnnotationsTable[i];
			for(Annotation parameterAnnotation : parameterAnnotations) {
				LParameter aLParameter =  null;
				try {
					aLParameter = LParameter.class.cast(parameterAnnotation);
				} catch (ClassCastException e) {} //annotations others than LParameter will be ignored here
				if(aLParameter != null && !aLParameter.adapter().equals(NO_ADAPTER.class)) {
					Class parameterAdapterClass = aLParameter.adapter();
					ObjectToTermAdapter parameterAdapter = null;
					try {
						parameterAdapter = (ObjectToTermAdapter)parameterAdapterClass.newInstance(); //the parameters should be transformed using an instance of the parametersAdapterClass
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
					parameterAdapter.setParameters(aLParameter.value());
					parametersAdapters[i] = parameterAdapter;
				}
			}
		}
		return parametersAdapters;
	}
	
	public abstract Query asQuery(Object targetObject, Object[] params);
	
}
