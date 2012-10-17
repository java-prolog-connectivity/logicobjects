package org.logicobjects.core;

import java.lang.reflect.Method;

import org.jpc.logicengine.LogicEngineConfiguration;
import org.logicobjects.annotation.method.LQuery;
//TODO delete
public class LogicRoutineFactory {
/*
	private LogicEngineConfiguration logicEngineConfig;
	
	public LogicRoutineFactory(LogicEngineConfiguration logicEngineConfig) {
		this.logicEngineConfig = logicEngineConfig;
	}
	
	public LogicRoutine createLogicRoutine(Method method) {
		if(LogicMethod.isLogicMethod(method))
			return new LogicMethod(logicEngineConfig, method);
		else
			return createRawLogicQuery(method);
	}
	
	private RawLogicQuery createRawLogicQuery(Method method) {
		LQuery aLQuery = method.getAnnotation(LQuery.class);
		if(aLQuery != null && !aLQuery.value().isEmpty())
			return new MultiPredicateQuery(logicEngineConfig, method);
		else
			return new SimplePredicateQuery(logicEngineConfig, method);
	}
*/
}
