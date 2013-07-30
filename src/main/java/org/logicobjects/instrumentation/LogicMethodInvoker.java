package org.logicobjects.instrumentation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jpc.engine.prolog.driver.AbstractPrologEngineDriver;
import org.jpc.query.Query;
import org.jpc.util.PrologUtil;
import org.logicobjects.adapter.methodresult.MethodResultAdapter;
import org.logicobjects.core.LogicRoutine;

public class LogicMethodInvoker {

	private AbstractPrologEngineDriver logicEngineConfig;
	private PrologUtil logicUtil;
	
	public LogicMethodInvoker(AbstractPrologEngineDriver logicEngineConfig) {
		this.logicEngineConfig = logicEngineConfig;
		this.logicUtil = new PrologUtil(logicEngineConfig.getEngine());
	}
	
	public Object invoke(Object targetObject, Method method, Object[] argumentsArray) {
		try {
			LogicRoutine logicMethod = LogicRoutine.create((method));
			List arguments = Arrays.asList(argumentsArray);
			ParsedLogicMethod parsedLogicMethod = logicMethod.parse(
					targetObject, arguments);
			Query query = logicUtil.query(parsedLogicMethod.asGoal());
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
