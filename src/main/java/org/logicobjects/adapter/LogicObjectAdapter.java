package org.logicobjects.adapter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jpc.LogicUtil;
import org.jpc.logicengine.LogicEngine;
import org.jpc.term.Variable;
import org.logicobjects.LogicObjects;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;
import org.logicobjects.core.LogicObject;
import org.logicobjects.core.LogicObjectClass;

//TODO delete ?
public class LogicObjectAdapter extends LogicAdapter<Object, LogicObject> {

	@Override
	public LogicObject adapt(Object source) {
		//return adapt(source, null);
		return null;
	}
	
	public LogicObject adapt(Object source, AdaptationContext adaptingContext) {
		return asLogicObject(source, adaptingContext);
	}

	
	
	public LogicObject asLogicObject(Object object, AdaptationContext adaptingContext) {
		if(object instanceof LogicObject) {
			return (LogicObject)object;
		} /*else if(object instanceof Class) {//create logic object with anonymous logic vars as parameters (useful for invoking only meta methods in the logic side)
			LogicObjectClass logicObjectClass = new LogicObjectClass((Class)object);
			String name = logicObjectClass.getLObjectName();
			List<String> declaredArgs = logicObjectClass.getLObjectArgs();
			int arity = declaredArgs.size();
			if(arity == 0) {
				List<Integer> arities = logicUtil.numberParametersLogtalkObject(name); //assuming that the arities are returned from the lowest to the highest
				if(arities.isEmpty())
					throw new RuntimeException("The logic object " + name + " does not exist in the logic side");
				if(!arities.contains(0)) //it does not exist a Logtalk object with the same name and arity = 0
					arity = arities.get(0); //take the arity of the first logtalk object with the same name
			}
			List arguments = new ArrayList();
			for(int i=0; i<arity; i++)
				arguments.add(Variable.ANONYMOUS_VAR);
			return new LogicObject(name, Arrays.asList(new ObjectToTermAdapter().adaptObjects(arguments, null)));
		} */else {
			return new LogicObject(new ObjectToTermAdapter().adapt(object, adaptingContext));
		}
	}

}
