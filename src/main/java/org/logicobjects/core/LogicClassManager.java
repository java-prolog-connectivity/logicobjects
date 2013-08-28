package org.logicobjects.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jpc.converter.TermConvertable;
import org.logicobjects.annotation.LConverter;
import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LDelegationObjectConverter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.descriptor.ConverterDescriptor;
import org.logicobjects.descriptor.LogicObjectDescriptor;
import org.minitoolbox.reflection.ReflectionUtil;
import org.minitoolbox.reflection.typevisitor.FindFirstTypeVisitor;
import org.minitoolbox.reflection.typevisitor.TypeVisitor.InterfaceMode;

public class LogicClassManager {

	private boolean includesTermConvertableInHierarchy(Class clazz) {
		return ReflectionUtil.includesInterfaceInHierarchy(clazz, TermConvertable.class);
	}
	
//	private boolean hasLObjectAnnotation(Class clazz) {
//		return clazz.isAnnotationPresent(LObject.class);
//	}
//	
//	private boolean hasLDelegationObjectAnnotation(Class clazz) {
//		return clazz.isAnnotationPresent(LDelegationObject.class);
//	}
//	
//	private boolean hasConverterAnnotation(Class clazz) {
//		return clazz.isAnnotationPresent(LConverter.class);
//	}
//	
//	private boolean hasLDelegationConverterAnnotation(Class clazz) {
//		return clazz.isAnnotationPresent(LDelegationObjectConverter.class);
//	}
	
	public LogicClass findLogicClass(Class clazz) {
		if(clazz.isInterface())
			throw new RuntimeException("Interfaces cannot be defined as logic classes");
		//Class nonSyntheticClass = ReflectionUtil.findFirstNonSyntheticClass(clazz);
		LogicClassBuilder logicClassBuilder = new LogicClassBuilder(clazz);
		createBuilderLogicClass(clazz, logicClassBuilder);
		return logicClassBuilder.build();
	}
	
	private void createBuilderLogicClass(Class clazz, LogicClassBuilder logicClassBuilder) {
		if(!Object.class.equals(clazz)) { //reached the top of the hierarchy
			if(!logicClassBuilder.definesObjectToTermStrategy()) {
				if(!logicClassBuilder.definesMethodInvokerStrategy()) {
					if(clazz.isAnnotationPresent(LDelegationObjectConverter.class))
						//logicClassBuilder.setMethodInvokerConverterDescriptor(ConverterDescriptor.create((LDelegationObjectConverter) clazz.getAnnotation(LDelegationObjectConverter.class)));
						logicClassBuilder.setMethodInvokerConverterDescriptor(new LDelegationObjectAnnotationDescriptor());
					else if(clazz.isAnnotationPresent(LDelegationObject.class))
						logicClassBuilder.setMethodInvokerDescriptor(LogicObjectDescriptor.create(clazz, (LDelegationObject) clazz.getAnnotation(LDelegationObject.class)));
				}
				if(includesTermConvertableInHierarchy(clazz))
					logicClassBuilder.setUsingTermConvertableInteface(true);
			}
			if(!logicClassBuilder.isFullySpecified()) {
				if(clazz.isAnnotationPresent(LConverter.class))
					logicClassBuilder.setDefaultTermConverterDescriptor(ConverterDescriptor.create((LConverter) clazz.getAnnotation(LConverter.class)));
				else if(clazz.isAnnotationPresent(LObject.class))
					logicClassBuilder.setTermDescriptor(LogicObjectDescriptor.create(clazz, (LObject) clazz.getAnnotation(LObject.class)));
			}
			if(!logicClassBuilder.isFullySpecified())
				createBuilderLogicClass(clazz.getSuperclass(), logicClassBuilder);
		}
		
	}

	public LogicClass findLogicMethodInvokerClass(Class descendant) {
		Class invokerClass = findMethodInvokerClass(descendant);
		if(invokerClass != null) {
			if(hasLDelegationObjectAnnotation(invokerClass)) {
				return new LogicClass(invokerClass, LogicObjectDescriptor.create((LDelegationObject)invokerClass.getAnnotation(LDelegationObject.class)));
			} else if(hasLObjectAnnotation(invokerClass)){
				return new LogicClass(invokerClass, LogicObjectDescriptor.create((LObject)invokerClass.getAnnotation(LObject.class)));
			}
		}
		return null;
	}
	

	
	public boolean isGuidingClass(Class candidateClass) {
		return isTermObjectClass(candidateClass) || hasLObjectAnnotation(candidateClass) || hasObjectToTermConverter(candidateClass);
	}
	


	/**
	 * Answers the first class/interface in the class hierarchy specifying a logic object method invoker (e.g., annotated with LDelegationObject)
	 * @param candidateClass
	 * @return
	 */
	public Class findMethodInvokerClass(Class candidateClass) {
		FindFirstTypeVisitor finderVisitor = new FindFirstTypeVisitor(InterfaceMode.EXCLUDE_INTERFACES) {
			
			@Override
			public boolean match(Class clazz) {
				return clazz.getAnnotation(LDelegationObject.class) != null || isGuidingClass(clazz);
			}
		};
		finderVisitor.visit(candidateClass);
		return finderVisitor.getFoundType();
	}
	
	public boolean hasGuidingClass(Class clazz) {
		return findGuidingClass(clazz) != null;
	}
	
	/**
	 * The guiding class is the first class in the hierarchy that either implements ITermObject, has a LogicObject annotation, or a LogicTerm annotation
	 * @param candidateClass
	 * @return
	 */
	public Class findGuidingClass(Class candidateClass) {
		if(candidateClass == null || candidateClass.equals(Object.class))
			return null;
		if(isGuidingClass(candidateClass))
			return candidateClass;
		else
			return findGuidingClass(candidateClass.getSuperclass());
	}
	
	private List<LogicClass> asLogicObjectClasses(List<Class> logicClasses) {
		List<LogicClass> logicObjectClasses = new ArrayList<>();
		for(Class clazz : logicClasses)
			logicObjectClasses.add(new LogicClass(clazz));
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
	public List<LogicClass> findAllLogicObjectClasses(Class clazz) {
		if(hasGuidingClass(clazz)) {
			List<Class> logicObjectClasses = findAllAnnotatedLogicClasses(clazz);
			return asLogicObjectClasses(logicObjectClasses);
		} else {
			return Arrays.asList(new LogicClass[] {createFromFirstNonSyntheticClass(clazz)});
		}	
	} 
	
	public List<Class> findAllAnnotatedLogicClasses(Class clazz) {
		List<Class> logicClasses = new ArrayList<Class>();
		findAllAnnotatedLogicClasses(clazz, logicClasses);
		return logicClasses;
	}
	
	private void findAllAnnotatedLogicClasses(Class clazz, List<Class> foundClasses) {
		Class logicClass = findGuidingClass(clazz);
		if(logicClass != null && hasLObjectAnnotation(logicClass)) {
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
	public Class findDelegationObjectClass(Class candidateClass) {
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
		

	public boolean hasNoArgsConstructor(Class clazz) {
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
	
	public boolean hasConstructorWithArgsNumber(Class clazz, int n) {
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
	public boolean hasConstructorWithOneVarArgs(Class clazz) {
		for(Constructor constructor : clazz.getConstructors()) {
			if(constructor.getParameterTypes().length == 1 && constructor.isVarArgs())
				return true;
		}	
		return false;
	}
	
	class LogicClassBuilder {
		
		private Class wrappedClass;
		/**
		 * Flag indicating that the object must be converted to a term by means of the implemented TermConvertable interface.
		 * 
		 */
		private boolean usingTermConvertableInteface;
		
		//mapping descriptors
		private LogicObjectDescriptor defaultTermDescriptor;
		private LogicObjectDescriptor methodInvokerDescriptor;
		//converter descriptors
		private ConverterDescriptor defaultTermConverterDescriptor;
		private ConverterDescriptor methodInvokerConverterDescriptor;

		public LogicClassBuilder(Class wrappedClass) {
			this.wrappedClass = wrappedClass;
		}
		
		public LogicClass build() {
			return new LogicClass(wrappedClass, defaultTermDescriptor, methodInvokerDescriptor, defaultTermConverterDescriptor, methodInvokerConverterDescriptor);
		}
		
		public boolean isUsingTermConvertableInteface() {
			return usingTermConvertableInteface;
		}

		void setUsingTermConvertableInteface(boolean usingTermConvertableInteface) {
			this.usingTermConvertableInteface = usingTermConvertableInteface;
		}
		
		public boolean isFullySpecified() {
			return definesObjectToTermStrategy() && definesTermToObjectStrategy();
		}
		
		public boolean definesMethodInvokerStrategy() {
			return methodInvokerDescriptor != null;
		}
		
		public boolean definesObjectToTermStrategy() {
			return defaultTermDescriptor != null || methodInvokerConverterDescriptor != null || usingTermConvertableInteface;
		}
		
		public boolean definesTermToObjectStrategy() {
			return defaultTermDescriptor != null || defaultTermConverterDescriptor != null;
		}

		public LogicObjectDescriptor getDefaultTermDescriptor() {
			return defaultTermDescriptor;
		}

		public void setDefaultTermDescriptor(LogicObjectDescriptor defaultTermDescriptor) {
			this.defaultTermDescriptor = defaultTermDescriptor;
		}

		public LogicObjectDescriptor getMethodInvokerDescriptor() {
			return methodInvokerDescriptor;
		}

		public void setMethodInvokerDescriptor(
				LogicObjectDescriptor methodInvokerDescriptor) {
			this.methodInvokerDescriptor = methodInvokerDescriptor;
		}

		public ConverterDescriptor getDefaultTermConverterDescriptor() {
			return defaultTermConverterDescriptor;
		}

		public void setDefaultTermConverterDescriptor(
				ConverterDescriptor defaultTermConverterDescriptor) {
			this.defaultTermConverterDescriptor = defaultTermConverterDescriptor;
		}

		public ConverterDescriptor getMethodInvokerConverterDescriptor() {
			return methodInvokerConverterDescriptor;
		}

		public void setMethodInvokerConverterDescriptor(
				ConverterDescriptor methodInvokerConverterDescriptor) {
			this.methodInvokerConverterDescriptor = methodInvokerConverterDescriptor;
		}

		public Class getWrappedClass() {
			return wrappedClass;
		}

	}

}
