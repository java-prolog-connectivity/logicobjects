package org.logicobjects.test;

import java.util.Set;

import org.junit.Test;
import org.logicobjects.LogicObjects;
import org.logicobjects.annotation.LObject;
import org.logicobjects.converter.Adapter;
import org.logicobjects.util.LogicObjectsSubTypesScanner;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Predicate;
//TODO delete
//create this test just to understand when a subclass is found
public class TestReflections2 {
/*
	@Test
	public void testSubtypes() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		
		//SubTypesScanner scanner = new SubTypesScanner();
		SubTypesScanner scanner = new LogicObjectsSubTypesScanner();
		config.setScanners(scanner);
		//config.addUrls(ClasspathHelper.forClass(this.getClass()));
		config.setUrls(ClasspathHelper.forClass(this.getClass()), ClasspathHelper.forClass(LogicObjects.class));
		Reflections reflections = new Reflections(config);
		Set set = reflections.getSubTypesOf(Adapter.class);
		System.out.println(set);
	}
	
	@Test
	public void testSubtypes2() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		
		FilterBuilder fb = new FilterBuilder();
		fb.include(FilterBuilder.prefix("org"));
		config.filterInputsBy(fb);
		
		config.setUrls(ClasspathHelper.forClass(this.getClass()), ClasspathHelper.forClass(LogicObjects.class));

		SubTypesScanner scanner = new SubTypesScanner(false);
		config.setScanners(scanner);
		
		Reflections reflections = new Reflections(config);
		Set<Class<? extends Object>> classes = reflections.getSubTypesOf(Object.class);
		System.out.println(classes);
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return Adapter.class.isAssignableFrom(clazz);
			  }
		};
		classes = ReflectionUtils.getAll(classes, predicate);
		System.out.println(classes);
	}
	*/
	@Test
	public void testSubtypes3() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		
		
		config.setUrls(ClasspathHelper.forClass(this.getClass()));

		//SubTypesScanner scanner = new SubTypesScanner(false);
		//config.setScanners(scanner);
		
		Reflections reflections = new Reflections(config);
		Set<Class<? extends Object>> classes = reflections.getTypesAnnotatedWith(LObject.class);
		System.out.println(classes);
	}

}
