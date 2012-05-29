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
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.logicobjects.instrumentation.ParsingData;
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
	 * @return the method parameters as specified in the annotation
	 */
	public String[] getParameters() {
		if(AnnotationConstants.isNullArray(aLMethod.args()))
			return null;
		return aLMethod.args();
	}

	@Override
	public Query asQuery(ParsedLogicMethod parsedMethodData) {
		Object targetObject = parsedMethodData.getTargetObject();
		LogicObject lo = new LogicObjectAdapter().adapt(targetObject, new MethodInvokerContext(targetObject.getClass()));
		String logicMethodName = parsedMethodData.getComputedMethodName();
		return lo.invokeMethod(logicMethodName, parsedMethodData.getComputedParameters());
	}


	
	@Override
	public ParsingData getDataToParse() {
		ParsingData parsingData = new ParsingData();
		parsingData.setParameters(getParameters());
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
