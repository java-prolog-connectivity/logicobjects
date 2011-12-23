package org.logicobjects.core;


import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;

import org.logicobjects.contextmanagement.GlobalLContext;
import org.logicobjects.instrumentation.LogicObjectInstrumentation;

public class LogtalkObjectFactory {
/*
	static {
		LogicEngine.prepare();  //TODO delete this, since everything will pass by the logic engine
	}
*/
	
	private static LogtalkObjectFactory factory;
	
	public static LogtalkObjectFactory getDefault() {
		if(factory == null)
			factory = new LogtalkObjectFactory();
		return factory;
	}
	
	private Set<String> scheduledSearchFilters;
	private Set<URL> scheduledSearchUrls;
	
	
	private GlobalLContext context;
	private ClassPool classPool;
	
	private LogtalkObjectFactory() {
		scheduledSearchFilters = new HashSet<String>();
		scheduledSearchUrls = new HashSet<URL>();
		//context = new LContext();
	}
	
	
	
	public ClassPool getClassPool() {
		if(classPool == null)
			classPool = ClassPool.getDefault();
		return classPool;
	}

	public void setClassPool(ClassPool classPool) {
		this.classPool = classPool;
	}



	private void initializeContext() {
/*
		ConfigurationBuilder config = new ConfigurationBuilder();
		
		//ClassLoader cl = (ClassLoader) ((BundleHost)IntensionalViewsPlugin.getDefault().getBundle()).getLoaderProxy().getBundleLoader().createClassLoader();
		//ClassLoader cl = IntensionalViewsPlugin.getDefault().getBundle().getClass().getClassLoader();
		//ClassLoader cl = IntensionalViewsPlugin.getDefault().getClass().getClassLoader();
		//ClassLoader cl = getClass().getClassLoader();
		//System.out.println(cl.getClass().getName());
		//System.out.println(IntensionalViewsPlugin.getDefault().getClass().getClassLoader() instanceof URLClassLoader);
		//System.out.println(cl instanceof URLClassLoader);
		//System.out.println(IntensionalViewsPlugin.getDefault().getBundleContext().getBundle()).getLoaderProxy().getBundleLoader());
		
		
		
		//ClassLoader cl = getClass().getClassLoader();
		//config.addClassLoader(cl);
		
		FilterBuilder fb = new FilterBuilder();
		//fb.includePackage(logicobjects.LogicEngine.class);
		//fb.include(FilterBuilder.prefix("testPackage"));
		fb.include(FilterBuilder.prefix("org.logicobjects"));
		fb.include(FilterBuilder.prefix("iv4e"));
		//fb.include(FilterBuilder.prefix("bin"));
		//fb.include(FilterBuilder.prefix("bin.logicobjects"));
		//fb.include(FilterBuilder.prefix("bin.iv4e"));
		

		
		config.filterInputsBy(fb);
		//config.setUrls(ClasspathHelper.forClassLoader());
		//config.setUrls(ClasspathHelper.forClassLoader(cl));
		//config.addUrls(ClasspathHelper.forClass(getClass(), cl));
		//config.addUrls(ClasspathHelper.forClass(getClass(), cl));
		//config.addUrls(ClasspathHelper.forClass(IntensionalViewsPlugin.class, cl));
		//config.addUrls(ClasspathHelper.forPackage("bin"));
		//config.addUrls(ClasspathHelper.forClass(IntensionalViewsPlugin.class));
		
		
		config.addUrls(ClasspathHelper.forPackage("iv4e"));
		config.addUrls(ClasspathHelper.forPackage("org.logicobjects"));
		//config.addUrls(ClasspathHelper.forPackage("testPackage", TestClass.class.getClassLoader()));
		//config.addUrls(ClasspathHelper.forPackage("testPackage"));


		Reflections system_reflections = new Reflections(config);
		//Reflections system_reflections = new Reflections(new Object[] {"logicobjects"});
		//Reflections system_reflections = new Reflections(new Object[] {"logicobjects", "bin"});

		//Reflections system_reflections = new Reflections(new Object[] {"iv4e", "logicobjects"});
		Set<Class<?>> classes = system_reflections.getTypesAnnotatedWith(LObject.class);
		
		System.out.println("printing logic classes: ");
		for(Class clazz : classes) {
			System.out.println("Logic class: "+clazz.getName());
		}
		*/
		
		
		
		//context = new LContext();
		context = new GlobalLContext();
		for(URL url : scheduledSearchUrls) {
			context.addSearchUrl(url);
		}
		
		for(String packageName : scheduledSearchFilters) {
			context.addSearchFilter(packageName);
		}

	}
	
	public GlobalLContext getContext() {
		if(context == null)
			initializeContext();
		return context;
	}

	public void addSearchFilter(String packageName) {
		if(context == null)
			scheduledSearchFilters.add(packageName);
		else
			context.addSearchFilter(packageName);
	}

	public void addSearchUrl(URL url) {
		if(context == null)
			scheduledSearchUrls.add(url);
		else
			context.addSearchUrl(url);
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
				LogtalkObject.loadDependencies(clazz); //load the dependencies in the Prolog engine
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
