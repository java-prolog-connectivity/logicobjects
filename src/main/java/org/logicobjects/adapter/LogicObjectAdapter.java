package org.logicobjects.adapter;


import java.util.ArrayList;
import java.util.List;

import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;
import org.logicobjects.core.LogicObjectClass;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.core.LogicObject;

public class LogicObjectAdapter extends Adapter<Object, LogicObject> {

	@Override
	public LogicObject adapt(Object source) {
		return adapt(source, null);
	}
	
	public LogicObject adapt(Object source, AdaptationContext adaptingContext) {
		return asLogicObject(source, adaptingContext);
	}
	/*
	public Term adapt(Object source, AdaptingContext adaptingContext) {
		
	}
	*/
	
	
	public LogicObject asLogicObject(Object object, AdaptationContext adaptingContext) {
		if(object instanceof LogicObject) {
			return (LogicObject)object;
		} else if(object instanceof Class) {//create logic object with anonymous logic vars as parameters (useful for invoking only meta methods in the logic side)
			LogicObjectClass logicObjectClass = new LogicObjectClass((Class)object);
			String name = logicObjectClass.getLObjectName();
			String[] declaredArgs = logicObjectClass.getLObjectArgs();
			int arity = declaredArgs.length;
			if(arity == 0) {
				List<Integer> arities = LogicEngine.getDefault().numberParametersLogtalkObject(name); //assuming that the arities are returned from the lowest to the highest
				if(arities.isEmpty())
					throw new RuntimeException("The logic object " + name + " does not exist in the logic side");
				if(!arities.contains(0)) //it does not exist a Logtalk object with the same name and arity = 0
					arity = arities.get(0); //take the arity of the first logtalk object with the same name
			}
			List arguments = new ArrayList();
			for(int i=0; i<arity; i++)
				arguments.add(LogicEngine.ANONYMOUS_VAR);
			return new LogicObject(name, ArrayToTermAdapter.objectsAsTerms(arguments.toArray()));
		} else {
			return new LogicObject(new ObjectToTermAdapter().adapt(object, adaptingContext));
		}
	}

	
	
	
}
