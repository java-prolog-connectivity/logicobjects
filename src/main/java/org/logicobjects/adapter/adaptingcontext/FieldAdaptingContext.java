package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import org.logicobjects.util.LogicUtil;

public class FieldAdaptingContext extends AccessibleObjectAdaptingContext {
	private Field field;
	
	public FieldAdaptingContext(Field field) {
		this.field = field;
	}
	
	@Override
	public Field getContext() {
		return field;
	}
	
	@Override
	protected Class getContextClass() {
		return field.getType();
	}

	@Override
	public String infereLogicObjectName() {
		return LogicUtil.javaClassNameToProlog(field.getType().getSimpleName());
	}

}
