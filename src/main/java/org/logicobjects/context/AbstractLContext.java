package org.logicobjects.context;

import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import jpl.Term;
import jpl.Variable;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.IgnoreLAdapter;
import org.logicobjects.core.LogicObjectClass;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.reflectiveutils.ReflectionUtil;

public abstract class AbstractLContext {

	//includes a package in the search path
	public abstract void addPackage(String packageName);
	//includes the classpath url of a given class in the search path
	public abstract void addSearchUrlFromClass(Class clazz);
	//includes a url in the search path
	public abstract void addSearchUrls(URL... urls);
	
	public abstract Set<Class<?>> getLogicClasses();
	public abstract Set<Class<? extends WrapperAdapter>> getWrapperAdapters();
	
	
	protected Set<Class<?>> filterInterfaces(Set<Class<?>> unfilteredClasses) {
		Set<Class<?>> filteredClasses = new HashSet<>();
		for(Class clazz : unfilteredClasses) {
			if(!clazz.isInterface()) {
				filteredClasses.add(clazz);
			}
		}
		return filteredClasses;
	}
	
	protected <T> Set<Class<? extends T>> filterAbstractClasses(Set<Class<? extends T>> unfilteredClasses) {
		Set<Class<? extends T>> filteredClasses = new HashSet<>();
		for(Class clazz : unfilteredClasses) {
			if(!ReflectionUtil.isAbstract(clazz)) {
				filteredClasses.add(clazz);
			}
		}
		return filteredClasses;
	}
	
	protected Set<Class<? extends WrapperAdapter>> filterAdapters(Set<Class<? extends Adapter>> foundAdapters) {
		Set<Class<? extends WrapperAdapter>> wrapperAdapters = new HashSet<>();
		for(Class<? extends Adapter> adapterClass : foundAdapters) {
			if(adapterClass.getAnnotation(IgnoreLAdapter.class) == null) {
				if(WrapperAdapter.class.isAssignableFrom(adapterClass) && !Modifier.isAbstract(adapterClass.getModifiers()))
					wrapperAdapters.add((Class<? extends WrapperAdapter>) adapterClass);
			}
		}
		return wrapperAdapters;
	}
	
	
	

	/**
	 * This method is a workaround to the problem that the current version of reflections (at the moment of testing: 0.9.5 ) does not recognize JBoss URLs.
	 * TODO check if next versions of Reflections still have this problem, otherwise this method can be removed.
	 * @param urls
	 * @return
	 */
	protected Set<URL> fixURLs(Set<URL> urls) {
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
			if(logicClass.getLObjectName().equals(logicName) && logicClass.getLObjectArgs().length == args)
				return clazz;
		}
		return null;
	}
	
	public Class findLogicClass(Term term) {
		if( term instanceof Variable || term instanceof jpl.Integer || term instanceof jpl.Float || term.name().equals(".") )
			return null;
		return findLogicClass(term.name(), term.args().length);
	}
	
	public abstract LogicEngineConfiguration getLogicEngineConfiguration(Class clazz);

}
