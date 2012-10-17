package org.logicobjects.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.IgnoreLAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.logicobjects.term.Compound;
import org.logicobjects.term.Term;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.reflectiveutils.PackagePropertiesTree;
import org.reflectiveutils.ReflectionUtil;

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
		Set<URL>filteredUrls = fixURLs(new HashSet<URL>(Arrays.<URL>asList(url))); //jboss compatibility hack
		config.addUrls(filteredUrls);
		systemReflections =  new Reflections(config);
		return systemReflections;
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
	
	private <T> Set<Class<? extends T>> filterAbstractClasses(Set<Class<? extends T>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return !ReflectionUtil.isAbstract(clazz);
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	
	private Set<Class<? extends WrapperAdapter>> filterAdapters(Set<Class<? extends Adapter>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return clazz.getAnnotation(IgnoreLAdapter.class) == null && WrapperAdapter.class.isAssignableFrom(clazz) && !ReflectionUtil.isAbstract(clazz);
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	

	//TODO verify that this method is still necessary after doing what is said here: http://code.google.com/p/reflections/wiki/JBossIntegration
	/**
	 * This method is a workaround to the problem that the current version of reflections (at the moment of testing: 0.9.5 ) does not recognize JBoss URLs.
	 * TODO check if next versions of Reflections still have this problem, otherwise this method can be removed.
	 * @param urls
	 * @return
	 */
	private Set<URL> fixURLs(Set<URL> urls) {
        Set<URL> results = new HashSet<URL>(urls.size());
        for (URL url : urls) {
            String cleanURL = url.toString();
            // Fix JBoss URLs
            if (url.getProtocol().startsWith("vfszip")) {
                cleanURL = cleanURL.replaceFirst("vfszip:", "file:");
            } else if (url.getProtocol().startsWith("vfsfile")) {
                cleanURL = cleanURL.replaceFirst("vfsfile:", "file:");
            } else if(url.getProtocol().startsWith("vfs")) {//added by me
                  cleanURL = cleanURL.replaceFirst("vfs:", "file:");
            } 
            cleanURL = cleanURL.replaceFirst("\\.jar/", ".jar!/");
            try {
                results.add(new URL(cleanURL));
            } catch (MalformedURLException ex) {
            	throw new RuntimeException(ex);  // Shouldn't happen, but we can't do more to fix this URL.
            }
        }
        return results;
    }
	
	
	public Class findLogicClass(String logicName, int args) {
		Set<Class<?>> set = getLogicClasses();
		for(Class clazz : set) {
			LogicObjectClass logicClass = new LogicObjectClass(clazz);
			if(logicClass.getLObjectName().equals(logicName) && logicClass.getLObjectArgs().size() == args)
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
			urls = fixURLs(urls); //jboss compatibility hack
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
		Set<URL>filteredUrls = fixURLs(new HashSet<URL>(Arrays.<URL>asList(urls))); //jboss compatibility hack
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
		engineConfigurations = filterAbstractClasses(reflections.getSubTypesOf(LogicEngineConfiguration.class));//remember that getSubTypesOf will work only if all the classes in the hierarchy are in the filtered urls
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
