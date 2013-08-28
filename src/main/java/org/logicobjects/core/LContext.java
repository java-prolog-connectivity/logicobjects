package org.logicobjects.core;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.logicobjects.annotation.IgnoreLAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.methodadapter.methodresult.solutioncomposition.WrapperAdapter;
import org.minitoolbox.PackagePropertiesTree;
import org.minitoolbox.reflection.ReflectionUtil;
import org.minitoolbox.reflection.googlereflections.GoogleReflectionsUtil;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Predicate;

public class LContext {

	private Set<Class<?>> logicClasses;
	private Set<Class<? extends WrapperAdapter>> compositionAdapters;
	private Reflections reflections;
	private Set<Class<? extends LogicEngineConfiguration>> engineConfigurations;
	private PackagePropertiesTree packagePropertiesTree;
	private Map<Class, LogicEngineConfiguration> engineConfigurationPool;
	private URL systemUrl;
	
	public LContext(Reflections reflections) {
		engineConfigurationPool = new HashMap<>();
		systemUrl = ClasspathHelper.forClass(this.getClass());
		this.reflections = reflections;
		refresh();
	}
	
	public LContext(boolean addCallerUrl) {
		engineConfigurationPool = new HashMap<>();
		systemUrl = ClasspathHelper.forClass(this.getClass());
		reflections = getSystemReflections();
		if(addCallerUrl)
			reflections.merge(getCallerReflections());
		refresh();
	}
	
	private Reflections getSystemReflections() {
		return getReflections(systemUrl);
	}
	
	private Reflections getCallerReflections() {
		return getReflections(findCallerClasspath());
	}
	
	private Reflections getReflections(URL url) {
		Reflections systemReflections;
		ConfigurationBuilder config = new ConfigurationBuilder();
		Set<URL>filteredUrls = GoogleReflectionsUtil.fixURLs(new HashSet<URL>(Arrays.<URL>asList(url))); //jboss compatibility hack
		config.addUrls(filteredUrls);
		systemReflections =  new Reflections(config);
		return systemReflections;
	}
	
	private URL findCallerClasspath() {
		return org.minitoolbox.reflection.ReflectionUtil.getConsumerLibraryUrl();
	}
	
	public Set<Class<?>> getLogicClasses() {
		return logicClasses;
	}
	
	public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		return compositionAdapters;
	}
	
	public LogicEngineConfiguration getLogicEngineConfiguration(String packageName) {
		return (LogicEngineConfiguration) packagePropertiesTree.findProperty(packageName, LogicEngineConfiguration.class);
	}
	
	private <T> Set<Class<? extends T>> filterSystemClasses(Set<Class<? extends T>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return !ClasspathHelper.forClass(clazz).equals(systemUrl);
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	
	private Set<Class<?>> filterInterfaces(Set<Class<?>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return !clazz.isInterface();
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	
	private Set<Class<? extends WrapperAdapter>> filterAdapters(Iterable<Class<? extends Adapter>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return clazz.getAnnotation(IgnoreLAdapter.class) == null && WrapperAdapter.class.isAssignableFrom(clazz) && !ReflectionUtil.isAbstract(clazz);
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	

	public Class findLogicClass(Term logicName, int args) {
		Set<Class<?>> set = getLogicClasses();
		for(Class clazz : set) {
			LogicObjectClass logicClass = new LogicObjectClass(clazz);
			if(logicClass.getLObjectName(). TODO CONVERT THIS TO TERM
					equals(logicName) && logicClass.getLObjectArgs().size() == args)
				return clazz;
		}
		return null;
	}
	
	public Class findLogicClass(Term term) {
		Class logicClass = null;
		if(term instanceof Compound) {
			Compound compound = (Compound) term;
			if(compound.name().equals("."))
				logicClass = findLogicClass(compound.name(), compound.arity());
		}
		return logicClass;
	}
	
	public void addPackage(String packageName) {
		if(reflections == null) {
			//reflections = new Reflections(packageName);
			ConfigurationBuilder config = new ConfigurationBuilder();
			FilterBuilder fb = new FilterBuilder();
			fb.include(FilterBuilder.prefix(packageName));
			config.filterInputsBy(fb);
			Set<URL> urls = ClasspathHelper.forPackage(packageName);
			urls = GoogleReflectionsUtil.fixURLs(urls); //jboss compatibility hack
			config.setUrls(urls);
			reflections = new Reflections(config);
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
		Set<URL>filteredUrls = GoogleReflectionsUtil.fixURLs(new HashSet<URL>(Arrays.<URL>asList(urls))); //jboss compatibility hack
		config.addUrls(filteredUrls);
		reflections_url =  new Reflections(config);
		if(reflections == null) {
			reflections = reflections_url;
		} else {
			reflections.merge(reflections_url);
		}
		refresh();
	} 
	
	private void refresh() {
		logicClasses = filterInterfaces(reflections.getTypesAnnotatedWith(LObject.class));
		compositionAdapters = filterAdapters(reflections.getSubTypesOf(Adapter.class));
		engineConfigurations = ReflectionUtil.filterAbstractClasses(reflections.getSubTypesOf(LogicEngineConfiguration.class));//remember that getSubTypesOf will work only if all the classes in the hierarchy are in the filtered urls
		engineConfigurations = filterSystemClasses(engineConfigurations); //this check can be deleted if LogicObjects does not include any non-abstract logic engine configuration
		packagePropertiesTree = new PackagePropertiesTree();
		for(Class<? extends LogicEngineConfiguration> clazz : engineConfigurations) {
			if(!ReflectionUtil.isAbstract(clazz)) {
				LogicEngineConfiguration logicEngineConfiguration = getLogicEngineConfigInstance(clazz);
				if(logicEngineConfiguration.isEnabled()) {
					for(String packageName : logicEngineConfiguration.getScope()) {
						packagePropertiesTree.addProperty(packageName, LogicEngineConfiguration.class, logicEngineConfiguration, false);
					}
				}
			}
		}
	}
	
	private <T extends LogicEngineConfiguration>LogicEngineConfiguration getLogicEngineConfigInstance(Class<T> clazz) {
		LogicEngineConfiguration engineConfig = engineConfigurationPool.get(clazz);
		if(engineConfig == null) {
			try {
				engineConfig = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			engineConfigurationPool.put(clazz, engineConfig);
		}
		return engineConfig;
	}

}
