package org.logicobjects.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jpc.LogicUtil;
import org.jpc.term.Compound;
import org.jpc.term.Query;
import org.jpc.term.Term;
import org.jpc.term.Variable;
import org.logicobjects.LogicObjects;
import org.logicobjects.LogicObjectsPreferences;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.methodparameters.MethodArgumentsAdapter;
import org.logicobjects.adapter.methodresult.HasSolutionAdapter;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.adapter.methodresult.NumberOfSolutionsAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.SolutionToLObjectAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.OneSolutionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.annotation.LTermAdapter.LTermAdapterUtil;
import org.logicobjects.annotation.method.LArgumentsAdapter;
import org.logicobjects.annotation.method.LArgumentsAdapter.LArgsAdapterUtil;
import org.logicobjects.annotation.method.LComposition;
import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.instrumentation.LogicMethodParser;
import org.logicobjects.instrumentation.LogicMethodParsingData;
import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Primitives;


/**
 * This class wraps a Java method acting as a logic method
 * 
 * @author scastro
 *
 */
public abstract class LogicRoutine {
	private Logger logger = LoggerFactory.getLogger(LogicRoutine.class);
	private Method method;
	//protected LogicEngineConfiguration logicEngineConfig;
	protected LogicUtil logicUtil;
	
	//private String queryString;
	
	/**
	 * indicates if the query string should be parsed replacing symbols and expressions
	 * if it has been generated from the method name then it should not be parsed
	 */
	//protected boolean unparsedQueryString; 
	
	public LogicRoutine(Method method) {
		this.logicUtil = LogicObjects.getLogicUtilFor(method.getDeclaringClass());
		this.method = method;
	}
	
	public static LogicRoutine create(Method method) {
		if(LogicMethod.isLogicMethod(method))
			return new LogicMethod(method);
		else
			return RawLogicQuery.create(method);
	}
	/*
	public static LogicRoutineFactory with(LogicEngineConfiguration logicEngineConfig) {
		return new LogicRoutineFactory(logicEngineConfig);
	}
	*/
	public static boolean isAnnotatedAsLogicRoutine(Method method) {
		return method.isAnnotationPresent(LMethod.class) || method.isAnnotationPresent(LQuery.class) || method.isAnnotationPresent(LExpression.class);
	}
	
	protected <A extends Annotation> A getAnnotation(Class<A> annotation) {
		return getWrappedMethod().getAnnotation(annotation);
	}
	
	protected <A extends Annotation> boolean isAnnotationPresent(Class<A> annotation) {
		return getAnnotation(annotation) != null;
	}
	


	public Method getWrappedMethod() {
		return method;
	}
	

	public MethodResultAdapter getMethodAdapter(ParsedLogicMethod parsedLogicMethod) {
		LComposition aLWrapper = getAnnotation(LComposition.class);
		LSolution aLSolution = getAnnotation(LSolution.class);
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
		LComposition aLWrapper = (LComposition)getAnnotation(LComposition.class);
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
	 * If declared, this array transform the original array of method arguments to a new object array
	 * @return
	 */
	public MethodArgumentsAdapter getMethodArgumentsArrayAdapter() {
		LArgumentsAdapter aLArgsAdapter = getAnnotation(LArgumentsAdapter.class);
		if(aLArgsAdapter == null)
			return null;
		try {
			MethodArgumentsAdapter argumentsAdapter = (MethodArgumentsAdapter)LArgsAdapterUtil.getAdapterClass(aLArgsAdapter).newInstance();
			return argumentsAdapter;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<ObjectToTermAdapter> getEachMethodArgumentAdapters() {
		Annotation[][] parameterAnnotationsTable = getWrappedMethod().getParameterAnnotations(); //a bidimensional array with all the annotations in the method parameters
		List<ObjectToTermAdapter> allMethodArgumentAdapters = new ArrayList<>();
		
		for(Annotation[] parameterAnnotations : parameterAnnotationsTable) {
			ObjectToTermAdapter methodArgumentAdapter = null;
			for(Annotation parameterAnnotation : parameterAnnotations) {
				LTermAdapter aLTermAdapter =  null;
				try {
					aLTermAdapter = LTermAdapter.class.cast(parameterAnnotation);
				} catch (ClassCastException e) {} //annotations others than LParameter will be ignored here
				if(aLTermAdapter != null) {
					Class methodArgumentAdapterClass = LTermAdapterUtil.getAdapterClass(aLTermAdapter);
					if(methodArgumentAdapterClass != null) {
						try {
							methodArgumentAdapter = (ObjectToTermAdapter)methodArgumentAdapterClass.newInstance(); //the method arguments should be transformed using an instance of the MethodArgumentsAdapterClass
						} catch(Exception e) {
							throw new RuntimeException(e);
						}
						methodArgumentAdapter.setParameters(Arrays.asList(aLTermAdapter.args()));
					}
					
				}
			}
			allMethodArgumentAdapters.add(methodArgumentAdapter);
		}
		
		return allMethodArgumentAdapters;
	}
	
	public boolean hasLogicMethodArguments() {
		return getLogicMethodArguments() != null;
	}
	
	
	public List adaptOriginalMethodArguments(List originalMethodArguments) {
		List adaptedMethodArguments = new ArrayList(originalMethodArguments);
		List<ObjectToTermAdapter> methodArgumentAdapters = getEachMethodArgumentAdapters();
		for(int i = 0; i <  methodArgumentAdapters.size(); i++) {
			ObjectToTermAdapter methodArgumentAdapter = methodArgumentAdapters.get(i);
			if(methodArgumentAdapter != null) {
				adaptedMethodArguments.set(i, methodArgumentAdapter.adapt(adaptedMethodArguments.get(i)));
			}
		}
	

		
		MethodArgumentsAdapter methodArgumentsAdapter = getMethodArgumentsArrayAdapter();
		if(methodArgumentsAdapter != null)
			adaptedMethodArguments = methodArgumentsAdapter.adapt(adaptedMethodArguments);
		
		return adaptedMethodArguments;
	}
	
	/*
	public AbstractLogicMethodParser getMethodParser() {
		if(methodParser == null)
			methodParser = AbstractLogicMethodParser.create(method);
		return methodParser;
	}
	*/

	public ParsedLogicMethod parse(Object targetObject, List originalMethodArguments) {
		List adaptedMethodArguments = adaptOriginalMethodArguments(originalMethodArguments);
		//ParsingData parsedData = AbstractLogicMethodParser.create(this).parse().parsedData(targetObject, adaptedMethodArguments);
		
		ParsedLogicMethod parsedLogicMethod = LogicMethodParser.create(this).parse().parsedLogicMethod(targetObject, adaptedMethodArguments);
		//configureParsedLogicMethod(parsedLogicMethod);
		return parsedLogicMethod;
	}
	
	public boolean hasCustomMethodName() {
		return !(customMethodName() == null || customMethodName().isEmpty());
	}
	
	public String logicMethodName() {
		String logicMethodName;
		if(hasCustomMethodName()) {  //test if a method name has been indicated at the annotation
			logicMethodName = customMethodName();
		} else {
			logicMethodName = LogicUtil.javaNameToProlog(getWrappedMethod().getName()); //if no name is provided in the annotation, it will be the method name after converting it to prolog naming conventions
		}
		return logicMethodName;
	}
	
	public Term getEachSolutionTerm(ParsedLogicMethod parsedLogicMethod) {
		Term eachSolutionTerm = null;
		LSolution aLSolution = getAnnotation(LSolution.class);
		if(aLSolution != null) {
			String solutionString = parsedLogicMethod.getParsedData().getSolutionString();
			if(solutionString != null && !solutionString.isEmpty()) {
				eachSolutionTerm = logicUtil.asTerm(solutionString);
			} 
		}
		if(eachSolutionTerm == null) {
			Term goal = parsedLogicMethod.asGoal();
			if(goal.hasVariable(LogicObjectsPreferences.IMPLICIT_RETURN_VARIABLE))
				eachSolutionTerm = new Variable(LogicObjectsPreferences.IMPLICIT_RETURN_VARIABLE);
		}
		if(eachSolutionTerm == null)
			eachSolutionTerm = asTerm(parsedLogicMethod);
		return eachSolutionTerm;
	}
	

	/**
	 * Answers the method and its arguments as a logic term
	 * @param parsedLogicMethod
	 * @return
	 */
	public Term asTerm(ParsedLogicMethod parsedLogicMethod) {
		return new Compound(parsedLogicMethod.getComputedMethodName(), new ObjectToTermAdapter().adaptObjects(parsedLogicMethod.getComputedMethodArguments()));
	}
	
	
	public void configureParsedLogicMethod(ParsedLogicMethod parsedLogicMethod) {
		configureParsedLogicMethodArguments(parsedLogicMethod);
		configureParsedLogicMethodName(parsedLogicMethod);
		configureParsedLogicMethodQueryString(parsedLogicMethod);	
	}
	
	protected void configureParsedLogicMethodArguments(ParsedLogicMethod parsedLogicMethod) {
		List arguments = null;
		
		//we convert the string representation of every argument in a term
		if(hasLogicMethodArguments()) {
			List<Term> newTermArgs = new ArrayList<Term>();
			for(Object stringTerm : parsedLogicMethod.getParsedData().getMethodArguments()) {
				newTermArgs.add(logicUtil.asTerm(stringTerm.toString()));
			}
			arguments = newTermArgs;
		} else {
			arguments = parsedLogicMethod.getOriginalMethodArguments();
		}
		parsedLogicMethod.setComputedMethodArguments(arguments);
	}

	protected void configureParsedLogicMethodName(ParsedLogicMethod parsedLogicMethod) {
		if(hasCustomMethodName())
			parsedLogicMethod.setComputedMethodName(parsedLogicMethod.getParsedData().getQueryString());
		else
			parsedLogicMethod.setComputedMethodName(logicMethodName());
	}
	
	/**
	 * The default implementation ignores any information in the parsedLogicMethod parameter, this could change in the future if it is found that the method name should be parsed
	 */
	protected void configureParsedLogicMethodQueryString(ParsedLogicMethod parsedLogicMethod) {
		if(hasCustomMethodName())
			parsedLogicMethod.setComputedQueryString(parsedLogicMethod.getParsedData().getQueryString());
		else
			parsedLogicMethod.setComputedQueryString(logicMethodName()); //by default, the query to be executed is the method name (if arguments are present they will be taken into account)
	}
	
	
	
	/**
	 * 
	 * @return relevant unparsed logic method data
	 */
	public abstract LogicMethodParsingData getDataToParse();
	
	/**
	 * 
	 * @return the method arguments as specified in the annotation
	 */
	public abstract List<String> getLogicMethodArguments();
	
	public abstract Term asGoal(ParsedLogicMethod parsedLogicMethod);
	public abstract String customMethodName();
	
}


