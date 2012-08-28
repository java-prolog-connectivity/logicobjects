package org.logicobjects.core;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;

import javassist.ClassPool;

import org.logicobjects.context.AbstractLContext;
import org.logicobjects.context.GlobalLContext;
import org.logicobjects.instrumentation.LogicObjectInstrumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicObjectFactory {
	
	private static Logger logger = LoggerFactory.getLogger(LogicObjectFactory.class);
	
	private static LogicObjectFactory factory;
	
	public static LogicObjectFactory getDefault() {
		if(factory == null)
			factory = new LogicObjectFactory();
		return factory;
	}

	
	private AbstractLContext context;
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

	public AbstractLContext getContext() {
		if(context == null) {
			context = new GlobalLContext();
		}
		return context;
	}

	public void setContext(AbstractLContext context) {
		this.context = context;
	}
	
	public void addSearchFilter(String packageName) {
		getContext().addSearchFilter(packageName);
	}

	public void addSearchUrl(URL url) {
		getContext().addSearchUrls(url);
	}

	/*
	public <T> T create(Class<T> c, Term term) {
		return (T) new TermToObjectAdapter().adapt(term, c);
	}
	*/

	public <T> T create(Class<T> clazz, Object... params) {
		verifyClass(clazz);
		Class instantiatingClass = null;
		if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			LogicObjectInstrumentation instrumentation = new LogicObjectInstrumentation(clazz, getClassPool());
			//instrumentation.run(); //instrument class and its ancestors
			boolean extendingClassLoaded = instrumentation.isExtendingClassLoaded();
			if(!extendingClassLoaded) { //the extending class has not been generated yet
				long startTime = System.nanoTime();
				LogicObjectClass.loadDependencies(clazz); //load the dependencies in the Prolog engine
				long endTimeDependencies = System.nanoTime();
				instantiatingClass = instrumentation.getExtendingClass(); //answers the extending class. Generates it if needed.
				long endTimeInstrumentation = System.nanoTime();
				long timeLoadingDependencies = (endTimeDependencies - startTime)/1000000;
				long timeInstrumentingClass =  (endTimeInstrumentation - endTimeDependencies)/1000000;
				logger.info("Extending class" + clazz.getSimpleName() + " with generated class " + instantiatingClass.getSimpleName());
				logger.info("Loading dependencies time: " + timeLoadingDependencies + " ms. Instrumentation time: " + timeInstrumentingClass + " ms.");
			} else //the extending class has already been generated
				instantiatingClass = instrumentation.getExtendingClass(); 
		} else
			instantiatingClass = clazz;
		

		try {
			if(params.length == 0) {
				return (T)instantiatingClass.newInstance();
			} else {
				
				if(LogicObjectClass.hasNoArgsConstructor(clazz)) {
					Object o = instantiatingClass.newInstance();
				} else if(LogicObjectClass.hasConstructorWithArgsNumber(clazz, params.length)) {
					Constructor constructor = clazz.getConstructor(objectsClasses(params));
				} else if((LogicObjectClass.hasConstructorWithOneVarArgs(clazz))) {
					
				}
				
				Constructor constructor = instantiatingClass.getConstructor(objectsClasses(params));  //NoSuchMethodException
				return (T)constructor.newInstance(params);
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private Class[] objectsClasses(Object[] objects) {
		Class[] classes = new Class[objects.length];
		for(int i=0; i<objects.length; i++)
			classes[i] = objects[i].getClass();
		return classes;
	}
	
	
	public LogicMetaObject createLogicMetaObject(Class clazz) {
		LogicMetaObject metaObject = null;
		return metaObject;
	}
	
	/**
	 * Verifies that a class is well formed and its logic methods can be instrumented
	 * @param clazz
	 */
	public void verifyClass(Class clazz) {
		//TODO
		/*
		 * 1) It should have either the implicit no args constructor, an explicit non-args, or a constructor with the same number of arguments as the properties of the logic object
		 */
	}
}

