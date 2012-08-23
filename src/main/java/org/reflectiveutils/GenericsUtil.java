package org.reflectiveutils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.ArrayTypeWrapper;
import org.reflectiveutils.wrappertype.SingleTypeWrapper;
import org.reflectiveutils.wrappertype.VariableTypeWrapper;


public class GenericsUtil {

	/**
	 * This method unifies the variable type arguments in an ancestor type according to the types in a descendant
	 * @param ancestor An ancestor type with some type variables
	 * @param descendant A descendant type without type variables
	 * @return a map of type variables to concrete types
	 */
	public Map<TypeVariable, Type> unify(Type ancestor, Type descendant) {
		return unify(AbstractTypeWrapper.wrap(ancestor), AbstractTypeWrapper.wrap(descendant));
	}
	
	public Map<TypeVariable, Type> unify(AbstractTypeWrapper ancestor, AbstractTypeWrapper descendant) {
		if(!ancestor.isAssignableFrom(descendant)) {
			throw new NotAncestorException(ancestor, descendant);
		}
		if(ancestor instanceof ArrayTypeWrapper) {
			return unify(((ArrayTypeWrapper) ancestor).getBaseType(), ((ArrayTypeWrapper) ancestor).getBaseType());
		}
		
		Map<TypeVariable, Type> typeVariables = new HashMap<TypeVariable, Type>(); //the map to return
		
		if(!ancestor.hasActualTypeArguments())  //if there are no type arguments, there are no type variables to unify
			return typeVariables;  //return empty map
		
		SingleTypeWrapper ancestorWithoutTypeArguments = (SingleTypeWrapper)AbstractTypeWrapper.wrap(ancestor.asClass()); //type variables suppressed
		AbstractTypeWrapper[] ancestorBoundTypes = findAncestorTypeParameters(ancestorWithoutTypeArguments, (SingleTypeWrapper)descendant); //how the parameter types in the ancestor looks like when looking at the descendant types
		AbstractTypeWrapper[] ancestorUnboundTypes = AbstractTypeWrapper.wrap(ancestor.getActualTypeArguments());  //the current type arguments in the ancestor. Some type arguments should include unbound type variables
		
		for(int i=0; i<ancestorUnboundTypes.length; i++) {
			AbstractTypeWrapper ancestorUnboundType = ancestorUnboundTypes[i];
			AbstractTypeWrapper ancestorBoundType = ancestorBoundTypes[i];
			unify(ancestorUnboundType, ancestorBoundType, typeVariables);
		}

		return typeVariables;
	}

	
	/**
	 * Unify two identical types
	 * @param unboundType a type with unbound type variables
	 * @param boundType a concrete type
	 * @param typeVariables
	 */
	public void unify(AbstractTypeWrapper unboundType, AbstractTypeWrapper boundType, Map<TypeVariable, Type> typeVariables) {
		if(unboundType instanceof VariableTypeWrapper) {
			VariableTypeWrapper variableType = (VariableTypeWrapper)unboundType;
			if(!variableType.isWildcard()) {
				Type existingBoundType = typeVariables.get(variableType.getWrappedType());
				if(existingBoundType == null) { //the type variable is not present in the map.
					typeVariables.put((TypeVariable) variableType.getWrappedType(), boundType.getWrappedType()); //associating the type variable with its bound concrete type
				} else { //the type variable already existed in the map
					if(!existingBoundType.equals(boundType.getWrappedType())) //the existing value should be identical to the bound value
						throw new RuntimeException("Variable " + variableType.getWrappedType() + " is already bound to a different value. " +
								"Bound type: " + existingBoundType + ".  " +
								"New type: " + boundType.getWrappedType());
				}
			}
			return;
		} else {
			SingleTypeWrapper unboundSingleType = (SingleTypeWrapper)unboundType;
			if(unboundSingleType.hasActualTypeArguments()) { //the unbound type has type arguments
				SingleTypeWrapper boundSingleType = (SingleTypeWrapper)boundType;
				if(boundSingleType.hasActualTypeArguments()) { //the bound type also has type arguments
					AbstractTypeWrapper[] nestedUnboundTypes = AbstractTypeWrapper.wrap(unboundSingleType.getActualTypeArguments());
					AbstractTypeWrapper[] nestedBoundTypes = AbstractTypeWrapper.wrap(boundSingleType.getActualTypeArguments());
					for(int i = 0; i<nestedUnboundTypes.length; i++) {
						unify(nestedUnboundTypes[i], nestedBoundTypes[i], typeVariables);
					}
				}
			}
			
		}
	}
	
	public void unify(AbstractTypeWrapper[] unboundType, AbstractTypeWrapper[] boundType, Map<TypeVariable, Type> typeVariables) {
		assert(unboundType.length == boundType.length);
		for(int i = 0; i<unboundType.length; i++) {
			unify(unboundType[i], boundType[i], typeVariables);
		}
	}
	
	public Map<TypeVariable, Type> unify(AbstractTypeWrapper[] unboundType, AbstractTypeWrapper[] boundType) {
		Map<TypeVariable, Type> typeVariables = new HashMap<TypeVariable, Type>();
		unify(unboundType, boundType, typeVariables);
		return typeVariables;
	}

	
	
	
	
	/*
	 * Answer if a descendant reach an ancestor class or interface in a class inheritance chain
	 */
	private boolean descendantReachedAncestor(Class descendant, Class ancestor) {
		if(!descendant.isInterface()) //descendant is a class, not an interface
			return ( descendant.equals(ancestor) || (ancestor.isInterface() && ReflectionUtil.includesInterfaceInHierarchy(descendant, ancestor)) );
		else
			return descendant.equals(ancestor);
	}
	
	/*
	 * Return the type parameters of a generic interface implemented by a class
	 */
	private AbstractTypeWrapper[] getActualTypeArgumentsInterface(Class clazz, Class interfaze) {
		for(Type t : clazz.getGenericInterfaces()) {
			SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(t);
			if(interfaceWrapper.asClass().equals(interfaze)) {
				AbstractTypeWrapper[] parameterizedTypes = AbstractTypeWrapper.wrap(interfaceWrapper.getActualTypeArguments());
				return parameterizedTypes;
			} 
		}
		return null;
	}
	

	/**
	 * Resolves the right types for an ancestor class taking into consideration a boolean flag indicating how to deal with unbound type variables.
	 * If the type variable names of the ancestor should be preserved, they will be replaced in the type list. Otherwise the type list is returned untouched.
	 * @param ancestor
	 * @param descendantParameterizedTypes
	 * @param keepVariableNamesAncestor
	 * @return
	 */
	private AbstractTypeWrapper[] bindVariableTypes(Class clazz, AbstractTypeWrapper[] actualTypeArguments, boolean keepVariableNamesAncestor) {
		AbstractTypeWrapper[] boundVariableTypes;
		if(actualTypeArguments == null) { //no type list
			boundVariableTypes = AbstractTypeWrapper.wrap(clazz.getTypeParameters()); //just return the type variables of the ancestor
		}
		else {
			if(keepVariableNamesAncestor) {
				Map<TypeVariable, Type> replacementMap = new HashMap<TypeVariable, Type>();
				TypeVariable[] typeParameters = clazz.getTypeParameters();
				for(int i = 0; i<actualTypeArguments.length; i++) {
					if(actualTypeArguments[i].getWrappedType() instanceof TypeVariable)
						replacementMap.put((TypeVariable) actualTypeArguments[i].getWrappedType(), typeParameters[i]);
				}
				boundVariableTypes = bindVariableTypes(actualTypeArguments, replacementMap);
			} else {
				boundVariableTypes = actualTypeArguments;
			}
		}
		return boundVariableTypes;	
	}
	
	private Type[] bindVariableTypes(Type[] types, Map<TypeVariable, Type> replacementMap) {
		return AbstractTypeWrapper.unwrap(bindVariableTypes(AbstractTypeWrapper.wrap(types), replacementMap));
	}
	
	private AbstractTypeWrapper[] bindVariableTypes(AbstractTypeWrapper[] types, Map<TypeVariable, Type> replacementMap) {
		AbstractTypeWrapper[] boundTypes = new AbstractTypeWrapper[types.length];
		for(int i = 0; i<types.length; i++) {
			boundTypes[i] = AbstractTypeWrapper.wrap(types[i].bindVariables(replacementMap));
		}
		return boundTypes;
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
	
	/**
	 * 
	 * @param ancestor
	 * @param descendant
	 * @return a map binding to each type variable in the ancestor a concrete type given by the descendant
	 */
	public Map<TypeVariable, Type> findAncestorTypeParametersMap(Type ancestor, Type descendant) {
		Type[] typeArguments = findAncestorTypeParameters(ancestor, descendant);
		return asTypeVariableReplacementMap(ancestor, typeArguments);
	}
	
	private Map<TypeVariable, Type> asTypeVariableReplacementMap(Type type, Type[] typeArguments) {
		Map<TypeVariable, Type> typeArgumentsMap = new HashMap<TypeVariable, Type>();
		TypeVariable[] typeVariables = new SingleTypeWrapper(type).getTypeParameters();
		for(int i=0; i<typeArguments.length; i++) {
			Type typeArgument = typeArguments[i];
			typeArgumentsMap.put(typeVariables[i], typeArgument);
		}
		return typeArgumentsMap;
	}
	
	
	public Type[] findAncestorTypeParameters(Type ancestor, Type descendant) {
		return AbstractTypeWrapper.unwrap(findAncestorTypeParameters(new SingleTypeWrapper(ancestor), new SingleTypeWrapper(descendant)));
	}
	

	/*
	 * Return the parameter types of an ancestor given a descendant
	 */
	private AbstractTypeWrapper[] findAncestorTypeParameters(SingleTypeWrapper ancestorTypeWrapper, SingleTypeWrapper descendantTypeWrapper) {
		return findAncestorTypeParameters(ancestorTypeWrapper, descendantTypeWrapper, false);
	}

	public AbstractTypeWrapper[] findAncestorTypeParameters(SingleTypeWrapper ancestorTypeWrapper, SingleTypeWrapper descendantTypeWrapper, boolean keepVariableNamesAncestor) {
		AbstractTypeWrapper[] descendantParameterizedTypes = null;
		if(descendantTypeWrapper.hasActualTypeArguments()) 
			descendantParameterizedTypes = AbstractTypeWrapper.wrap(descendantTypeWrapper.getActualTypeArguments());
		return findAncestorTypeParameters(ancestorTypeWrapper.asClass(), descendantTypeWrapper.asClass(), descendantParameterizedTypes, keepVariableNamesAncestor);
	}
	
	private AbstractTypeWrapper[] findAncestorTypeParameters(Class ancestor, Class descendant, AbstractTypeWrapper[] descendantParameterizedTypes, boolean keepVariableNamesAncestor) {
		if(!ancestor.isAssignableFrom(descendant))
			throw new NotAncestorException(ancestor, descendant);
		if(ancestor.equals(descendant)) {
			return bindVariableTypes(ancestor, descendantParameterizedTypes, keepVariableNamesAncestor);
		} else if(descendantReachedAncestor(descendant, ancestor)) {  //if we are here then descendant is a class and ancestor an interface
			for(Class includedInterface : ReflectionUtil.includedInterfaces(descendant)) {
				if(ancestor.isAssignableFrom(includedInterface)) {
					AbstractTypeWrapper[] bindingsInterface = getActualTypeArgumentsInterface(descendant, includedInterface);
					
					
					Map<TypeVariable, Type> replacementMap = asTypeVariableReplacementMap(descendant, AbstractTypeWrapper.unwrap(descendantParameterizedTypes));
					bindingsInterface = bindVariableTypes(bindingsInterface, replacementMap);
					
					
					
					/*
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
					*/
					return findAncestorTypeParameters(ancestor, includedInterface, bindingsInterface, keepVariableNamesAncestor);
				}
			}
			throw new RuntimeException(); //we should never arrive here
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
		
		AbstractTypeWrapper[] superParameterizedTypes = superParameterizedTypes(descendantParameterizedTypes, descendant, superClass);
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
	
	private AbstractTypeWrapper[] superParameterizedTypes(AbstractTypeWrapper[] classParameterizedTypesValues, Class clazz, Class superClass) {
		AbstractTypeWrapper[] superActualTypeArguments = superActualTypeArguments(clazz, superClass);
		if(classParameterizedTypesValues == null)
			return superActualTypeArguments;
		Map<TypeVariable, Type> replacementMap = asTypeVariableReplacementMap(clazz, AbstractTypeWrapper.unwrap(classParameterizedTypesValues));
		return bindVariableTypes(superActualTypeArguments, replacementMap);
		
		/*
		AbstractTypeWrapper[] classParameterizedTypes = AbstractTypeWrapper.wrap(clazz.getTypeParameters()); //the parameterized types in the class, as they are declared in the class file
		AbstractTypeWrapper[] superParameterizedTypes = AbstractTypeWrapper.wrap(superClass.getTypeParameters()); //the parameterized types in the super class, as they are declared in the class file
		
		AbstractTypeWrapper[] superActualTypeArguments = superActualTypeArguments(clazz, superClass); //the parameterized types in the super class as they are instantiated by the base class declaration

		for(int i=0; i<superActualTypeArguments.length; i++) {
			if(superActualTypeArguments[i] instanceof VariableTypeWrapper) {  //one of the super types is a variable, so probably it needs to be replaced by a value in classParameterizedTypesValues
				VariableTypeWrapper superParameterizedType = (VariableTypeWrapper) superActualTypeArguments[i]; //the name of the type variable corresponds to the name it was declared in clazz
				for(int j=0; j<classParameterizedTypes.length; j++) { //finding a variable type in the base class with the same name than superParameterizedType
					VariableTypeWrapper classParameterizedType = (VariableTypeWrapper) classParameterizedTypes[j];
					if(superParameterizedType.getName().equals(classParameterizedType.getName())) { //found it
						if(classParameterizedTypesValues != null) {
							superActualTypeArguments[i] = classParameterizedTypesValues[j];
						}
						if(superActualTypeArguments[i] instanceof VariableTypeWrapper && !keepVariableNamesAncestor) {
							superActualTypeArguments[i] = superParameterizedTypes[i]; //the variable will be replaced with another value with the same name used in the super class declaration
						}
						break;
					}
				}
			}
			
		}
		return superActualTypeArguments;
		*/
	}
	
	private AbstractTypeWrapper[] superActualTypeArguments(Class clazz, Class superClass) {
		if(!clazz.isInterface() && clazz.getSuperclass().equals(superClass))
			return AbstractTypeWrapper.wrap(new SingleTypeWrapper(clazz.getGenericSuperclass()).getActualTypeArguments());
		for(Type interfaze : clazz.getGenericInterfaces()) {
			SingleTypeWrapper interfaceWrapper = new SingleTypeWrapper(interfaze);
			if(interfaceWrapper.asClass().equals(superClass))
				return AbstractTypeWrapper.wrap(interfaceWrapper.getActualTypeArguments());
		}
		return null;
	}


	/*
	 * Return the parameter types of an descendant given a ancestor
	 */
	public Type[] findDescendantTypeParameters(Type ancestor, Type descendant) {
		return AbstractTypeWrapper.unwrap(findDescendantTypeParameters(new SingleTypeWrapper(ancestor), new SingleTypeWrapper(descendant)));
	}
	

	public AbstractTypeWrapper[] findDescendantTypeParameters(SingleTypeWrapper ancestorTypeWrapper, SingleTypeWrapper descendantTypeWrapper) {
		SingleTypeWrapper ancestorWithoutParameters = new SingleTypeWrapper(ancestorTypeWrapper.asClass());
		AbstractTypeWrapper[] ancestorWithDescendantTypes = findAncestorTypeParameters(ancestorWithoutParameters, descendantTypeWrapper, false);

		//if(!ancestorTypeWrapper.hasTypeParameters())
			//throw new RuntimeException("Calling findDescendantTypeParameters with an ancestor type without parameters");
		//Type[] ancestorTypes = ancestorTypeWrapper.getTypeParameters();
		
		/*if(!ancestorTypeWrapper.hasActualTypeArguments())
			throw new RuntimeException("Calling findDescendantTypeParameters with an ancestor type without actual arguments");*/
		Type[] ancestorActualTypeArguments = ancestorTypeWrapper.getActualTypeArguments();
		
		
		
		Map<TypeVariable, Type> typeVariables = unify(ancestorWithDescendantTypes, AbstractTypeWrapper.wrap(ancestorActualTypeArguments));
		Type boundDescendantType = descendantTypeWrapper.bindVariables(typeVariables);
		return AbstractTypeWrapper.wrap(new SingleTypeWrapper(boundDescendantType).getActualTypeArguments());
		
		
		
		/*
		if(!descendantTypeWrapper.hasTypeParameters())
			throw new RuntimeException("Calling findDescendantTypeParameters with a descendant type without parameters");
		TypeVariable[] descendantTypes = descendantTypeWrapper.getTypeParameters();
		
		
		Type[] descendantTypesResult = new Type[descendantTypes.length];
		for(int i = 0; i<descendantTypes.length; i++) 
			descendantTypesResult[i] = descendantTypes[i];
		
		
		
		
		for(int i=0; i<ancestorWithDescendantTypes.length; i++) {
			if(ancestorWithDescendantTypes[i] instanceof VariableTypeWrapper) {
				VariableTypeWrapper matchedVariableTypeWrapper = (VariableTypeWrapper)ancestorWithDescendantTypes[i];
				for(int j=0; j<descendantTypes.length; j++) {
					AbstractTypeWrapper descendantType = AbstractTypeWrapper.wrap(descendantTypes[j]);
					if(  (descendantType instanceof VariableTypeWrapper)  &&  VariableTypeWrapper.class.cast(descendantType).getName().equals(matchedVariableTypeWrapper.getName()) ) {
						if(!(ancestorActualTypeArguments.length==0))
							descendantTypesResult[j] = ancestorActualTypeArguments[i];
					}
				}
			}
		}
		return AbstractTypeWrapper.wrap(descendantTypesResult);
		*/
	}

}
