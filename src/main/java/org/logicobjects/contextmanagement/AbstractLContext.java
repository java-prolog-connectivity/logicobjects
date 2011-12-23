package org.logicobjects.contextmanagement;

import java.util.HashSet;
import java.util.Set;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;
import org.logicobjects.annotation.IgnoreAdapter;

public abstract class AbstractLContext {

	public abstract Set<Class<?>> getLogicClasses();
	
	public abstract Set<Class<? extends SolutionCompositionAdapter>> getCompositionAdapters();
	
	
	protected Set<Class<?>> filterLogicClasses(Set<Class<?>> unfilteredLogicClasses) {
		Set<Class<?>> filteredClasses = new HashSet<Class<?>>();
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
		return filteredClasses;
	}
	
	protected void updateAdapters(Set<Class<? extends Adapter>> foundAdapters, Set<Class<? extends SolutionCompositionAdapter>> compositionAdapters) {
		for(Class<? extends Adapter> adapterClass : foundAdapters) {
			if(adapterClass.getAnnotation(IgnoreAdapter.class) == null) {
				if(!adapterClass.equals(SolutionCompositionAdapter.class) && SolutionCompositionAdapter.class.isAssignableFrom(adapterClass))
					compositionAdapters.add((Class<? extends SolutionCompositionAdapter>) adapterClass);
			}
		}
	}
	
}
