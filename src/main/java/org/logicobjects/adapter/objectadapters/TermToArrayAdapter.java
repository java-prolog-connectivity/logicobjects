package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import org.jpc.LogicUtil;
import org.jpc.term.Term;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.ArrayTypeWrapper;

public class TermToArrayAdapter extends TermToObjectAdapter<Object[]> {

	public Object[] adapt(Term listTerm) {
		return adapt(listTerm, Object[].class);
	}


	public Object[] adapt(Term listTerm, Type type, AdaptationContext adaptingContext) {
		ArrayTypeWrapper typeWrapper = new ArrayTypeWrapper(type);
		Type componentType = typeWrapper.getComponentType();
		List<Term> termItems= LogicUtil.listToTerms(listTerm);
		Object array = ReflectionUtil.createArray(componentType, termItems.size());
		for(int i=0; i<termItems.size(); i++) {
			Array.set(array, i, new TermToObjectAdapter().adapt(termItems.get(i), componentType, adaptingContext));
		}
		return (Object[]) array;
	}



	

	
	
}
