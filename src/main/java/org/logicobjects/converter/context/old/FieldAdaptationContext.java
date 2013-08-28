package org.logicobjects.converter.context.old;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import org.jpc.util.PrologUtil;

public class FieldAdaptationContext extends AnnotatedSingleElementAdaptationContext {
	private Field field;
	
	public FieldAdaptationContext(Field field) {
		this.field = field;
	}
	
	@Override
	public Field getContext() {
		return field;
	}
	
	@Override
	public Class getContextClass() {
		return field.getType();
	}


}
