package org.reflectiveutils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.reflectiveutils.AbstractTypeWrapper.SingleTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.VariableTypeWrapper;


public class GenericsUtil {


	
	
	private boolean descendantReachedAncestor(Class descendant, Class ancestor) {
		if(!descendant.isInterface())
			return ( descendant.equals(ancestor) || (ancestor.isInterface() && ReflectionUtil.includesInterfaceInHierarchy(descendant, ancestor)) );
		else
			return descendant.equals(ancestor);
	}
	
	public AbstractTypeWrapper[] getParameterizedTypesInterface(Class clazz, Class interfaze) {
		for(Type t : clazz.getGenericInterfaces()) {
			SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(t);
			if(interfaceWrapper.asClass().equals(interfaze)) {
				AbstractTypeWrapper[] parameterizedTypes = AbstractTypeWrapper.wrap(interfaceWrapper.getParameters());
				return parameterizedTypes;
			} 
		}
		return null;
	}
	
	public AbstractTypeWrapper[] findParametersInstantiations(Class ancestor, Type descendant) {
		SingleTypeWrapper descendantTypeWrapper = new SingleTypeWrapper(descendant);
		if(descendantTypeWrapper.isParameterized()) {
			return findParametersInstantiations(ancestor, descendantTypeWrapper.asClass(), AbstractTypeWrapper.wrap(descendantTypeWrapper.getParameters()), false);
		} else {
			return findParametersInstantiations(ancestor, descendantTypeWrapper.asClass(), null, false);
		}
	}
	

	
	public AbstractTypeWrapper[] bindingsForAncestor(Class ancestor, Class descendant, AbstractTypeWrapper[] descendantParameterizedTypes, boolean keepVariableNamesAncestor) {
		if(descendantParameterizedTypes != null) {
			return descendantParameterizedTypes;
		}
		else
			return AbstractTypeWrapper.wrap(ancestor.getTypeParameters());
	}
	
	/*
	public AbstractTypeWrapper[] bindingsForAncestor(Class ancestor, Class descendant, AbstractTypeWrapper[] descendantParameterizedTypes, boolean keepVariableNamesAncestor) {
		if(descendantParameterizedTypes != null) {
			if(ancestor.isInterface() && !descendant.isInterface()) {
				//SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(getGenericInterface(descendant, ancestor));
				//AbstractTypeWrapper[] bindingsInterface = AbstractTypeWrapper.wrap(interfaceWrapper.getParameters());
				System.out.println(descendant);
				System.out.println(ancestor);
				AbstractTypeWrapper[] bindingsInterface = getParameterizedTypesInterface(descendant, ancestor);
				AbstractTypeWrapper[] classParametersVar = AbstractTypeWrapper.wrap(descendant.getTypeParameters());
				for(int i=0; i<bindingsInterface.length; i++) {
					if(bindingsInterface[i] instanceof VariableTypeWrapper) {
						VariableTypeWrapper foundVariableType = (VariableTypeWrapper) bindingsInterface[i];
						for(int j=0; j<classParametersVar.length; j++) {
							if(foundVariableType.getName().equals(VariableTypeWrapper.class.cast(classParametersVar[j]).getName()))
								bindingsInterface[i] = descendantParameterizedTypes[j];
						}
							
					}
				}
				return bindingsInterface;
			}
			else {
				return descendantParameterizedTypes;
			}
		}
		else
			return AbstractTypeWrapper.wrap(ancestor.getTypeParameters());
	}
	*/
	
	public AbstractTypeWrapper[] findParametersInstantiations(Class ancestor, Class descendant, AbstractTypeWrapper[] descendantParameterizedTypes, boolean keepVariableNamesAncestor) {
		if(!ancestor.isAssignableFrom(descendant))
			throw new NotAncestorException(ancestor, descendant);
		if(ancestor.equals(descendant)) {
			return bindingsForAncestor(ancestor, descendant, descendantParameterizedTypes, keepVariableNamesAncestor);
		} else if(descendantReachedAncestor(descendant, ancestor)) {  //if we are here descendant is a class and ancestor an interface
			for(Class includedInterface : ReflectionUtil.includedInterfaces(descendant)) {
				if(ancestor.isAssignableFrom(includedInterface)) {
					AbstractTypeWrapper[] bindingsInterface = getParameterizedTypesInterface(descendant, includedInterface);
					AbstractTypeWrapper[] classParametersVar = AbstractTypeWrapper.wrap(descendant.getTypeParameters());
					for(int i=0; i<bindingsInterface.length; i++) {
						if(bindingsInterface[i] instanceof VariableTypeWrapper) {
							VariableTypeWrapper foundVariableType = (VariableTypeWrapper) bindingsInterface[i];
							for(int j=0; j<classParametersVar.length; j++) {
								if(foundVariableType.getName().equals(VariableTypeWrapper.class.cast(classParametersVar[j]).getName()))
									bindingsInterface[i] = descendantParameterizedTypes[j];
							}
								
						}
					}
					return findParametersInstantiations(ancestor, includedInterface, bindingsInterface, keepVariableNamesAncestor);
				}
			}
			return null;
		}
		

		Class superClass = null;
		if(!descendant.isInterface() && ancestor.isAssignableFrom(descendant.getSuperclass()))
			superClass = descendant.getSuperclass();
		else {
			Class[] includedInterfaces = null;
			
			if(!descendant.isInterface())
				includedInterfaces = ReflectionUtil.includedInterfaces(descendant);
			else
				includedInterfaces = descendant.getInterfaces();
			
			for(Class includedInterface : includedInterfaces) {
				if(ancestor.isAssignableFrom(includedInterface)) {
					superClass = includedInterface;
					break;
				}
			}
		}
		
		AbstractTypeWrapper[] superParameterizedTypes = superParameterizedTypes(descendantParameterizedTypes, descendant, superClass, keepVariableNamesAncestor);
		/*
		if(descendantReachedAncestor(descendant.getSuperclass(), ancestor)) {
			if(!ancestor.isInterface())
				return superParameterizedTypes;
			else {
				SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(getGenericInterface(descendant, ancestor));		
			}
		}
		else*/
			return findParametersInstantiations(ancestor, superClass, superParameterizedTypes, keepVariableNamesAncestor);
	}
	
	
	public AbstractTypeWrapper[] superParameterizedTypes(Class clazz, Class superClass) {
		if(!clazz.isInterface() && clazz.getSuperclass().equals(superClass))
			return AbstractTypeWrapper.wrap(new SingleTypeWrapper(clazz.getGenericSuperclass()).getParameters());
		for(Type interfaze : clazz.getGenericInterfaces()) {
			SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(interfaze);
			if(interfaceWrapper.asClass().equals(superClass))
				return AbstractTypeWrapper.wrap(interfaceWrapper.getParameters());
		}
		return null;
	}
	
	public AbstractTypeWrapper[] superParameterizedTypes(AbstractTypeWrapper[] classParameterizedTypesValues, Class clazz, Class superClass, boolean keepVariableNamesAncestor) {
		AbstractTypeWrapper[] classParameterizedTypes = AbstractTypeWrapper.wrap(clazz.getTypeParameters()); //the parameterized types in the class, as they are declared in the class file
		AbstractTypeWrapper[] superParameterizedTypes = AbstractTypeWrapper.wrap(superClass.getTypeParameters()); //the parameterized types in the super class, as they are declared in the class file
		
		AbstractTypeWrapper[] superParameterizedTypesValues = superParameterizedTypes(clazz, superClass); //the parameterized types in the super class as they are instantiated by the base class declaration

		for(int i=0; i<superParameterizedTypesValues.length; i++) {
			if(superParameterizedTypesValues[i] instanceof VariableTypeWrapper) {  //one of the super types is a variable, so probably it needs to be replaced by a value in classParameterizedTypesValues
				VariableTypeWrapper superParameterizedType = (VariableTypeWrapper) superParameterizedTypesValues[i]; //the name of the type variable corresponds to the name it was declared in clazz
				for(int j=0; j<classParameterizedTypes.length; j++) { //finding a variable type in the base class with the same name than superParameterizedType
					VariableTypeWrapper classParameterizedType = (VariableTypeWrapper) classParameterizedTypes[j];
					if(superParameterizedType.getName().equals(classParameterizedType.getName())) { //found it
						if(keepVariableNamesAncestor) { //keep the original type variable names for all the type variables that were not replaced
							if(classParameterizedTypesValues != null && !(classParameterizedTypesValues[j] instanceof VariableTypeWrapper) ) {
								superParameterizedTypesValues[i] = classParameterizedTypesValues[j];
							} else {//it was not possible to replace the type variable with a non variable value in classParameterizedTypesValues[j]
								superParameterizedTypesValues[i] = superParameterizedTypes[i]; //the variable will be replaced with another value with the same name used in the super class declaration
							}	
						} else {  //use the type variable names used by the descendant class
							if(classParameterizedTypesValues != null) {
								superParameterizedTypesValues[i] = classParameterizedTypesValues[j];
							}
						}
					break;
					}
				}
			}
		}
		
		
		/*
		if(classParameterizedTypes!=null) {
			TypeVariable[] typeVariables = clazz.getTypeParameters();
			for(int i=0; i<typeVariables.length; i++) {
				VariableTypeWrapper variableTypeWrapper  = new VariableTypeWrapper(typeVariables[i]);
				for(int j=0; j<superParameterizedTypes.length; j++) {
					if(superParameterizedTypes[j] instanceof VariableTypeWrapper) {
						VariableTypeWrapper superVariableTypeWrapper = (VariableTypeWrapper)superParameterizedTypes[j];
						if(variableTypeWrapper.getName().equals(superVariableTypeWrapper.getName())) {
							//if(! (classParameterizedTypes[i] instanceof VariableTypeWrapper) )
								superParameterizedTypes[j] = classParameterizedTypes[i];
						}
					}
					
				}
			}
		}
		*/
		
		return superParameterizedTypesValues;
	}
	
	
	public Class[] getClassesInHieararchy(Class ancestor, Class descendant) {
		List<Class> hierarchy = new ArrayList<Class>();
		
		Class currentDescendant = descendant;
		while(true) {
			hierarchy.add(0, currentDescendant);
			if(currentDescendant.equals(ancestor)) { //done, we reach the ancestor in the hierarchy
				return hierarchy.toArray(new Class[] {});
			} else if(currentDescendant.equals(Object.class)) {
					throw new NotAncestorException(ancestor, descendant);
			} else {
				currentDescendant = currentDescendant.getSuperclass();
			}
		}
	}

}
