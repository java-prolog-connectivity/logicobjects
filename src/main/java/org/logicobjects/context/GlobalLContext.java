package org.logicobjects.context;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import jpl.Term;
import jpl.Variable;

import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.core.LogicClass;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class GlobalLContext extends AbstractLContext {
	
	SimpleLContext systemContext;
	SimpleLContext userContext;
	
	public GlobalLContext() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.addUrls(ClasspathHelper.forClass(this.getClass()));
		Reflections system_reflections = new Reflections(config);
		systemContext = new SimpleLContext(system_reflections);
		userContext = new SimpleLContext();
	}

	public SimpleLContext getSystemContext() {
		return systemContext;
	}

	public SimpleLContext getUserContext() {
		return userContext;
	}

	public void addSearchFilter(String packageName) {
		userContext.addSearchFilter(packageName);
	}
	
	public void addSearchUrlFromClass(Class clazz) {
		userContext.addSearchUrlFromClass(clazz);
	}
	
	public void addSearchUrl(URL url) {
		userContext.addSearchUrls(url);
	}
	
	public Set<Class<?>> getLogicClasses() {
		Set<Class<?>> setCombination = new HashSet<Class<?>>(); //a new set needs to be defined since the system and user sets are Immutable collections
		setCombination.addAll(systemContext.getLogicClasses());
		if(userContext != null) {
			setCombination.addAll(userContext.getLogicClasses());
		}
		return setCombination;
	}
	
	public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		Set<Class<? extends WrapperAdapter>> setCombination = new HashSet<Class<? extends WrapperAdapter>>(); //a new set needs to be defined since the system and user sets are Immutable collections
		setCombination.addAll(systemContext.getWrapperAdapters());
		if(userContext != null) {
			setCombination.addAll(userContext.getWrapperAdapters());
		}
		return setCombination;
	}
	

	
	public Class findLogicClass(String logicName, int args) {
		Set<Class<?>> set = getLogicClasses();
		for(Class clazz : set) {
			LogicClass logicClass = new LogicClass(clazz);
			if(logicClass.getLogicName().equals(logicName) && logicClass.getParameters().length == args)
				return clazz;
		}
		return null;
	}
	
	public Class findLogicClass(Term term) {
		if( term instanceof Variable || term instanceof jpl.Integer || term instanceof jpl.Float )
			return null;
		return findLogicClass(term.name(), term.args().length);
	}

	public static void main(String[] args) {
		
		Set<Class<?>> allLogicClasses = new GlobalLContext().getLogicClasses();
		for(Class clazz : allLogicClasses) {
			System.out.println(clazz);
		}
		
		Set<Class<? extends WrapperAdapter>> allLogicWrappers = new GlobalLContext().getWrapperAdapters();
		for(Class clazz : allLogicWrappers) {
			System.out.println(clazz);
		}
		
		/*
		//Set<Class<? extends SolutionCompositionAdapter>> wrapperAdapters = new LContext().getAllWrapperAdapters();
		Set<Class<? extends Adapter>> adapters = new LContext().getAllAdapters();
		System.out.println("*** Showing all adapters ***");
		for(Class clazz : adapters) {
			System.out.println(clazz.getName());
		}
		System.out.println("*** Showing all composition adapters ***");
		Set<Class<? extends SolutionCompositionAdapter>> wrapperAdapters = new LContext().getAllWrapperAdapters();
		for(Class clazz : wrapperAdapters) {
			System.out.println(clazz.getName());
		}
		*/
	}
}

