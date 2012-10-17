package org.logicobjects.instrumentation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jpc.LogicUtil;
import org.jpc.logicengine.LogicEngineConfiguration;
import org.jpc.term.Query;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.core.LogicRoutine;

public class LogicMethodInvoker {

	private LogicEngineConfiguration logicEngineConfig;
	private LogicUtil logicUtil;
	
	public LogicMethodInvoker(LogicEngineConfiguration logicEngineConfig) {
		this.logicEngineConfig = logicEngineConfig;
		this.logicUtil = new LogicUtil(logicEngineConfig.getEngine());
	}
	
	public Object invoke(Object targetObject, Method method, Object[] argumentsArray) {
		try {
			LogicRoutine logicMethod = LogicRoutine.create((method));
			List arguments = Arrays.asList(argumentsArray);
			ParsedLogicMethod parsedLogicMethod = logicMethod.parse(
					targetObject, arguments);
			Query query = logicUtil.createQuery(parsedLogicMethod.asGoal());
			MethodResultAdapter resultAdapter = logicMethod
					.getMethodAdapter(parsedLogicMethod);
			Object result = resultAdapter.adapt(query);
			logicUtil.flushOutput(); //TODO maybe this should be customizable per method ?
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}
}
