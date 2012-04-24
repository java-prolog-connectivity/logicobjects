package org.logicobjects.context;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class SystemLContext extends SimpleLContext {
	
	public SystemLContext() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		Set<URL> urls = new HashSet<URL>(Arrays.<URL>asList(ClasspathHelper.forClass(this.getClass())));
		urls = filterURLs(urls); //jboss compatibility hack
		config.addUrls(urls);
		Reflections system_reflections = new Reflections(config);
		setReflections(system_reflections);
	}

}
