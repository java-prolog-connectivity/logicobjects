package org.logicobjects.converter;

import java.lang.reflect.Type;
import java.util.List;

import org.jpc.Jpc;
import org.jpc.converter.FromTermConverter;
import org.jpc.term.Term;
import org.logicobjects.LogicObjects;
import org.logicobjects.converter.descriptor.LogicObjectDescriptor;

public class TermToAnnotatedObjectConverter<T extends Term,U> extends LogicObjectConverter implements FromTermConverter<T,U> {

	public TermToAnnotatedObjectConverter(LogicObjectDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	public U fromTerm(T term, Type targetType, Jpc context) {
		LogicObjects logicObjectsContext = (LogicObjects) context;
		List<?> params = getParams(term, descriptor, context);
		logicObjectsContext.getLogicObjectFactory().create((Class<?>)targetType, params);
	}

	private List<?> getParams(T term, LogicObjectDescriptor descriptor, LogicObjects context) {
		// TODO Auto-generated method stub
		return null;
	}



}
