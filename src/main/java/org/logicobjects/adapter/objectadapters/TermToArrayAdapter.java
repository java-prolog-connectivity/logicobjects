package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import org.jpc.term.AbstractTerm;
import org.jpc.util.PrologUtil;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;
import org.minitoolbox.reflection.ReflectionUtil;
import org.minitoolbox.reflection.typewrapper.ArrayTypeWrapper;

public class TermToArrayAdapter extends TermToObjectAdapter<Object[]> {

	public Object[] adapt(AbstractTerm listTerm) {
		return adapt(listTerm, Object[].class);
	}


	public Object[] adapt(AbstractTerm listTerm, Type type, AdaptationContext adaptingContext) {
		ArrayTypeWrapper typeWrapper = new ArrayTypeWrapper(type);
		Type componentType = typeWrapper.getComponentType();
		List<AbstractTerm> termItems= PrologUtil.listToTerms(listTerm);
		Object array = ReflectionUtil.createArray(componentType, termItems.size());
		for(int i=0; i<termItems.size(); i++) {
			Array.set(array, i, new TermToObjectAdapter().adapt(termItems.get(i), componentType, adaptingContext));
		}
		return (Object[]) array;
	}



	

	
	
}
