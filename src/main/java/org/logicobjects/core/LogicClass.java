package org.logicobjects.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import jpl.Term;

import org.logicobjects.adapter.LogicResourcePathAdapter;
import org.logicobjects.adapter.adaptingcontext.LMethodInvokerDescription;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.visitor.FindFirstTypeVisitor;
import org.reflectiveutils.visitor.TypeVisitor.InterfaceMode;

public class LogicClass {
	
	private Class clazz;
	
	public LogicClass(Class clazz) {
		this.clazz = clazz;
	}
	
	public Class getWrappedClass() {
		return clazz;
	}
	
	public String getLogicName() {
		LObject lObjectAnnotation = (LObject) clazz.getAnnotation(LObject.class);
		String name = lObjectAnnotation.name();
		if(!name.isEmpty())
			return name;
		else
			return LogicUtil.javaClassNameToProlog(clazz.getSimpleName());
	}
	
	public String[] getParameters() {
		LObject lObjectAnnotation = (LObject) clazz.getAnnotation(LObject.class);
		return lObjectAnnotation.params();
	}
	
	public String[] getImports() {
		LObject lObjectAnnotation = (LObject) clazz.getAnnotation(LObject.class);
		return lObjectAnnotation.imports();
	}
	
	public String[] getModules() {
		LObject lObjectAnnotation = (LObject) clazz.getAnnotation(LObject.class);
		return lObjectAnnotation.modules();
	}
	
	public boolean automaticImport() {
		LObject lObjectAnnotation = (LObject) clazz.getAnnotation(LObject.class);
		return lObjectAnnotation.automaticImport();
	}
	
	

	/*
	 * Return a boolean indicating if all the modules and objects were loaded correctly
	 */
	public static boolean loadDependencies(Class clazz) {
		clazz = findDependencyInfoClass(clazz);
		if(clazz == null)
			return false;
		
		LMethodInvokerDescription lMethodInvokerDescription = LMethodInvokerDescription.create(clazz);  //look at the annotations of the class and return an object having information about how to load dependencies
		
		//LObject aLObject = getLogicObjectAnnotation(clazz);
		
		if(!lMethodInvokerDescription.automaticImport())
			return false;
		
		boolean result = true;
		
		String[] annotationModules = lMethodInvokerDescription.modules();
		String[] bundleModules = getBundleModules(clazz);
		if(bundleModules == null) {
			bundleModules = new String[] {};
		}
		
		Set<String> allModules = new LinkedHashSet<String>(); //Set is used to avoid duplicates. Using LinkedHashSet instead of HashSet (the faster), since the former will preserve the insertion order
		allModules.addAll(Arrays.asList(bundleModules));
		allModules.addAll(Arrays.asList(annotationModules));
		
		List<Term> moduleTerms = new ArrayList<Term>();
		new LogicResourcePathAdapter().adapt(allModules, moduleTerms);
		
		result = LogicEngine.getDefault().ensureLoaded(moduleTerms); //loading prolog modules
		
		String[] annotationImports = lMethodInvokerDescription.imports();
		String[] bundleImports = getBundleImports(clazz);
		if(bundleImports == null) {
			bundleImports = new String[] {};
		}
		String[] defaultImports = getDefaultImports(clazz);
		
		Set<String> allImports = new LinkedHashSet<String>(); //Set is used to avoid duplicates.
		allImports.addAll(Arrays.asList(bundleImports));
		allImports.addAll(Arrays.asList(annotationImports));
		allImports.addAll(Arrays.asList(defaultImports));
		

		List<Term> importTerms = new ArrayList<Term>();
		new LogicResourcePathAdapter().adapt(allImports, importTerms);
		
		return LogicEngine.getDefault().logtalkLoad(importTerms) && result; //loading Logtalk objects
	}
	
	private static final String IMPORTS = "objects"; //"objects" in logicobjects files will be loaded with logtalk_load
	private static final String MODULES = "modules"; //"modules" in logicobjects files will be loaded with ensure_loaded
	public static final String BUNDLE_NAME = "logicobjects";
	
	public static ResourceBundle getBundle(Class clazz) {
		try {
			return ResourceBundle.getBundle(clazz.getPackage().getName() + "."+BUNDLE_NAME);
		} catch(MissingResourceException e) {
			return null;
		}
	}
	
	public static String[] getBundleImports(Class clazz) {
		return getBundleProperty(clazz, IMPORTS);
	}
	
	public static String[] getBundleModules(Class clazz) {
		return getBundleProperty(clazz, MODULES);
	}
	
	public static String[] getBundleProperty(Class clazz, String propertyName) {
		ResourceBundle bundle = getBundle(clazz);
		if(bundle == null)
			return null;
		if(bundle.containsKey(propertyName)) {
			String stringProperties = bundle.getString(propertyName);
			String[] properties = stringProperties.split(",");
			return properties;
		} else
			return new String[] {};
	}
	
	public static String[] getAnnotationImports(Class clazz) {
		return ((LObject)clazz.getAnnotation(LObject.class)).imports();
	}
	
	public static String[] getAnnotationModules(Class clazz) {
		return ((LObject)clazz.getAnnotation(LObject.class)).modules();
	}
	
	private static boolean addIfLgtFileExists(String fileName, Class clazz, List<String> destiny) {
		if(fileName == null || fileName.equals(""))
			return false;
		String packageName = clazz.getPackage().getName();
		if(clazz.getResource(fileName+".lgt") != null) { //the getResource method will append before the path of the class
			destiny.add(packageName+"."+fileName);
			return true;
		}
		return false;
	}
	
	public static String[] getDefaultImports(Class clazz) {
		List<String> defaultImports = new ArrayList<String>();
		addIfLgtFileExists(clazz.getSimpleName(), clazz, defaultImports);
		
		LObject logicObject = getLogicObjectAnnotation(clazz);
		if(logicObject != null)
			addIfLgtFileExists(logicObject.name(), clazz, defaultImports);
		return defaultImports.toArray(new String[] {});
	}
	
	
	
	

	
	public static LObject getLogicObjectAnnotation(Class clazz) {
		Class annotatedClass = findLogicClass(clazz);
		if(annotatedClass != null)
			return (LObject)annotatedClass.getAnnotation(LObject.class);
		else
			return null;
	}
	
	
	
	/*
	 * The guiding class is the first class in the hierarchy that either implements TermObject, has a LogicObject annotation, or a LogicTerm annotation
	 */
	public static Class findGuidingClass(Class candidateClass) {
		if(candidateClass.equals(Object.class))
			return null;
		if(isGuidingClass(candidateClass))
			return candidateClass;
		else
			return findGuidingClass(candidateClass.getSuperclass());
	}
	
	public static boolean isGuidingClass(Class candidateClass) {
		return isTermObjectClass(candidateClass) || candidateClass.getAnnotation(LObject.class) != null || candidateClass.getAnnotation(LTermAdapter.class) != null;
	}
	
	public static boolean isTermObjectClass(Class aClass) {
		for(Class anInterface : aClass.getInterfaces()) {
			if(ITermObject.class.isAssignableFrom(anInterface))
				return true;
		}
		return false;
	}


	/*
	 * This method returns the first class or interface is the hierarchy annotated with the LObject annotation 
	 */
	public static Class findLogicClass(Class candidateClass) {
		/*
		if(candidateClass.equals(Object.class)) //end of the hierarchy
			return null;
		if(candidateClass.getAnnotation(LObject.class) != null)
			return candidateClass;
		else {
			Class logicClass = null;
			for(Class interfaze : candidateClass.getInterfaces()) { //answers only the interfaces declared by the class, does not include the ones in its superclass
				logicClass = findLogicClass(interfaze);
				if(logicClass != null)
					return logicClass;
			}
			if(!candidateClass.isInterface()) {
				logicClass = findLogicClass(candidateClass.getSuperclass());
			} 
			return logicClass;
		}
		*/
		
		FindFirstTypeVisitor finderVisitor = new FindFirstTypeVisitor(InterfaceMode.EXCLUDE_INTERFACES) {
			@Override
			public boolean match(Class clazz) {
				return clazz.getAnnotation(LObject.class) != null;
			}
		};
		finderVisitor.visit(candidateClass);
		return finderVisitor.getFoundType();
		
	}
	
	/**
	 * Answers the first class/interface in the class hierarchy specifying the logic object method invoker
	 * @param candidateClass
	 * @return
	 */
	public static Class findDelegationObjectClass(Class candidateClass) {
		FindFirstTypeVisitor finderVisitor = new FindFirstTypeVisitor(InterfaceMode.EXCLUDE_INTERFACES) {
			@Override
			public boolean doVisit(Class clazz) {
				boolean shouldContinue = super.doVisit(clazz);
				if(shouldContinue)
					shouldContinue = !isGuidingClass(clazz);
				return shouldContinue;
			}
			
			@Override
			public boolean match(Class clazz) {
				return clazz.getAnnotation(LDelegationObject.class) != null;
			}
		};
		finderVisitor.visit(candidateClass);
		return finderVisitor.getFoundType();
	}
	
	public static Class findDependencyInfoClass(Class candidateClass) {
		FindFirstTypeVisitor finderVisitor = new FindFirstTypeVisitor(InterfaceMode.EXCLUDE_INTERFACES) {
			@Override
			public boolean doVisit(Class clazz) {
				boolean shouldContinue = super.doVisit(clazz);
				if(shouldContinue)
					shouldContinue = !isTermObjectClass(clazz);
				return shouldContinue;
			}
			
			@Override
			public boolean match(Class clazz) {
				return clazz.getAnnotation(LDelegationObject.class) != null || clazz.getAnnotation(LObject.class) != null;
			}
		};
		finderVisitor.visit(candidateClass);
		return finderVisitor.getFoundType();
	}
	
}

