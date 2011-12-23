package org.logicobjects.contextmanagement;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.SolutionCompositionAdapter;
import org.logicobjects.annotation.LObject;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.LoggerFactory;

public class SimpleLContext extends AbstractLContext {

	private Set<Class<?>> logicClasses;
	private Set<Class<? extends SolutionCompositionAdapter>> compositionAdapters;

	private Reflections reflections;
	
	public SimpleLContext(Reflections reflections) {
		this.reflections = reflections;
		refresh();
	}
	
	public SimpleLContext() {
	}
	
	public Set<Class<?>> getLogicClasses() {
		if(reflections == null) {
			LoggerFactory.getLogger(SimpleLContext.class).warn("Asking for user logic classes without having provided a filter. Looking for classes in the static and context class loader.");
			addSearchUrls(ClasspathHelper.forClassLoader().toArray(new URL[] {}));
		} 
		return logicClasses;
	}
	
	public Set<Class<? extends SolutionCompositionAdapter>> getCompositionAdapters() {
		if(reflections == null) {
			LoggerFactory.getLogger(SimpleLContext.class).warn("Asking for user composition adapters without having provided a filter. Looking for classes in the static and context class loader.");
			addSearchUrls(ClasspathHelper.forClassLoader().toArray(new URL[] {}));
		} 
		return compositionAdapters;
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
		logicClasses = filterLogicClasses(reflections.getTypesAnnotatedWith(LObject.class));
		Set<Class<? extends Adapter>> unfilteredAdapters = reflections.getSubTypesOf(Adapter.class);
		compositionAdapters = new HashSet<Class<? extends SolutionCompositionAdapter>>();
		updateAdapters(unfilteredAdapters, compositionAdapters);
	}
	
	
}
