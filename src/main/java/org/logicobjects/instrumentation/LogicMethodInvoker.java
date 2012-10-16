package org.logicobjects.instrumentation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.core.LogicRoutine;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.logicobjects.term.Query;
import org.logicobjects.util.LogicUtil;

public class LogicMethodInvoker {

	private LogicEngineConfiguration logicEngineConfig;
	private LogicUtil logicUtil = new LogicUtil(logicEngineConfig.getEngine());
	
	public LogicMethodInvoker(LogicEngineConfiguration logicEngineConfig) {
		this.logicEngineConfig = logicEngineConfig;
	}
	
	public Object invoke(Object targetObject, Method method, Object[] argumentsArray) {
		LogicRoutine logicMethod = LogicRoutine.create((method));
		List arguments = Arrays.asList(argumentsArray);
		ParsedLogicMethod parsedLogicMethod = logicMethod.parse(targetObject, arguments);
		Query query = logicUtil.createQuery(parsedLogicMethod.asGoal());
		MethodResultAdapter resultAdapter = logicMethod.getMethodAdapter(parsedLogicMethod);
		Object result = resultAdapter.adapt(query);
		logicUtil.flushOutput(); //TODO maybe this should be customizable per method ?
		return result;	
	}
}
