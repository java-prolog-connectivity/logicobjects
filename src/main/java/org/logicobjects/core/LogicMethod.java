package org.logicobjects.core;

import java.lang.reflect.Method;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.MethodInvokerContext;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LMethod.LMethodUtil;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.logicobjects.instrumentation.LogicMethodParsingData;
import org.logicobjects.util.AnnotationConstants;
import org.logicobjects.util.LogicUtil;

public class LogicMethod extends AbstractLogicMethod {

	private LMethod aLMethod;
	
	public static boolean isLogicMethod(Method method) {
		return method.getAnnotation(LMethod.class) != null;
	}
	
	public LogicMethod(Method method) {
		super(method);
		aLMethod = (LMethod)getAnnotation(LMethod.class);
		if(aLMethod == null)
			throw new RuntimeException("The method "+getWrappedMethod().getName() + " is not a logic method");
	}

	
	/**
	 * 
	 * @return the method arguments as specified in the annotation
	 */
	@Override
	public String[] getLogicMethodArguments() {
		return LMethodUtil.getArgs(aLMethod);
	}

	@Override
	public Query asQuery(ParsedLogicMethod parsedMethodData) {
		Object targetObject = parsedMethodData.getTargetObject();
		LogicObject lo = new LogicObjectAdapter().adapt(targetObject, new MethodInvokerContext(targetObject.getClass()));
		String logicMethodName = parsedMethodData.getComputedMethodName();
		return lo.invokeMethod(logicMethodName, parsedMethodData.getComputedMethodArguments());
	}


	
	@Override
	public LogicMethodParsingData getDataToParse() {
		LogicMethodParsingData parsingData = new LogicMethodParsingData();
		parsingData.setMethodArguments(getLogicMethodArguments());
		parsingData.setSolutionString(getEachSolutionValue());
		return parsingData;
	}


	@Override
	public String customMethodName() {
		return aLMethod.name();
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
