package org.logicobjects.core;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.logicobjects.annotation.Ignore;
import org.logicobjects.annotation.LObject;
import org.logicobjects.methodadapter.methodresult.solutioncomposition.WrapperAdapter;
import org.minitoolbox.reflection.ReflectionUtil;
import org.minitoolbox.reflection.googlereflections.GoogleReflectionsUtil;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Predicate;

public class ClassPathContext {

	private final static URL systemUrl = ClasspathHelper.forClass(ClassPathContext.class);
	
	private static URL findCallerClasspath() {
		return org.minitoolbox.reflection.ReflectionUtil.getConsumerLibraryUrl();
	}
	
	private static Reflections getCallerReflections() {
		return getReflections(findCallerClasspath());
	}
	
	public static ClassPathContext forCaller() {
		Reflections reflections = getReflections(systemUrl);
		reflections.merge(getCallerReflections());
		return new ClassPathContext(reflections);
	}
	
	private static Reflections getReflections(URL url) {
		ConfigurationBuilder config = new ConfigurationBuilder();
		Set<URL>filteredUrls = GoogleReflectionsUtil.fixURLs(new HashSet<URL>(Arrays.<URL>asList(url))); //jboss compatibility hack
		config.addUrls(filteredUrls);
		return new Reflections(config);
	}
	
	/**
	 * 
	 * @param unfilteredClasses a set of classes.
	 * @return the set of classes sent as parameter without any class that is in the LogicObjects classpath.
	 */
	private static <T> Set<Class<? extends T>> filterSystemClasses(Set<Class<? extends T>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return !ClasspathHelper.forClass(clazz).equals(systemUrl);
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	
//	private static Set<Class<?>> filterInterfaces(Set<Class<?>> unfilteredClasses) {
//		Predicate predicate = new Predicate<Class>() {
//			  public boolean apply(Class clazz) {
//			    return !clazz.isInterface();
//			  }
//		};
//		return ReflectionUtils.getAll(unfilteredClasses, predicate);
//	}
	
	private static Set<Class<? extends WrapperAdapter>> filterAdapters(Iterable<Class<? extends Adapter>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return clazz.getAnnotation(Ignore.class) == null && WrapperAdapter.class.isAssignableFrom(clazz) && !ReflectionUtil.isAbstract(clazz);
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	
	
	
	
	private final Reflections reflections;
	
	private Set<Class<?>> logicClasses;
	private Set<Class<? extends WrapperAdapter>> compositionAdapters;
	
	public ClassPathContext(Reflections reflections) {
		this.reflections = reflections;
		refresh();
	}
	
	public ClassPathContext() {
		this(getReflections(systemUrl));
	}

	public Set<Class<?>> getLogicClasses() {
		return logicClasses;
	}
	
	public Set<Class<? extends WrapperAdapter>> getCompositionAdapters() {
		return compositionAdapters;
	}
	
	

	
	public void addPackage(String packageName) {
		ConfigurationBuilder config = new ConfigurationBuilder();
		FilterBuilder fb = new FilterBuilder();
		fb.include(FilterBuilder.prefix(packageName));
		config.filterInputsBy(fb);
		Set<URL> urls = ClasspathHelper.forPackage(packageName);
		urls = GoogleReflectionsUtil.fixURLs(urls); //jboss compatibility hack
		config.setUrls(urls);
		Reflections packageReflections = new Reflections(config);
		//packageReflections = new Reflections(packageName);
		reflections.merge(packageReflections);
		refresh();
	} 
	
	public void addUrlFromClass(Class clazz) {
		addUrls(ClasspathHelper.forClass(clazz));
	} 
	
	public void addUrls(URL... urls) {
		Reflections reflections_url;
		ConfigurationBuilder config = new ConfigurationBuilder();
		Set<URL>filteredUrls = GoogleReflectionsUtil.fixURLs(new HashSet<URL>(Arrays.<URL>asList(urls))); //jboss compatibility hack
		config.addUrls(filteredUrls);
		reflections_url =  new Reflections(config);
		reflections.merge(reflections_url);
		refresh();
	} 
	
	private void refresh() {
		logicClasses = reflections.getTypesAnnotatedWith(LObject.class);
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
	
	
	
	public Class<?> findLogicClass(String logicName, int args) {
		Set<Class<?>> set = getLogicClasses();
		for(Class clazz : set) {
			LogicObjectClass logicClass = new LogicObjectClass(clazz);
			if(logicClass.getLObjectName().
					equals(logicName) && logicClass.getLObjectArgs().size() == args)
				return clazz;
		}
		return null;
	}
	
	public Class<?> findLogicClass(Term term) {
		Class logicClass = null;
		if(term instanceof Compound) {
			Compound compound = (Compound) term;
			if(!compound.isList())
				logicClass = findLogicClass(compound.getName(), compound.getArity());
		}
		return logicClass;
	}

}
