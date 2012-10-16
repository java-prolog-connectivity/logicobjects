package org.logicobjects.context;

import java.net.URL;
import java.util.Set;

import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.reflections.util.ClasspathHelper;
import org.reflectiveutils.PackagePropertiesTree;
import org.reflectiveutils.ReflectionUtil;
import org.slf4j.LoggerFactory;

public class UserLContext extends SimpleLContext {

	private Set<Class<? extends LogicEngineConfiguration>> engineConfigurations;
	private PackagePropertiesTree packagePropertiesTree;
	
	@Override
	protected void refresh() {
		super.refresh();
		
		engineConfigurations = filterAbstractClasses(reflections.getSubTypesOf(LogicEngineConfiguration.class));
		packagePropertiesTree = new PackagePropertiesTree();
		for(Class<? extends LogicEngineConfiguration> clazz : engineConfigurations) {
			if(!ReflectionUtil.isAbstract(clazz)) {
				try {
					LogicEngineConfiguration logicEngineConfiguration = clazz.newInstance();
					if(logicEngineConfiguration.isEnabled()) {
						
						for(String packageName : logicEngineConfiguration.getScope()) {
							packagePropertiesTree.addProperty(packageName, LogicEngineConfiguration.class, logicEngineConfiguration, false);
						}
					}
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public LogicEngineConfiguration getLogicEngineConfiguration(String packageName) {
		if(packagePropertiesTree == null)
			loadDefaultSearchUrl();
		return (LogicEngineConfiguration) packagePropertiesTree.findProperty(packageName, LogicEngineConfiguration.class);
	}
	
	@Override
	protected void loadDefaultSearchUrl() {
		URL url = findCallerClasspath();
		LoggerFactory.getLogger(UserLContext.class).warn("No classpath search URL defined. Looking in the same classpath URL than the user of the library: "+url);
		addSearchUrls(url);
		
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
	
}
