package org.logicobjects.context;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.LObject;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.LoggerFactory;

public class SimpleLContext extends AbstractLContext {

	private Set<Class<?>> logicClasses;
	private Set<Class<? extends WrapperAdapter>> compositionAdapters;

	private Reflections reflections;
	
	public SimpleLContext() {
	}
	
	public SimpleLContext(Reflections reflections) {
		this.reflections = reflections;
		refresh();
	}
	
	
	private void loadDefaultSearchUrl(String reason) {
		LoggerFactory.getLogger(SimpleLContext.class).warn("Asking for "+reason+" without having provided a filter.");
		URL url = findCallerClasspath();
		LoggerFactory.getLogger(SimpleLContext.class).warn("Looking for classes in the same classpath than the user of the library: "+url);
		addSearchUrls(url);
		//addSearchUrls(ClasspathHelper.forClassLoader(ClasspathHelper.getStaticClassLoader()).toArray(new URL[] {}));
	}
	
	public Set<Class<?>> getLogicClasses() {
		if(reflections == null) {
			loadDefaultSearchUrl("user logic classes");
		} 
		return logicClasses;
	}
	
	public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		if(reflections == null) {
			loadDefaultSearchUrl("user wrapper adapters");
		} 
		return compositionAdapters;
	}
	
	private URL findCallerClasspath() {
		URL logicObjectsURL = ClasspathHelper.forClass(getClass(), null);
		//The first element in the stack trace is the getStackTrace method, and the second is this method
		//Then we start at the third member
		for(int i = 2; i<Thread.currentThread().getStackTrace().length; i++) {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[i];
			try {
				Class callerClass = Class.forName(stackTraceElement.getClassName());
				URL callerURL = ClasspathHelper.forClass(callerClass, null);
				if(!callerURL.equals(logicObjectsURL))
					return callerURL;
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
	
	public void addSearchFilter(String packageName) {
		if(reflections == null) {
			reflections = new Reflections(packageName);
		} else {
			Reflections newReflections = new Reflections(packageName);
			reflections.merge(newReflections);
		}
		refresh();
	} 
	
	public void addSearchUrlFromClass(Class clazz) {
		addSearchUrls(ClasspathHelper.forClass(clazz));
	} 
	
	public void addSearchUrls(URL... urls) {
		Reflections reflections_url;
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.addUrls(urls);
		reflections_url =  new Reflections(config);
		if(reflections == null) {
			reflections = reflections_url;
		} else {
			reflections.merge(reflections_url);
		}
		refresh();
	} 
	
	//this could be optimized indeed ...
	private void refresh() {
		logicClasses = new HashSet<Class<?>>();
		filterLogicClasses(reflections.getTypesAnnotatedWith(LObject.class), logicClasses);
		Set<Class<? extends Adapter>> unfilteredAdapters = reflections.getSubTypesOf(Adapter.class);
		compositionAdapters = new HashSet<Class<? extends WrapperAdapter>>();
		filterAdapters(unfilteredAdapters, compositionAdapters);
	}
	
	
}
