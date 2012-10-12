package org.logicobjects.context;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jpl.Term;
import jpl.Variable;

import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.core.LogicObjectClass;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class GlobalLContext extends AbstractLContext {
	
	SimpleLContext systemContext;
	SimpleLContext userContext;
	
	public GlobalLContext() {
		systemContext = new SystemLContext();
		userContext = new UserLContext();
	}

	public SimpleLContext getSystemContext() {
		return systemContext;
	}

	public SimpleLContext getUserContext() {
		return userContext;
	}

	@Override
	public void addPackage(String packageName) {
		userContext.addPackage(packageName);
	}
	
	@Override
	public void addSearchUrlFromClass(Class clazz) {
		userContext.addSearchUrlFromClass(clazz);
	}
	
	@Override
	public void addSearchUrls(URL ...urls) {
		userContext.addSearchUrls(urls);
	}
	
	@Override
	public Set<Class<?>> getLogicClasses() {
		Set<Class<?>> setCombination = new HashSet<Class<?>>(); //a new set needs to be defined since the system and user sets are Immutable collections
		setCombination.addAll(systemContext.getLogicClasses());
		if(userContext != null) {
			setCombination.addAll(userContext.getLogicClasses());
		}
		return setCombination;
	}
	
	@Override
	public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		Set<Class<? extends WrapperAdapter>> setCombination = new HashSet<Class<? extends WrapperAdapter>>(); //a new set needs to be defined since the system and user sets are Immutable collections
		setCombination.addAll(systemContext.getWrapperAdapters());
		if(userContext != null) {
			setCombination.addAll(userContext.getWrapperAdapters());
		}
		return setCombination;
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

	@Override
	public LogicEngineConfiguration getLogicEngineConfiguration(Class clazz) {
		return userContext.getLogicEngineConfiguration(clazz);
	}

}

