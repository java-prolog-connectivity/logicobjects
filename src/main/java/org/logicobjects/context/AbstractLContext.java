package org.logicobjects.context;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.IgnoreAdapter;

public abstract class AbstractLContext {

	public abstract Set<Class<?>> getLogicClasses();
	
	public abstract Set<Class<? extends WrapperAdapter>> getWrapperAdapters();
	
	
	protected void filterLogicClasses(Set<Class<?>> unfilteredLogicClasses, Set<Class<?>> filteredClasses) {
		for(Class clazz : unfilteredLogicClasses) {
			if(!clazz.isInterface()) {
				filteredClasses.add(clazz);
			} /*else {
				for(Object _implementor : system_reflections.getSubTypesOf(clazz)) {//TODO discover how i can put a Class in the for
					Class implementor = (Class)_implementor; //this kind of things make me hate java ...
					if(!implementor.isInterface() && ReflectionUtil.includesInterfaceInHierarchy(implementor, clazz))
						filteredClasses.add(implementor);
				}
			} */
		}
	}
	
	protected void filterAdapters(Set<Class<? extends Adapter>> foundAdapters, Set<Class<? extends WrapperAdapter>> wrapperAdapters) {
		for(Class<? extends Adapter> adapterClass : foundAdapters) {
			if(adapterClass.getAnnotation(IgnoreAdapter.class) == null) {
				if(WrapperAdapter.class.isAssignableFrom(adapterClass) && !Modifier.isAbstract(WrapperAdapter.class.getModifiers()))
					wrapperAdapters.add((Class<? extends WrapperAdapter>) adapterClass);
			}
		}
	}
	
}
