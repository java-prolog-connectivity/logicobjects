package org.logicobjects.converter;

import java.util.ArrayList;
import java.util.List;

import org.jpc.Jpc;
import org.jpc.mapping.converter.ToTermConverter;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.logicobjects.converter.descriptor.LogicObjectDescriptor;
import org.minitoolbox.reflection.BeansUtil;

public class AnnotatedObjectToTermConverter<T,U extends Term> extends LogicObjectConverter implements ToTermConverter<T,U> {

	public AnnotatedObjectToTermConverter(LogicObjectDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	public U toTerm(T object, Class<U> termClass, Jpc context) {
		String logicObjectName = descriptor.name();
		List<Term> arguments = propertiesAsTerms(object, descriptor.args(), context);
		if(arguments.isEmpty())
			return context.toTerm(logicObjectName, termClass);
		else {
			if(!Compound.class.equals(termClass))
				throw new RuntimeException();
			else {
				return (U) new Compound(logicObjectName, arguments);
			}
		}
	}

	public static List<Term> propertiesAsTerms(Object object, List<String> propertyNames, Jpc context) {
		List<Term> arguments = new ArrayList<Term>();
		for(String propertyName : propertyNames) {
			Term term = propertyAsTerm(object, propertyName, context);
			arguments.add(term);
		}
		return arguments;
	}
	
	public static Term propertyAsTerm(Object lObject, String propertyName, Jpc context) {
		Term propertyAsTerm = null;
		Object propertyValue = BeansUtil.getProperty(lObject, propertyName);
		propertyAsTerm = X;//TODO
		return propertyAsTerm;
	}


}
