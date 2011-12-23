package org.logicobjects.test;

import java.net.URL;
import java.util.Set;

import org.junit.Test;
import org.logicobjects.annotation.LObject;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class TestReflections /*extends TestCase*/ {

	@Test
	public void testFindingLogicObjects() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		
		
		
		FilterBuilder fb = new FilterBuilder();
		fb.include(FilterBuilder.prefix("logicobjects"));
		//fb.include(FilterBuilder.prefix("iv4e"));
		//fb.include(FilterBuilder.prefix("bin"));
		config.filterInputsBy(fb);
		
		

		//config.addUrls(ClasspathHelper.forClassLoader());
		
		//config.setUrls(ClasspathHelper.forPackage("logicobjects"));
		config.setUrls(ClasspathHelper.forPackage("logicobjects"));
		
		System.out.println("printing urls");
		for(URL url : ClasspathHelper.forPackage("iv4e")) {
			System.out.println(url);
		}
		

		Reflections system_reflections = new Reflections(config);
		//Reflections system_reflections2 = new Reflections("logicobjects");
		
		Set<Class<?>> classes = system_reflections.getTypesAnnotatedWith(LObject.class);
		
		System.out.println("printing logic classes: ");
		for(Class clazz : classes) {
			System.out.println("Logic class: "+clazz.getName());
		}
		System.out.println("done ...");
	}
	
	
	
	
	
	/*
	@Test
	public void testContextClass() {
		LContext context = new LContext();
		context.addSearchPath("iv4e");
		Class clazz = context.findLogicClass("iv", 0);
		System.out.println(clazz.getName());
		clazz = context.findLogicClass("intensional_set", 3);
		System.out.println(clazz.getName());
	}
*/
}
