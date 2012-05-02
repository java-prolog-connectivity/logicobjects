package org.logicobjects.adapter.methodparameters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jpl.Term;

import org.logicobjects.core.AbstractLogicMethod;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicMethod;
import org.logicobjects.instrumentation.AbstractLogicMethodParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TermParametersAdapter extends ParametersAdapter {
	private static Logger logger = LoggerFactory.getLogger(TermParametersAdapter.class);

	private Object targetObject;
	private Method targetMethod;
	private AbstractLogicMethod logicMethod;
	
	public TermParametersAdapter(Method targetMethod, Object targetObject) {
		this.targetObject = targetObject;
		this.targetMethod = targetMethod;
		this.logicMethod = AbstractLogicMethod.create(targetMethod);
		if(logicMethod instanceof LogicMethod)
			setParameters(LogicMethod.class.cast(logicMethod).getParameters());
	}
	
	@Override
	public Object[] adapt(Object[] javaMethodParams) {
		LogicEngine engine = LogicEngine.getDefault();
		Object[] newParamStrings = AbstractLogicMethodParser.create(targetMethod).resolveInputTokens(targetObject, javaMethodParams);
		//we convert the string representation of every param in a term
		List<Term> newTermParams = new ArrayList<Term>();
		for(Object stringTerm : newParamStrings) {
			newTermParams.add(engine.textToTerm(stringTerm.toString()));
		}
		return newTermParams.toArray(new Term[] {});
	}



	
	

	
}

