package org.logicobjects.core;


import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;

import org.logicobjects.context.GlobalLContext;
import org.logicobjects.instrumentation.LogicObjectInstrumentation;

public class LogicObjectFactory {
	
	private static LogicObjectFactory factory;
	
	public static LogicObjectFactory getDefault() {
		if(factory == null)
			factory = new LogicObjectFactory();
		return factory;
	}

	
	private GlobalLContext context;
	private ClassPool classPool;
	
	/**
	 * This class should not be directly instantiated
	 */
	private LogicObjectFactory() {
	}
	
	
	
	public ClassPool getClassPool() {
		if(classPool == null)
			classPool = ClassPool.getDefault();
		return classPool;
	}

	public void setClassPool(ClassPool classPool) {
		this.classPool = classPool;
	}

	public GlobalLContext getContext() {
		if(context == null) {
			context = new GlobalLContext();
		}
		return context;
	}

	public void addSearchFilter(String packageName) {
		getContext().addSearchFilter(packageName);
	}

	public void addSearchUrl(URL url) {
		getContext().addSearchUrl(url);
	}

	/*
	public <T> T create(Class<T> c, Term term) {
		return (T) new TermToObjectAdapter().adapt(term, c);
	}
	*/
	
	public <T> T create(Class<T> clazz) {
		Class instantiatingClass = null;
		if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			LogicObjectInstrumentation instrumentation = new LogicObjectInstrumentation(clazz, getClassPool());
			//instrumentation.run(); //instrument class and its ancestors
			if(!instrumentation.isExtendingClassLoaded())
				LogicClass.loadDependencies(clazz); //load the dependencies in the Prolog engine
			instantiatingClass = instrumentation.getExtendingClass(); //create an extending class
		} else
			instantiatingClass = clazz;
		

		try {
			return (T)instantiatingClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	

}
