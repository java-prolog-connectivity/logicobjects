package org.logicobjects.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.logicobjects.adapter.adaptingcontext.AbstractLogicObjectDescriptor;
import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.visitor.FindFirstTypeVisitor;
import org.reflectiveutils.visitor.TypeVisitor.InterfaceMode;

/**
 * A class providing a description (i.e., mapping information) for instantiating logic objects
 * Part of the data is in the logic side 
 * @author scastro
 *
 */
public class LogicObjectClass {
	
	private Class logicClass;
	private AbstractLogicObjectDescriptor logicObjectDescriptor;
	
	public LogicObjectClass(Class logicClass) {
		this(logicClass, AbstractLogicObjectDescriptor.create(logicClass)); //default LogicObjectDescriptor
	}
	
	public LogicObjectClass(Class logicClass, AbstractLogicObjectDescriptor logicObjectDescriptor) {
		assert(logicClass != null);
		assert(logicObjectDescriptor != null);
		this.logicClass = logicClass;
		this.logicObjectDescriptor = logicObjectDescriptor;
	}

	public static LogicObjectClass findLogicObjectClass(Class descendant) {
		Class guidingClass = findGuidingClass(descendant);
		if(guidingClass != null) {
			return isLogicClass(guidingClass)?new LogicObjectClass(guidingClass):null;
		}
		else {
			return createFromFirstNonSyntheticClass(descendant);
		}
	}
	
	private static LogicObjectClass createFromFirstNonSyntheticClass(Class descendant) {
		return new LogicObjectClass(ReflectionUtil.findFirstNonSyntheticClass(descendant));
	}
	
	public static LogicObjectClass findLogicMethodInvokerClass(Class descendant) {
		Class invokerClass = findMethodInvokerClass(descendant);
		if(invokerClass != null) {
			if(isDelegationObjectClass(invokerClass)) {
				return new LogicObjectClass(invokerClass, AbstractLogicObjectDescriptor.create((LDelegationObject)invokerClass.getAnnotation(LDelegationObject.class)));
			} else if(isLogicClass(invokerClass)){
				return new LogicObjectClass(invokerClass, AbstractLogicObjectDescriptor.create((LObject)invokerClass.getAnnotation(LObject.class)));
			}
		}
		return null;
	}
	
	public Class getWrappedClass() {
		return logicClass;
	}
	
	public String getSimpleName() {
		return logicClass.getSimpleName();
	}
	
	public Package getPackage() {
		return logicClass.getPackage();
	}
	
	public URL getResource(String name) {
		return logicClass.getResource(name);
	}
	
	public Field getDeclaredField(String name) throws NoSuchFieldException, SecurityException {
		return logicClass.getDeclaredField(name);
	}
	
	public Method getDeclaredMethod(String name) throws NoSuchMethodException, SecurityException {
		return logicClass.getDeclaredMethod(name);
	}
	
	
	
	
	
	public AbstractLogicObjectDescriptor getLogicObjectDescriptor() {
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
	
	public String getLObjectArgsList() {
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
	
	public static boolean hasGuidingClass(Class clazz) {
		return findGuidingClass(clazz) != null;
	}
	
	/**
	 * The guiding class is the first class in the hierarchy that either implements ITermObject, has a LogicObject annotation, or a LogicTerm annotation
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
	
	private static List<LogicObjectClass> asLogicObjectClasses(List<Class> logicClasses) {
		List<LogicObjectClass> logicObjectClasses = new ArrayList<>();
		for(Class clazz : logicClasses)
			logicObjectClasses.add(new LogicObjectClass(clazz));
		return logicObjectClasses;
	}
	
	
	/**
	 * Answers a list of logic object classes starting from the more specialized
	 * If there is not a guiding class in the hierarchy, a default logic object class will be chosen according to the one arg constructor of LogicObjectClass
	 * Otherwise, a list will all the logic object classes (classes annotated with LObject) is collected and transformed to a list of LogicObjectClass
	 * This list can be empty since it is possible that the guiding class is a class implementing ITermObject or annotated with an explicit adapter
	 * This method is useful from implementing the loading mechanism
	 * In order to load a logic class, first its ancestor dependencies must be loaded
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<LogicObjectClass> findAllLogicObjectClasses(Class clazz) {
		if(hasGuidingClass(clazz)) {
			List<Class> logicObjectClasses = findAllAnnotatedLogicClasses(clazz);
			return asLogicObjectClasses(logicObjectClasses);
		} else {
			return Arrays.asList(new LogicObjectClass[] {createFromFirstNonSyntheticClass(clazz)});
		}	
	} 
	
	public static List<Class> findAllAnnotatedLogicClasses(Class clazz) {
		List<Class> logicClasses = new ArrayList<Class>();
		findAllAnnotatedLogicClasses(clazz, logicClasses);
		return logicClasses;
	}
	
	private static void findAllAnnotatedLogicClasses(Class clazz, List<Class> foundClasses) {
		Class logicClass = findGuidingClass(clazz);
		if(logicClass != null && isLogicClass(logicClass)) {
			foundClasses.add(logicClass);
			findAllAnnotatedLogicClasses(clazz.getSuperclass(), foundClasses);
		}
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

