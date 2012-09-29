package org.logicobjects.core;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.ClassPool;

import org.logicobjects.context.AbstractLContext;
import org.logicobjects.context.GlobalLContext;
import org.logicobjects.instrumentation.LogicObjectInstrumentation;
import org.reflections.util.ClasspathHelper;
import org.reflectiveutils.BeansUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicObjectFactory {
	
	private static Logger logger = LoggerFactory.getLogger(LogicObjectFactory.class);
	
	private static LogicObjectFactory factory;
	
	private ResourceManager resourceManager;
	private LogicDependenciesLoader logicDependenciesLoader;
	
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
		String tmpDir = LogicEngine.getDefault().getPreferences().getTmpDirectory();
		resourceManager = new ResourceManager(tmpDir);
		logicDependenciesLoader = new LogicDependenciesLoader();
	}

	public ResourceManager getResourceManager(){
		return resourceManager;
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
		return create(null, clazz, params);
	}
	
	public <T> T create(Object declaringObject, Class<T> clazz, Object... params) {
		if(declaringObject != null && declaringObject instanceof Class)
			throw new RuntimeException("The context object cannot be an instance of " + Class.class.getName());
		Class instantiatingClass = null;
		//if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			LogicObjectInstrumentation instrumentation = new LogicObjectInstrumentation(clazz, getClassPool());
			//instrumentation.run(); //instrument class and its ancestors
			boolean extendingClassLoaded = instrumentation.isExtendingClassLoaded();
			if(!extendingClassLoaded) { //the extending class has not been generated yet
				long startTime = System.nanoTime();
				verifyClass(clazz);
				resourceManager.process(ClasspathHelper.forClass(clazz));
				logicDependenciesLoader.loadDependencies(clazz); //load the dependencies in the Prolog engine
				long endTimeDependencies = System.nanoTime();
				instantiatingClass = instrumentation.getExtendingClass(); //answers the extending class. Generates it if needed.
				long endTimeInstrumentation = System.nanoTime();
				long timeLoadingDependencies = (endTimeDependencies - startTime)/1000000;
				long timeInstrumentingClass =  (endTimeInstrumentation - endTimeDependencies)/1000000;
				logger.info("Extending class" + clazz.getSimpleName() + " with generated class " + instantiatingClass.getSimpleName());
				logger.info("Loading dependencies time: " + timeLoadingDependencies + " ms. Instrumentation time: " + timeInstrumentingClass + " ms.");
			} else //the extending class has already been generated
				instantiatingClass = instrumentation.getExtendingClass(); 
		//} else
			//instantiatingClass = clazz;
		

		try {
			List<Object> logicObjectsParamsConstructor = Arrays.asList(params);
			List<Class> logicObjectsParamsClasses = objectsClasses(Arrays.asList(params));
			
			List<Object> allParamsConstructor = new ArrayList(logicObjectsParamsConstructor); //the surrounding new ArrayList is to make the original list mutable
			List<Class> allParamsClasses = new ArrayList(logicObjectsParamsClasses);
			
			if(declaringObject != null) {
				allParamsConstructor.add(0, declaringObject);
				allParamsClasses.add(0, declaringObject.getClass());
			}
			Constructor constructorWithParams = null;
			try {
				constructorWithParams = instantiatingClass.getConstructor(allParamsClasses.toArray(new Class[]{}));
			} catch(NoSuchMethodException e) {
				//the constructor with all the parameters does not exist
			}
			T logicClassInstance;
			
			if(constructorWithParams != null) {
				logicClassInstance = (T)constructorWithParams.newInstance(allParamsConstructor.toArray());
			} else {
				if(declaringObject != null) {
					Constructor declaringObjectConstructor = instantiatingClass.getConstructor(new Class[]{Object.class});
					logicClassInstance = (T)declaringObjectConstructor.newInstance(new Object[]{declaringObjectConstructor});
				} else
					logicClassInstance = (T)instantiatingClass.newInstance();
				
				BeansUtil.setProperties(logicClassInstance, LogicObjectClass.findLogicObjectClass(instantiatingClass).getLObjectArgs(), logicObjectsParamsConstructor.toArray());

				/*
				if(LogicObjectClass.hasNoArgsConstructor(clazz)) {
					Object o = instantiatingClass.newInstance();
				} else if(LogicObjectClass.hasConstructorWithArgsNumber(clazz, paramsConstructor.size())) {
					Constructor constructor = clazz.getConstructor(paramsClasses.toArray(new Class[]{}));
				} else if((LogicObjectClass.hasConstructorWithOneVarArgs(clazz))) {
					//TODO
				}
				*/
				
			}
			return logicClassInstance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private List<Class> objectsClasses(List objects) {
		List<Class> classes = new ArrayList<Class>();
		for(Object o: objects)
			classes.add(o.getClass());
		return classes;
	}
	/*
	private Class[] objectsClasses(Object[] objects) {
		Class[] classes = new Class[objects.length];
		for(int i=0; i<objects.length; i++)
			classes[i] = objects[i].getClass();
		return classes;
	}*/
	
	
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

