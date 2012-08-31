package org.logicobjects.core;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import jpl.Term;

import org.logicobjects.adapter.LogicResourcePathAdapter;
import org.logicobjects.adapter.adaptingcontext.LogicObjectDescriptor;
import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.visitor.FindFirstTypeVisitor;
import org.reflectiveutils.visitor.TypeVisitor.InterfaceMode;

/**
 * A class providing a description for instantiating logic objects
 * Part of the data is in the logic side 
 * @author scastro
 *
 */
public class LogicObjectClass {
	
	private Class logicClass;
	private LogicObjectDescriptor logicObjectDescriptor;
	
	public LogicObjectClass(Class logicClass) {
		this(logicClass, LogicObjectDescriptor.create(logicClass)); //default LogicObjectDescriptor
	}
	
	public LogicObjectClass(Class logicClass, LogicObjectDescriptor logicObjectDescriptor) {
		assert(logicClass != null);
		assert(logicObjectDescriptor != null);
		this.logicClass = logicClass;
		this.logicObjectDescriptor = logicObjectDescriptor;
	}

	public static LogicObjectClass findLogicObjectClass(Class descendant) {
		Class guidingClass = findGuidingClass(descendant);
		if(guidingClass != null)
			return isLogicClass(guidingClass)?new LogicObjectClass(guidingClass):null;
		else
			return null;
	}
	

	public static LogicObjectClass findLogicMethodInvokerClass(Class descendant) {
		Class invokerClass = findMethodInvokerClass(descendant);
		if(invokerClass != null) {
			if(isDelegationObjectClass(invokerClass)) {
				return new LogicObjectClass(invokerClass, LogicObjectDescriptor.create((LDelegationObject)invokerClass.getAnnotation(LDelegationObject.class)));
			} else {
				return new LogicObjectClass(invokerClass, LogicObjectDescriptor.create((LObject)invokerClass.getAnnotation(LObject.class)));
			}
		}
		return null;
	}
	
	
	public Class getWrappedClass() {
		return logicClass;
	}
	
	
	public LogicObjectDescriptor getLogicObjectDescriptor() {
		return logicObjectDescriptor;
	}
	
	public String getLObjectName() {
		String name = getLogicObjectDescriptor().name();
		if(!name.isEmpty())
			return name;
		else
			return LogicUtil.javaClassNameToProlog(logicClass.getSimpleName());
	}
	
	public String[] getLObjectArgs() {
		return getLogicObjectDescriptor().args();
	}
	
	public String getLObjectArgsArray() {
		return getLogicObjectDescriptor().argsList();
	}
	
	public String[] getImports() {
		return getLogicObjectDescriptor().imports();
	}
	
	public String[] getModules() {
		return getLogicObjectDescriptor().modules();
	}
	
	public boolean automaticImport() {
		return getLogicObjectDescriptor().automaticImport();
	}
	
	
	
	
	
	

	
	public static boolean isGuidingClass(Class candidateClass) {
		return isTermObjectClass(candidateClass) || isLogicClass(candidateClass) || hasTermAdapter(candidateClass);
	}
	
	public static boolean isTermObjectClass(Class clazz) {
		return ITermObject.class.isAssignableFrom(clazz);
	}
	
	public static boolean isLogicClass(Class clazz) {
		return clazz.isAnnotationPresent(LObject.class);
	}
	
	public static boolean isDelegationObjectClass(Class clazz) {
		return clazz.isAnnotationPresent(LDelegationObject.class);
	}
	
	public static boolean hasTermAdapter(Class clazz) {
		return clazz.isAnnotationPresent(LTermAdapter.class);
	}

	/**
	 * Answers the first class/interface in the class hierarchy specifying a logic object method invoker (e.g., annotated with LDelegationObject)
	 * @param candidateClass
	 * @return
	 */
	public static Class findMethodInvokerClass(Class candidateClass) {
		FindFirstTypeVisitor finderVisitor = new FindFirstTypeVisitor(InterfaceMode.EXCLUDE_INTERFACES) {
			
			@Override
			public boolean match(Class clazz) {
				return clazz.getAnnotation(LDelegationObject.class) != null || isGuidingClass(clazz);
			}
		};
		finderVisitor.visit(candidateClass);
		return finderVisitor.getFoundType();
	}
	

	/**
	 * The guiding class is the first class in the hierarchy that either implements TermObject, has a LogicObject annotation, or a LogicTerm annotation
	 * @param candidateClass
	 * @return
	 */
	public static Class findGuidingClass(Class candidateClass) {
		if(candidateClass == null || candidateClass.equals(Object.class))
			return null;
		if(isGuidingClass(candidateClass))
			return candidateClass;
		else
			return findGuidingClass(candidateClass.getSuperclass());
	}
	
	
	/**
	 * Answers the delegation class in the hierarchy
	 * If before arriving to the class, it is found a "guiding" class (a class with LObject annotation or other information for converting an object to a LObject) the method will return null
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
		
		
		
		
	public static boolean loadDependencies(Class clazz) {
		boolean result = true;
		LogicObjectClass logicObjectClass = findLogicObjectClass(clazz);
		if(logicObjectClass != null) {
			result = logicObjectClass.loadDependencies();
		} else {
			loadDefaultDependencies(clazz);
		}
		return result;
	}
		
		
	public boolean loadDependencies() {
		if(!getLogicObjectDescriptor().automaticImport())
			return false;
		
		boolean result = true; //we have succeed until we demonstrate the contrary :)
		
		//LOADING PROLOG MODULES
		String[] descriptorModules = getLogicObjectDescriptor().modules(); //modules defined in the descriptor (e.g., with the LObject annotation)
		String[] bundleModules = getBundleModules(); //modules defined in the property file
		if(bundleModules == null) {
			bundleModules = new String[] {};
		}
		Set<String> allModules = new LinkedHashSet<String>(); //Set is used to avoid duplicates. Using LinkedHashSet instead of HashSet (the faster), since the former will preserve the insertion order
		allModules.addAll(Arrays.asList(bundleModules));
		allModules.addAll(Arrays.asList(descriptorModules));
		
		allModules = normalizeFileNames(allModules);
		
		List<Term> moduleTerms = new ArrayList<Term>();
		new LogicResourcePathAdapter().adapt(allModules, moduleTerms);
		
		result = LogicEngine.getDefault().ensureLoaded(moduleTerms); //loading prolog modules
		
		
		//LOADING LOGTALK OBJECTS
		String[] descriptorImports = logicObjectDescriptor.imports();
		String[] bundleImports = getBundleImports();
		if(bundleImports == null) {
			bundleImports = new String[] {};
		}
		String[] defaultImports = getLogicObjectClassDefaultImports();
		
		Set<String> allImports = new LinkedHashSet<String>(); //Set is used to avoid duplicates.
		allImports.addAll(Arrays.asList(bundleImports));
		allImports.addAll(Arrays.asList(descriptorImports));
		allImports.addAll(Arrays.asList(defaultImports));
		
		allImports = normalizeFileNames(allImports);
		
		List<Term> importTerms = new ArrayList<Term>();
		new LogicResourcePathAdapter().adapt(allImports, importTerms);
		
		result = LogicEngine.getDefault().logtalkLoad(importTerms) && result; //loading Logtalk objects

		loadDefaultDependencies(logicClass);
		return result;
	}
	
	private Set<String> normalizeFileNames(Set<String> names) {
		Set<String> fileNames = new LinkedHashSet<String>();
		for(String s : names) {
			fileNames.add(normalizeFileName(s));
		}
		return fileNames;
	}
	
	public static String normalizeFileName(String name) {
		return name.trim().replaceAll("\\.(lgt|pl)", "");
	}

	/**
	 * Load the default dependencies of a class
	 * These dependencies are based on the class name only, it does not assume that the class is annotated with LObject or LDelegationObject (e.g., it just implements the ITermObject interface or it is annotated with the LTermAdapter adapter)
	 * @param clazz
	 */
	public static void loadDefaultDependencies(Class clazz) {
		String[] defaultImports = getClassDefaultImports(clazz);
		List<Term> importTerms = new ArrayList<Term>();
		new LogicResourcePathAdapter().adapt(Arrays.asList(defaultImports), importTerms);
		LogicEngine.getDefault().logtalkLoad(importTerms);
	}
	
	
	
	
	
	
	private static final String IMPORTS = "imports"; //"objects" in logicobjects files will be loaded with logtalk_load
	private static final String MODULES = "modules"; //"modules" in logicobjects files will be loaded with ensure_loaded
	public static final String BUNDLE_NAME = "logicobjects";
	
	public ResourceBundle getBundle() {
		try {
			return ResourceBundle.getBundle(logicClass.getPackage().getName() + "."+BUNDLE_NAME);
		} catch(MissingResourceException e) {
			return null;
		}
	}
	
	public String[] getBundleImports() {
		return getBundleProperty(IMPORTS);
	}
	
	public String[] getBundleModules() {
		return getBundleProperty(MODULES);
	}
	
	public String[] getBundleProperty(String propertyName) {
		ResourceBundle bundle = getBundle();
		if(bundle == null)
			return null;
		if(bundle.containsKey(propertyName)) {
			String stringProperties = bundle.getString(propertyName);
			String[] properties = stringProperties.split(",");
			return properties;
		} else
			return new String[] {};
	}
	

	
	private static boolean addIfLgtFileExists(String fileName, Class clazz, List<String> destiny) {
		if(fileName == null || fileName.equals(""))
			return false;
		String packageName = clazz.getPackage().getName();
	
		/**
		 * the getResource method will append before the path of the class
		 * note that for some reason this method is not case sensitive: fileName will match any file with the same name without taking into consideration its case
		 */
		URL url = clazz.getResource(fileName+".lgt");
		if(url != null) { 
			String urlFileName = url.getFile();
//			System.out.println("**************************************************************");
//			System.out.println(urlFileName);
//			System.out.println("**************************************************************");
			//destiny.add(packageName+"."+fileName);
			destiny.add(normalizeFileName(urlFileName));
			return true;
		}
		return false;
	}
	
	
	
	public String[] getLogicObjectClassDefaultImports() {
		List<String> defaultImports = new ArrayList<String>();
		addIfLgtFileExists(getLogicObjectDescriptor().name(), logicClass, defaultImports);
		return defaultImports.toArray(new String[] {});
	}
	
	
	public static String[] getClassDefaultImports(Class clazz) {
		List<String> defaultImports = new ArrayList<String>();
		/**
		 * It is not a good idea to look for a .lgt class with exactly the same name than the class
		 * 1) If it starts with a capital letter then it does not respect the Prolog conventions
		 * 2) If the only variation in the java name and the prolog name (obtained with 'javaClassNameToProlog') the resource will be loaded twice
		 */
		addIfLgtFileExists(clazz.getSimpleName(), clazz, defaultImports);
		addIfLgtFileExists(LogicUtil.javaClassNameToProlog(clazz.getSimpleName()), clazz, defaultImports);
		return defaultImports.toArray(new String[] {});
	}
	
	
	
	

	
	
	
	

	
	
	
	
	
	public static boolean hasNoArgsConstructor(Class clazz) {
		Constructor[] constructors = clazz.getConstructors();
		if(constructors.length == 0) //implicit constructor
			return true;
		try {
			clazz.getConstructor(); //if this method does not thrown a NoSuchMethodException exception, then there is a non-parameters constructor
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static boolean hasConstructorWithArgsNumber(Class clazz, int n) {
		for(Constructor constructor : clazz.getConstructors()) {
			if(constructor.getParameterTypes().length == n)
				return true;
		}	
		return false;
	}
	
	/**
	 * Answers if a class has a constructor with only one declared argument that happens no be a variable args constructor
	 * @param clazz
	 * @return
	 */
	public static boolean hasConstructorWithOneVarArgs(Class clazz) {
		for(Constructor constructor : clazz.getConstructors()) {
			if(constructor.getParameterTypes().length == 1 && constructor.isVarArgs())
				return true;
		}	
		return false;
	}
	



	
}

