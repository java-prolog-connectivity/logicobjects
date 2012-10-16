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

	
	protected abstract void loadDefaultSearchUrl();
	
	@Override
	public Set<Class<?>> getLogicClasses() {
		if(reflections == null) {
			loadDefaultSearchUrl();
		} 
		return logicClasses;
	}
	
	@Override
	public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		if(reflections == null) {
			loadDefaultSearchUrl();
		} 
		return compositionAdapters;
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
