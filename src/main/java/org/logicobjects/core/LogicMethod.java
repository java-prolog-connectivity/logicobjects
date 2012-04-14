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
		LMethod aLMethod = (LMethod) method.getAnnotation(LMethod.class);
		String name = aLMethod.name(); //method indicated at the annotation
		if(!name.isEmpty())
			return name;
		else
			return LogicUtil.javaNameToProlog(method.getName()); //if no name is provided in the annotation, answer the method name after converting it to prolog naming conventions
	}
	
	/**
	 * 
	 * @return the method parameters as specified in the annotation
	 */
	public String[] getParameters() {
		LMethod aLMethod = (LMethod) method.getAnnotation(LMethod.class);
		return aLMethod.parameters();
	}
	
	public MethodResultAdapter getMethodAdapter() {
		LWrapper aLWrapper = (LWrapper) method.getAnnotation(LWrapper.class);
		MethodResultAdapter resultAdapter = null;
		LSolution aLSolution = (LSolution) method.getAnnotation(LSolution.class);
		/**
		 * if this is true, the user has not specified the solution of the method
		 * By default, the solution will be a Map of variables names to bindings
		 * Before choosing this default solution, we take a look to the return types of the method to see if another behaviour is probably the desireble one
		 */
		if(aLSolution == null) {
			if(aLWrapper == null) {
				Class returnType = method.getReturnType();
				if(returnType.equals(Query.class))
					return new MethodResultAdapter.DefaultMethodResultAdapter(method);
				if(returnType.equals(Void.class) || returnType.equals(Boolean.class) || returnType.equals(boolean.class))
					return new HasSolutionAdapter(method);
				if(Number.class.isAssignableFrom(returnType))
					return new NumberOfSolutionsAdapter(method);
			}
		}
		return getCompositionAdapter();
	}
	
	public SolutionCompositionAdapter getCompositionAdapter() {
		SolutionCompositionAdapter compositionAdapter = null;
		LWrapper aLWrapper = (LWrapper) method.getAnnotation(LWrapper.class);
		try {
			if(aLWrapper == null) {
				compositionAdapter = new OneSolutionAdapter(method);
			} else {
				compositionAdapter = (WrapperAdapter)aLWrapper.adapter().newInstance();
				compositionAdapter.setParameters(aLWrapper.value());
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
			LSolution aLSolution = (LSolution) method.getAnnotation(LSolution.class);
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
		Annotation[][] parameterAnnotationsTable = method.getParameterAnnotations(); //a bidimensional array with all the annotations in the method parameters
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
	
	public boolean isRawQuery() {
		return method.getAnnotation(LQuery.class) != null;
	}
	
	public String getRawQuery() {
		LQuery aLQuery = (LQuery) method.getAnnotation(LQuery.class);
		if(aLQuery == null)
			throw new RuntimeException("No raw query has been defined for method "+method.getName());
		return aLQuery.value();
	}
	
}
