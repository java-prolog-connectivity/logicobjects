package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import jpl.Term;

import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptingContext;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.ArrayTypeWrapper;

public class TermToArrayAdapter extends TermToObjectAdapter<Object[]> {

	public Object[] adapt(Term listTerm) {
		return adapt(listTerm, Object[].class);
	}


	public Object[] adapt(Term listTerm, Type type, AdaptingContext adaptingContext) {
		ArrayTypeWrapper typeWrapper = new ArrayTypeWrapper(type);
		Type componentType = typeWrapper.getComponentType();
		Term[] termItems= LogicUtil.listToTermArray(listTerm);
		Object array = createArray(componentType, termItems.length);
		for(int i=0; i<termItems.length; i++) {
			Array.set(array, i, new TermToObjectAdapter().adapt(termItems[i], componentType, adaptingContext));
		}
		return (Object[]) array;
	}


	public static Object createArray(Type type, int length) {
		return Array.newInstance(AbstractTypeWrapper.wrap(type).asClass(), length);
	}
	
	
	

	
	
}
