package org.logicobjects.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jpc.converter.TermConvertable;
import org.logicobjects.annotation.LConverter;
import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LDelegationObjectConverter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.descriptor.AnnotationConverterDescriptor;
import org.logicobjects.descriptor.AnnotationLogicObjectDescriptor;
import org.logicobjects.descriptor.ClassLogicObjectDescriptor;
import org.logicobjects.descriptor.ConverterDescriptor;
import org.logicobjects.descriptor.LogicObjectDescriptor;
import org.minitoolbox.reflection.ReflectionUtil;
import org.minitoolbox.reflection.typevisitor.FindFirstTypeVisitor;
import org.minitoolbox.reflection.typevisitor.TypeVisitor.InterfaceMode;

public class LogicClassManager {

	//private static Logger logger = LoggerFactory.getLogger(LogicClassManager.class);
	
	private boolean includesTermConvertableInHierarchy(Class clazz) {
		return ReflectionUtil.includesInterfaceInHierarchy(clazz, TermConvertable.class);
	}
	
	public LogicClass findLogicClass(Class clazz) {
		if(clazz.isInterface())
			throw new RuntimeException("Interfaces cannot be defined as logic classes");
		LogicClassBuilder logicClassBuilder = new LogicClassBuilder(clazz);
		prepareLogicClassBuilder(clazz, logicClassBuilder);
		return logicClassBuilder.build();
	}
	
	private void prepareLogicClassBuilder(Class clazz, LogicClassBuilder logicClassBuilder) {
		if(!Object.class.equals(clazz)) { //reached the top of the hierarchy
			if(!logicClassBuilder.definesObjectToTermStrategy()) {
				if(!logicClassBuilder.definesMethodInvokerStrategy()) {
					if(clazz.isAnnotationPresent(LDelegationObjectConverter.class)) {
						logicClassBuilder.setMethodInvokerConverterDescriptor(new AnnotationConverterDescriptor(clazz, (LDelegationObjectConverter)clazz.getAnnotation(LDelegationObjectConverter.class)));
						//currently commented out the warning below since a class may define a converter, but the LObject annotation may provide information for the loader
						/*
						if(clazz.isAnnotationPresent(LDelegationObject.class)) {
							logger.warn(clazz.getSimpleName() + " is annotated with both " + LDelegationObjectConverter.class.getSimpleName() + " and " + LDelegationObject.class.getSimpleName() +
									". " + LDelegationObject.class.getSimpleName() + " will be ignored");
						}
						*/
					} else if(clazz.isAnnotationPresent(LDelegationObject.class))
						logicClassBuilder.setMethodInvokerDescriptor(new AnnotationLogicObjectDescriptor(clazz, (LDelegationObject)clazz.getAnnotation(LDelegationObject.class)));
				}
				if(includesTermConvertableInHierarchy(clazz))
					logicClassBuilder.setUsingTermConvertableInteface(true);
			}
			if(!logicClassBuilder.isFullySpecified()) {
				if(clazz.isAnnotationPresent(LConverter.class)) {
					logicClassBuilder.setDefaultTermConverterDescriptor(new AnnotationConverterDescriptor(clazz, (LConverter)clazz.getAnnotation(LConverter.class)));
					/*
					if(clazz.isAnnotationPresent(LObject.class)) {
						logger.warn(clazz.getSimpleName() + " is annotated with both " + LConverter.class.getSimpleName() + " and " + LObject.class.getSimpleName() +
								". " + LObject.class.getSimpleName() + " will be ignored");
					}
					*/
				} else if(clazz.isAnnotationPresent(LObject.class))
					logicClassBuilder.setDefaultTermDescriptor(new AnnotationLogicObjectDescriptor(clazz, (LObject)clazz.getAnnotation(LObject.class)));
			}
			if(!logicClassBuilder.isFullySpecified())
				prepareLogicClassBuilder(clazz.getSuperclass(), logicClassBuilder);
		}
		
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
		


	
	
	class LogicClassBuilder {
		
		private Class wrappedClass;
		private Class firstNonSyntheticClass;
		
		/**
		 * Flag indicating that an instance must be converted to a term by means of the TermConvertable interface.
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
			this.firstNonSyntheticClass = ReflectionUtil.findFirstNonSyntheticClass(wrappedClass);
		}

		public Class getWrappedClass() {
			return wrappedClass;
		}

		/**
		 * Build a LogicClass and returns it.
		 * If the builder does not have enough data to build a LogicClass, 
		 * will create it using a mapping descriptor based on values inferred from the first non-synthetic class in the hierarchy.
		 * @return a LogicClass built according to the properties of the builder.
		 */
		public LogicClass build() {
			if(!isFullySpecified()) {
				return new LogicClass(wrappedClass, defaultTermDescriptor, methodInvokerDescriptor, defaultTermConverterDescriptor, methodInvokerConverterDescriptor);
			} else {
				return new LogicClass(wrappedClass, new ClassLogicObjectDescriptor(firstNonSyntheticClass), methodInvokerDescriptor, defaultTermConverterDescriptor, methodInvokerConverterDescriptor);
			}
		}
		
		public boolean isUsingTermConvertableInteface() {
			return usingTermConvertableInteface;
		}

		void setUsingTermConvertableInteface(boolean usingTermConvertableInteface) {
			this.usingTermConvertableInteface = usingTermConvertableInteface;
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

		public boolean isFullySpecified() {
			return definesObjectToTermStrategy() && definesTermToObjectStrategy();
		}
		
		public boolean definesMethodInvokerStrategy() {
			return methodInvokerDescriptor != null || methodInvokerConverterDescriptor != null;
		}
		
		public boolean definesObjectToTermStrategy() {
			return defaultTermDescriptor != null || defaultTermConverterDescriptor != null || usingTermConvertableInteface;
		}
		
		public boolean definesTermToObjectStrategy() {
			return defaultTermDescriptor != null || defaultTermConverterDescriptor != null;
		}

	}

}
