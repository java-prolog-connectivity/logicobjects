package org.logicobjects.context;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.reflectiveutils.PackagePropertiesTree;
import org.reflectiveutils.ReflectionUtil;
import org.slf4j.LoggerFactory;

public abstract class SimpleLContext extends AbstractLContext {

	private Set<Class<?>> logicClasses;
	private Set<Class<? extends WrapperAdapter>> compositionAdapters;
	protected Reflections reflections;
	

	
	public SimpleLContext() {
	}
	
	public SimpleLContext(Reflections reflections) {
		setReflections(reflections);
	}
	
	public void setReflections(Reflections reflections) {
		this.reflections = reflections;
		refresh();
	}
/*
	protected void setEngineConfigurations(Set<Class<? extends LogicEngineConfiguration>> engineConfigurations) {
		this.engineConfigurations = engineConfigurations;
	}
*/
	private void loadDefaultSearchUrlWithWarning(String reason) {
		LoggerFactory.getLogger(SimpleLContext.class).warn("Asking for "+reason+" without having provided a filter url.");
		URL url = findCallerClasspath();
		LoggerFactory.getLogger(SimpleLContext.class).warn("Looking for classes in the same classpath than the user of the library: "+url);
		addSearchUrls(url);
	}
	
	@Override
	public Set<Class<?>> getLogicClasses() {
		if(reflections == null) {
			loadDefaultSearchUrlWithWarning("user logic classes");
		} 
		return logicClasses;
	}
	
	@Override
	public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		if(reflections == null) {
			loadDefaultSearchUrlWithWarning("user wrapper adapters");
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
				//for generated classes the callerURL will be null.
				if(callerURL != null && !callerURL.equals(logicObjectsURL))
					return callerURL;
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
	@Override
	public void addPackage(String packageName) {
		if(reflections == null) {
			//reflections = new Reflections(packageName);
			ConfigurationBuilder config = new ConfigurationBuilder();
			FilterBuilder fb = new FilterBuilder();
			fb.include(FilterBuilder.prefix(packageName));
			config.filterInputsBy(fb);
			Set<URL> urls = ClasspathHelper.forPackage(packageName);
			urls = fixURLs(urls); //jboss compatibility hack
			config.setUrls(urls);
			reflections = new Reflections(config);
		} else {
			Reflections newReflections = new Reflections(packageName);
			reflections.merge(newReflections);
		}
		refresh();
	} 
	
	@Override
	public void addSearchUrlFromClass(Class clazz) {
		addSearchUrls(ClasspathHelper.forClass(clazz));
	} 
	
	@Override
	public void addSearchUrls(URL... urls) {
		Reflections reflections_url;
		ConfigurationBuilder config = new ConfigurationBuilder();
		//TODO new ArrayList().;
		
		Set<URL>filteredUrls = fixURLs(new HashSet<URL>(Arrays.<URL>asList(urls))); //jboss compatibility hack
		//System.out.println("************************************* FILTERED URLSs");
		//System.out.println(filteredUrls);
		config.addUrls(filteredUrls);
		reflections_url =  new Reflections(config);
		if(reflections == null) {
			reflections = reflections_url;
		} else {
			reflections.merge(reflections_url);
		}
		refresh();
	} 
	
	//TODO this could be optimized using the reflections API. In the current version the filtering of classes is in two steps...
	protected void refresh() {
		logicClasses = filterInterfaces(reflections.getTypesAnnotatedWith(LObject.class));
		compositionAdapters = filterAdapters(reflections.getSubTypesOf(Adapter.class));
	}

	
}
