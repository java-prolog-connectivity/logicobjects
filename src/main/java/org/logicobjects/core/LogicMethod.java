package org.logicobjects.core;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jpc.term.Term;
import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LMethod.LMethodUtil;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.converter.context.old.MethodInvokerAdaptationContext;
import org.logicobjects.converter.old.LogicObjectAdapter;
import org.logicobjects.instrumentation.LogicMethodParsingData;
import org.logicobjects.instrumentation.ParsedLogicMethod;

public class LogicMethod extends LogicRoutine {

	private LMethod aLMethod;
	
	public static boolean isLogicMethod(Method method) {
		return method.getAnnotation(LMethod.class) != null || (!method.isAnnotationPresent(LQuery.class) && !method.isAnnotationPresent(LExpression.class));
	}
	
	public LogicMethod(Method method) {
		super(method);
		aLMethod = (LMethod)getAnnotation(LMethod.class);
		//if(aLMethod == null)
			//throw new RuntimeException("The method "+getWrappedMethod().getName() + " is not a logic method");
	}

	
	/**
	 * 
	 * @return the method arguments as specified in the annotation
	 */
	@Override
	public List<String> getLogicMethodArguments() {
		List<String> lMethodArgs = null;
		if(aLMethod != null) {
			lMethodArgs = LMethodUtil.getArgs(aLMethod);
		}
		return lMethodArgs;
	}

	@Override
	public Term asGoal(ParsedLogicMethod parsedMethodData) {
		Object targetObject = parsedMethodData.getTargetObject();
		LogicObject lo = new LogicObjectAdapter().adapt(targetObject, new MethodInvokerAdaptationContext(targetObject.getClass()));
		String logicMethodName = parsedMethodData.getComputedMethodName();
		Term goal = lo.asGoal(logicMethodName, parsedMethodData.getComputedMethodArguments());
		return goal;
	}


	
	@Override
	public LogicMethodParsingData getDataToParse() {
		LogicMethodParsingData parsingData = new LogicMethodParsingData();
		if(hasCustomMethodName()) //this is to avoid parsing method names such as $1. This is a valid Java method name, but would be interpreted by the parser as "a String given by the first argument of the logic method"
			parsingData.setQueryString(logicMethodName());
		parsingData.setMethodArguments(getLogicMethodArguments());
		parsingData.setSolutionString(getEachSolutionValue());
		return parsingData;
	}


	@Override
	public String customMethodName() {
		if(aLMethod != null)
			return aLMethod.name();
		return null;
	}

	





	
	
	/*
	@Override
	protected void computeSolution(ParsedLogicMethod parsedLogicMethod) {
		LSolution aLSolution = getAnnotation(LSolution.class);
		if(aLSolution != null) {
			String parsedSolutionString = parsedLogicMethod.getParsedData().getSolutionString();
			if(parsedSolutionString == null || parsedSolutionString.isEmpty()) {
				
			} else {
				LogicEngine engine = LogicEngine.getDefault();
				
				engine.textToTerm
			}
		}
			
	}
	*/
	
}
