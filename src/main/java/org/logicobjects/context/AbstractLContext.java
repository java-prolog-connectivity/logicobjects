package org.logicobjects.context;

import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.logicobjects.adapter.Adapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.IgnoreLAdapter;
import org.logicobjects.core.LogicObjectClass;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.logicobjects.term.Compound;
import org.logicobjects.term.Term;
import org.reflections.ReflectionUtils;
import org.reflectiveutils.ReflectionUtil;

import com.google.common.base.Predicate;

public abstract class AbstractLContext {

	//includes a package in the search path
	public abstract void addPackage(String packageName);
	//includes the classpath url of a given class in the search path
	public abstract void addSearchUrlFromClass(Class clazz);
	//includes a url in the search path
	public abstract void addSearchUrls(URL... urls);
	
	protected Set<Class<?>> filterInterfaces(Set<Class<?>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return !clazz.isInterface();
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	
	protected <T> Set<Class<? extends T>> filterAbstractClasses(Set<Class<? extends T>> unfilteredClasses) {
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return !ReflectionUtil.isAbstract(clazz);
			  }
		};
		return ReflectionUtils.getAll(unfilteredClasses, predicate);
	}
	
	protected Set<Class<? extends WrapperAdapter>> filterAdapters(Set<Class<? extends Adapter>> unfilteredClasses) {
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
	
	public abstract Set<Class<?>> getLogicClasses();
	public abstract Set<Class<? extends WrapperAdapter>> getWrapperAdapters();
	public abstract LogicEngineConfiguration getLogicEngineConfiguration(String packageName);

}
