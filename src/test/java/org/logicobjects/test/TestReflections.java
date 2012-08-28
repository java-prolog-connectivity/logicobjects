package org.logicobjects.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.junit.Test;
import org.logicobjects.annotation.LObject;
import org.logicobjects.core.LogicObjectFactory;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Predicate;


public class TestReflections extends LocalLogicTest {
/*
	public static interface I{}
	
	@Test
	public void testI() {
		I i = new I(){};
		System.out.println(i.getClass());
		System.out.println(i.getClass().getSuperclass());
	}
*/	
	
	@Test
	public void testFindingLogicObjectsWithCustomizedReflections() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		FilterBuilder fb = new FilterBuilder();
		fb.include(FilterBuilder.prefix("org.logicobjects"));
		config.filterInputsBy(fb);
		config.setUrls(ClasspathHelper.forPackage("org.logicobjects"));
		config.setScanners(new SubTypesScanner(false));
		Reflections reflections = new Reflections(config);
		//Set<Class<? extends Adapter>> classes = reflections.getSubTypesOf(Adapter.class);//getSubTypesOf(Object.class);
		Set<Class<? extends Object>> classes = reflections.getSubTypesOf(Object.class);
		//Set<Class<? extends LogicEngine>> classes = reflections.getSubTypesOf(LogicEngine.class);
		
		Predicate predicate = new Predicate<Class>() {
			  public boolean apply(Class clazz) {
			    return clazz.getAnnotation(LObject.class) != null;
			  }
		};

		classes = ReflectionUtils.getAll(classes, predicate);
		
		System.out.println("Number of classes: " + classes.size());
		for(Class clazz : classes) {
			System.out.println(clazz);
		}
		System.out.println("done ...");
	}
	
	
	
	@Test
	public void testFindLogicObjectsWithReflections() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		FilterBuilder fb = new FilterBuilder();
		
		fb.include(FilterBuilder.prefix("org.logicobjects"));
		//fb.include(FilterBuilder.prefix("iv4e"));
		//fb.include(FilterBuilder.prefix("bin"));
		config.filterInputsBy(fb);
		
		

		//config.addUrls(ClasspathHelper.forClassLoader());
		
		//config.setUrls(ClasspathHelper.forPackage("logicobjects"));
		config.setUrls(ClasspathHelper.forPackage("org.logicobjects"));
		

		

		Reflections system_reflections = new Reflections(config);
		//Reflections system_reflections2 = new Reflections("logicobjects");
		
		Set<Class<?>> classes = system_reflections.getTypesAnnotatedWith(LObject.class);
		
		System.out.println("printing logic classes: ");
		System.out.println("Number of classes: " + classes.size());
		for(Class clazz : classes) {
			System.out.println("Logic class: "+clazz.getName());
		}
		System.out.println("done ...");
	}
	
	
	
	@Test
	public void testFindLogicObjectsWithFramework() {
		Set<Class<?>> classes = LogicObjectFactory.getDefault().getContext().getLogicClasses(); //this does not include logic classes in the test packages
		System.out.println("printing logic classes: ");
		System.out.println("Number of classes: " + classes.size());
		for(Class clazz : classes) {
			System.out.println(clazz);
		}
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
	
	/**
	 * Test for finding resources with a given name
	 * (Currently) does not work if the resources are in a jar.
	 * it is commented out so maven will not complain in the automated unit tests execution in the 'installation' phase (where the project is zipped in a jar)
	 */
	/*
	@Test
	public void testFindResources() {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forPackage("org.logicobjects"))
        .setScanners(new ResourcesScanner()));
		
		
		Predicate predicate = new Predicate<String>() {
			  public boolean apply(String string) {
			    return string.matches(".*\\.properties");
			  }
		};
		
		Set<String> propertiesFiles = reflections.getResources(predicate);  //in case a complex predicate is needed
		//Set<String> propertiesFiles = reflections.getResources(Pattern.compile(".*\\.properties")); //in case the condition is just based on the name of the file
		
		System.out.println("Number of property files: " + propertiesFiles.size());
		for(String propertyFile : propertiesFiles) {
			File file = new File(propertyFile);
			//assertTrue(file.exists());
		}
	}
	*/
	
}
