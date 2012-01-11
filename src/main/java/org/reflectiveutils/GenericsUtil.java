package org.reflectiveutils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import org.reflectiveutils.AbstractTypeWrapper.SingleTypeWrapper;
import org.reflectiveutils.AbstractTypeWrapper.VariableTypeWrapper;


public class GenericsUtil {

	/*
	 * Answer if a descendant reach an ancestor class or interface in a class inheritance chain
	 */
	private boolean descendantReachedAncestor(Class descendant, Class ancestor) {
		if(!descendant.isInterface())
			return ( descendant.equals(ancestor) || (ancestor.isInterface() && ReflectionUtil.includesInterfaceInHierarchy(descendant, ancestor)) );
		else
			return descendant.equals(ancestor);
	}
	
	/*
	 * Return the type parameters of a generic interface implemented by a class
	 */
	private AbstractTypeWrapper[] getTypeParametersInterface(Class clazz, Class interfaze) {
		for(Type t : clazz.getGenericInterfaces()) {
			SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(t);
			if(interfaceWrapper.asClass().equals(interfaze)) {
				AbstractTypeWrapper[] parameterizedTypes = AbstractTypeWrapper.wrap(interfaceWrapper.getActualTypeArguments());
				return parameterizedTypes;
			} 
		}
		return null;
	}
	

	
	private AbstractTypeWrapper[] bindingsForAncestor(Class ancestor, Class descendant, AbstractTypeWrapper[] descendantParameterizedTypes, boolean keepVariableNamesAncestor) {
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
	
	
	public Type[] findAncestorTypeParameters(Type ancestor, Type descendant) {
		return AbstractTypeWrapper.unwrap(findAncestorTypeParameters(new SingleTypeWrapper(ancestor), new SingleTypeWrapper(descendant)));
	}
	
	
	/*
	 * Return the parameter types of an ancestor given a descendant
	 */
	private AbstractTypeWrapper[] findAncestorTypeParameters(SingleTypeWrapper ancestorTypeWrapper, SingleTypeWrapper descendantTypeWrapper) {
		return findAncestorTypeParameters(ancestorTypeWrapper, descendantTypeWrapper, false);
	}
	
	
	
	
	
	
	public Type[] findDescendantTypeParameters(Type ancestor, Type descendant) {
		return AbstractTypeWrapper.unwrap(findDescendantTypeParameters(new SingleTypeWrapper(ancestor), new SingleTypeWrapper(descendant)));
	}
	
	/*
	 * Return the parameter types of an descendant given a ancestor
	 */
	public AbstractTypeWrapper[] findDescendantTypeParameters(SingleTypeWrapper ancestorTypeWrapper, SingleTypeWrapper descendantTypeWrapper) {
		if(!ancestorTypeWrapper.hasTypeParameters())
			throw new RuntimeException("Calling findDescendantTypeParameters with an ancestor type without parameters");
		Type[] ancestorTypes = ancestorTypeWrapper.getTypeParameters();
		
		/*if(!ancestorTypeWrapper.hasActualTypeArguments())
			throw new RuntimeException("Calling findDescendantTypeParameters with an ancestor type without actual arguments");*/
		Type[] ancestorActualArguments = ancestorTypeWrapper.getActualTypeArguments();
		
		if(!descendantTypeWrapper.hasTypeParameters())
			throw new RuntimeException("Calling findDescendantTypeParameters with a descendant type without parameters");
		TypeVariable[] descendantTypes = descendantTypeWrapper.getTypeParameters();
		
		
		Type[] descendantTypesResult = new Type[descendantTypes.length];
		for(int i = 0; i<descendantTypes.length; i++) 
			descendantTypesResult[i] = descendantTypes[i];
		
		
		SingleTypeWrapper ancestorWithoutParameters = new SingleTypeWrapper(ancestorTypeWrapper.asClass());
		AbstractTypeWrapper[] ancestorWithDescendantTypes = findAncestorTypeParameters(ancestorWithoutParameters, descendantTypeWrapper, true);
		
		for(int i=0; i<ancestorWithDescendantTypes.length; i++) {
			if(ancestorWithDescendantTypes[i] instanceof VariableTypeWrapper) {
				VariableTypeWrapper matchedVariableTypeWrapper = (VariableTypeWrapper)ancestorWithDescendantTypes[i];
				for(int j=0; j<descendantTypes.length; j++) {
					AbstractTypeWrapper descendantType = AbstractTypeWrapper.wrap(descendantTypes[j]);
					if(  (descendantType instanceof VariableTypeWrapper)  &&  VariableTypeWrapper.class.cast(descendantType).getName().equals(matchedVariableTypeWrapper.getName()) ) {
						if(!(ancestorActualArguments.length==0))
							descendantTypesResult[j] = ancestorActualArguments[i];
					}
				}
			}
		}
		return AbstractTypeWrapper.wrap(descendantTypesResult);
	}

	public AbstractTypeWrapper[] findAncestorTypeParameters(SingleTypeWrapper ancestorTypeWrapper, SingleTypeWrapper descendantTypeWrapper, boolean keepVariableNamesAncestor) {
		if(descendantTypeWrapper.hasActualTypeArguments()) {
			return findAncestorTypeParameters(ancestorTypeWrapper.asClass(), descendantTypeWrapper.asClass(), AbstractTypeWrapper.wrap(descendantTypeWrapper.getActualTypeArguments()), keepVariableNamesAncestor);
		} else {
			return findAncestorTypeParameters(ancestorTypeWrapper.asClass(), descendantTypeWrapper.asClass(), null, keepVariableNamesAncestor);
		}
	}
	
	private AbstractTypeWrapper[] findAncestorTypeParameters(Class ancestor, Class descendant, AbstractTypeWrapper[] descendantParameterizedTypes, boolean keepVariableNamesAncestor) {
		if(!ancestor.isAssignableFrom(descendant))
			throw new NotAncestorException(ancestor, descendant);
		if(ancestor.equals(descendant)) {
			return bindingsForAncestor(ancestor, descendant, descendantParameterizedTypes, keepVariableNamesAncestor);
		} else if(descendantReachedAncestor(descendant, ancestor)) {  //if we are here then descendant is a class and ancestor an interface
			for(Class includedInterface : ReflectionUtil.includedInterfaces(descendant)) {
				if(ancestor.isAssignableFrom(includedInterface)) {
					AbstractTypeWrapper[] bindingsInterface = getTypeParametersInterface(descendant, includedInterface);
					AbstractTypeWrapper[] classParametersVar = AbstractTypeWrapper.wrap(descendant.getTypeParameters());
					for(int i=0; i<bindingsInterface.length; i++) {
						if(bindingsInterface[i] instanceof VariableTypeWrapper) {
							VariableTypeWrapper foundVariableType = (VariableTypeWrapper) bindingsInterface[i];
							for(int j=0; j<classParametersVar.length; j++) {
								if(foundVariableType.getName().equals(VariableTypeWrapper.class.cast(classParametersVar[j]).getName()) && descendantParameterizedTypes != null) {
									if(!(descendantParameterizedTypes[j] instanceof VariableTypeWrapper) || keepVariableNamesAncestor)
										bindingsInterface[i] = descendantParameterizedTypes[j];
								}
							}
						}
					}
					return findAncestorTypeParameters(ancestor, includedInterface, bindingsInterface, keepVariableNamesAncestor);
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
			return findAncestorTypeParameters(ancestor, superClass, superParameterizedTypes, keepVariableNamesAncestor);
	}
	
	private AbstractTypeWrapper[] superParameterizedTypes(AbstractTypeWrapper[] classParameterizedTypesValues, Class clazz, Class superClass, boolean keepVariableNamesAncestor) {
		AbstractTypeWrapper[] classParameterizedTypes = AbstractTypeWrapper.wrap(clazz.getTypeParameters()); //the parameterized types in the class, as they are declared in the class file
		AbstractTypeWrapper[] superParameterizedTypes = AbstractTypeWrapper.wrap(superClass.getTypeParameters()); //the parameterized types in the super class, as they are declared in the class file
		
		AbstractTypeWrapper[] superParameterizedTypesValues = superParameterizedTypes(clazz, superClass); //the parameterized types in the super class as they are instantiated by the base class declaration

		for(int i=0; i<superParameterizedTypesValues.length; i++) {
			if(superParameterizedTypesValues[i] instanceof VariableTypeWrapper) {  //one of the super types is a variable, so probably it needs to be replaced by a value in classParameterizedTypesValues
				VariableTypeWrapper superParameterizedType = (VariableTypeWrapper) superParameterizedTypesValues[i]; //the name of the type variable corresponds to the name it was declared in clazz
				for(int j=0; j<classParameterizedTypes.length; j++) { //finding a variable type in the base class with the same name than superParameterizedType
					VariableTypeWrapper classParameterizedType = (VariableTypeWrapper) classParameterizedTypes[j];
					if(superParameterizedType.getName().equals(classParameterizedType.getName())) { //found it
						if(classParameterizedTypesValues != null) {
							superParameterizedTypesValues[i] = classParameterizedTypesValues[j];
						}
						if(superParameterizedTypesValues[i] instanceof VariableTypeWrapper && !keepVariableNamesAncestor) {
							superParameterizedTypesValues[i] = superParameterizedTypes[i]; //the variable will be replaced with another value with the same name used in the super class declaration
						}
						break;
					}
				}
			}
		}
		return superParameterizedTypesValues;
	}
	
	private AbstractTypeWrapper[] superParameterizedTypes(Class clazz, Class superClass) {
		if(!clazz.isInterface() && clazz.getSuperclass().equals(superClass))
			return AbstractTypeWrapper.wrap(new SingleTypeWrapper(clazz.getGenericSuperclass()).getActualTypeArguments());
		for(Type interfaze : clazz.getGenericInterfaces()) {
			SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(interfaze);
			if(interfaceWrapper.asClass().equals(superClass))
				return AbstractTypeWrapper.wrap(interfaceWrapper.getActualTypeArguments());
		}
		return null;
	}
	
	

	
	


}
