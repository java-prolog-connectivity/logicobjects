package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import jpl.Atom;
import jpl.Term;
import jpl.Util;

import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.ArrayTypeWrapper;

public class TermToArrayAdapter extends TermToObjectAdapter<Object[]> {

	public Object[] adapt(Term listTerm) {
		return adapt(listTerm, Object[].class);
	}


	public Object[] adapt(Term listTerm, Type type, Field field) {
		ArrayTypeWrapper typeWrapper = new ArrayTypeWrapper(type);
		Type componentType = typeWrapper.getComponentType();
		Term[] termItems= LogicUtil.listToTermArray(listTerm);
		Object array = createArray(componentType, termItems.length);
		for(int i=0; i<termItems.length; i++) {
			Array.set(array, i, new TermToObjectAdapter().adapt(termItems[i], componentType, field));
		}
		return (Object[]) array;
	}


	public static Object createArray(Type type, int length) {
		return Array.newInstance(AbstractTypeWrapper.wrap(type).asClass(), length);
	}
	
	
	

	
	
}
