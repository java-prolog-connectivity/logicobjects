package org.logicobjects.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jpl.Query;
import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.methodparameters.ParametersAdapter;
import org.logicobjects.adapter.methodresult.HasSolutionAdapter;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.adapter.methodresult.NumberOfSolutionsAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.SolutionToLObjectAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.OneSolutionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.annotation.LTermAdapter.LTermAdapterUtil;
import org.logicobjects.annotation.method.LArgsAdapter;
import org.logicobjects.annotation.method.LArgsAdapter.LArgsAdapterUtil;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LWrapper;
import org.logicobjects.instrumentation.AbstractLogicMethodParser;
import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.logicobjects.instrumentation.ParsingData;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.AbstractTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Primitives;

public abstract class AbstractLogicMethod {
	private Logger logger = LoggerFactory.getLogger(AbstractLogicMethod.class);
	
	private AbstractLogicMethodParser methodParser;
	private Method method;
	
	//private String queryString;
	
	/**
	 * indicates if the query string should be parsed replacing symbols and expressions
	 * if it has been generated from the method name then it should not be parsed
	 */
	//protected boolean unparsedQueryString; 
	
	public AbstractLogicMethod(Method method) {
		this.method = method;
	}
	
	protected <A extends Annotation> A getAnnotation(Class<A> clazz) {
		return getWrappedMethod().getAnnotation(clazz);
	}
	
	public static AbstractLogicMethod create(Method method) {
		if(LogicMethod.isLogicMethod(method))
			return new LogicMethod(method);
		else
			return RawLogicQuery.create(method);
	}

	public Method getWrappedMethod() {
		return method;
	}
	

	public MethodResultAdapter getMethodAdapter(ParsedLogicMethod parsedLogicMethod) {
		LWrapper aLWrapper = (LWrapper)getAnnotation(LWrapper.class);
		LSolution aLSolution = (LSolution)getAnnotation(LSolution.class);
		/**
		 * if this is true, the user has not specified the solution of the method
		 * By default, the solution will be a Map of variables names to bindings
		 * Before choosing this default solution, we take a look to the return types of the method to see if another behaviour is probably the desirable one
		 */
		if(aLSolution == null) {
			if(aLWrapper == null) {
				Class returnType = getWrappedMethod().getReturnType();
				if(returnType.equals(Query.class))
					return new MethodResultAdapter.DefaultMethodResultAdapter();
				if(returnType.equals(Void.class) || returnType.equals(Boolean.class) || returnType.equals(boolean.class))
					return new HasSolutionAdapter();
				if(Number.class.isAssignableFrom(Primitives.wrap(returnType)))
					return new NumberOfSolutionsAdapter();
			}
		}
		return getCompositionAdapter(parsedLogicMethod);
	}
	
	public SolutionCompositionAdapter getCompositionAdapter(ParsedLogicMethod parsedLogicMethod) {
		SolutionCompositionAdapter compositionAdapter = null;
		LWrapper aLWrapper = (LWrapper)getAnnotation(LWrapper.class);
		try {
			if(aLWrapper == null) {
				compositionAdapter = new OneSolutionAdapter();
			} else {
				compositionAdapter = (WrapperAdapter)aLWrapper.adapter().getConstructor().newInstance();
				//compositionAdapter = (WrapperAdapter)aLWrapper.adapter().newInstance();
				//compositionAdapter.setMethod(getWrappedMethod());
				//compositionAdapter.setParameters(aLWrapper.value());
			}
			compositionAdapter.setParsedLogicMethod(parsedLogicMethod);
			EachSolutionAdapter eachSolutionAdapter = getEachSolutionAdapter(compositionAdapter);
			compositionAdapter.setEachSolutionAdapter(eachSolutionAdapter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return compositionAdapter;
	}
	
	public String getEachSolutionValue() {
		String eachSolutionValue = null;
		LSolution aLSolution = (LSolution)getAnnotation(LSolution.class);
		if(aLSolution != null)
			eachSolutionValue = aLSolution.value();
		return eachSolutionValue;
	}
	
	public EachSolutionAdapter getEachSolutionAdapter(SolutionCompositionAdapter compositionAdapter) {
		EachSolutionAdapter eachSolutionAdapter = null;
		try {
			LSolution aLSolution = (LSolution)getAnnotation(LSolution.class);
			if(aLSolution == null) {
				if(Map.class.isAssignableFrom(AbstractTypeWrapper.wrap(compositionAdapter.getEachSolutionType()).asClass())) {
					eachSolutionAdapter = EachSolutionAdapter.EachSolutionMapAdapter.class.newInstance(); //will answer a map of logic variable bindings
					return eachSolutionAdapter;
				} else {
					return new SolutionToLObjectAdapter();
				}
			} else {
				eachSolutionAdapter = (EachSolutionAdapter)aLSolution.adapter().newInstance();
				return eachSolutionAdapter;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * If declared, this array transform the original array of parameters to a new object array
	 * @return
	 */
	public ParametersAdapter getArrayParametersAdapter() {
		LArgsAdapter aLParamsAdapter = getAnnotation(LArgsAdapter.class);
		if(aLParamsAdapter == null)
			return null;
		try {
			ParametersAdapter parametersAdapter = (ParametersAdapter)LArgsAdapterUtil.getAdapterClass(aLParamsAdapter).newInstance();
			return parametersAdapter;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public ObjectToTermAdapter[] getEachParameterAdapters() {
		Annotation[][] parameterAnnotationsTable = getWrappedMethod().getParameterAnnotations(); //a bidimensional array with all the annotations in the method parameters
		ObjectToTermAdapter[] parametersAdapters = new ObjectToTermAdapter[parameterAnnotationsTable.length];
		for(int i = 0; i < parameterAnnotationsTable.length; i++) {
			Annotation[] parameterAnnotations = parameterAnnotationsTable[i];
			for(Annotation parameterAnnotation : parameterAnnotations) {
				LTermAdapter aLTermAdapter =  null;
				try {
					aLTermAdapter = LTermAdapter.class.cast(parameterAnnotation);
				} catch (ClassCastException e) {} //annotations others than LParameter will be ignored here
				if(aLTermAdapter != null) {
					Class parameterAdapterClass = LTermAdapterUtil.getAdapterClass(aLTermAdapter);
					if(parameterAdapterClass != null) {
						ObjectToTermAdapter parameterAdapter = null;
						try {
							parameterAdapter = (ObjectToTermAdapter)parameterAdapterClass.newInstance(); //the parameters should be transformed using an instance of the parametersAdapterClass
						} catch(Exception e) {
							throw new RuntimeException(e);
						}
						parameterAdapter.setParameters(aLTermAdapter.value());
						parametersAdapters[i] = parameterAdapter;
					}
					
				}
			}
		}
		return parametersAdapters;
	}
	
	public boolean declaresLogicParameters() {
		return getParameters() != null;
	}
	
	
	public Object[] adaptOriginalParameters(Object[] originalParameters) {
		Object[] adaptedParams = originalParameters;
		ObjectToTermAdapter[] parameterAdapters = getEachParameterAdapters();
		for(int i = 0; i <  parameterAdapters.length; i++) {
			ObjectToTermAdapter parameterAdapter = parameterAdapters[i];
			if(parameterAdapter != null) {
				adaptedParams[i] = parameterAdapter.adapt(adaptedParams[i]);
			}
		}
	
		ParametersAdapter parametersAdapter = getArrayParametersAdapter();
		if(parametersAdapter != null)
			adaptedParams = parametersAdapter.adapt(adaptedParams);
		
		return adaptedParams;
	}
	
	public AbstractLogicMethodParser getMethodParser() {
		if(methodParser == null)
			methodParser = AbstractLogicMethodParser.create(method);
		return methodParser;
	}
	

	public ParsedLogicMethod parse(Object targetObject, Object[] originalParameters) {
		Object[] adaptedParams = adaptOriginalParameters(originalParameters);
		ParsingData parsedData = getMethodParser().parse().parsedData(targetObject, adaptedParams);
		ParsedLogicMethod parsedLogicMethod = new ParsedLogicMethod(this, targetObject, adaptedParams, parsedData);
		computeParameters(parsedLogicMethod);
		computeMethodName(parsedLogicMethod);
		computeQueryString(parsedLogicMethod);		
		return parsedLogicMethod;
	}



	public String logicMethodName() {
		String logicMethodName;
		if(customMethodName() == null || customMethodName().isEmpty()) {  //test if a method name has been indicated at the annotation
			logicMethodName = LogicUtil.javaNameToProlog(getWrappedMethod().getName()); //if no name is provided in the annotation, it will be the method name after converting it to prolog naming conventions
		} else {
			logicMethodName = customMethodName();
		}
		return logicMethodName;
	}
	
	public Term getEachSolutionTerm(ParsedLogicMethod parsedLogicMethod) {
		LSolution aLSolution = getAnnotation(LSolution.class);
		if(aLSolution == null)
			return asTerm(parsedLogicMethod);
		
		String solutionString = parsedLogicMethod.getParsedData().getSolutionString();
		if(solutionString == null || solutionString.isEmpty()) {
			return asTerm(parsedLogicMethod);
		} else {
			LogicEngine engine = LogicEngine.getDefault();
			return engine.textToTerm(solutionString);
		}
	}
	
	/**
	 * Answers the method and its parameters as a logic term
	 * @param parsedLogicMethod
	 * @return
	 */
	public Term asTerm(ParsedLogicMethod parsedLogicMethod) {
		return LogicUtil.asTerm(parsedLogicMethod.getComputedMethodName(), ArrayToTermAdapter.arrayAsTerms(parsedLogicMethod.getComputedParameters()));
	}
	
	protected void computeParameters(ParsedLogicMethod parsedLogicMethod) {
		Object[] params = null;
		
		//we convert the string representation of every param in a term
		if(declaresLogicParameters()) {
			LogicEngine engine = LogicEngine.getDefault();
			List<Term> newTermParams = new ArrayList<Term>();
			for(Object stringTerm : parsedLogicMethod.getParsedData().getParameters()) {
				newTermParams.add(engine.textToTerm(stringTerm.toString()));
			}
			params = newTermParams.toArray(new Term[] {});
		} else {
			params = parsedLogicMethod.getOriginalParameters();
		}
		parsedLogicMethod.setComputedParameters(params);
	}
	
	/**
	 * The default implementation ignores any information in the parsedLogicMethod parameter, this could change in the future if it is found that the method name should be parsed
	 */
	protected void computeQueryString(ParsedLogicMethod parsedLogicMethod) {
		parsedLogicMethod.setComputedQueryString(logicMethodName()); //by default, the query to be executed is the method name (if parameters are present they will be added)
	}
	
	private void computeMethodName(ParsedLogicMethod parsedLogicMethod) {
		parsedLogicMethod.setComputedMethodName(logicMethodName());
	}
	
	
	public abstract String[] getParameters();
	public abstract Query asQuery(ParsedLogicMethod parsedLogicMethod);
	public abstract ParsingData getDataToParse();
	public abstract String customMethodName();
	
}


