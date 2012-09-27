package org.reflectiveutils.wrappertype;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.logicobjects.annotation.LObject;
import org.reflectiveutils.ReflectionUtil;

/*
 * The objective of this class is to reduce the amount of castings and instanceof operations that otherwise classes dealing with Java type classes will have to do
 * 
 */
public abstract class AbstractTypeWrapper {
	
	protected Type wrappedType;
	
	public AbstractTypeWrapper(Type wrappedType) {
		setWrappedType(wrappedType);
	}
	
	public Type getWrappedType() {
		return wrappedType;
	}
	
	public void setWrappedType(Type wrappedType) {
		this.wrappedType = wrappedType;
	}
	
	public boolean isAssignableFrom(Type type) {
		return isAssignableFrom(AbstractTypeWrapper.wrap(type));
	}
	
	public abstract Class asClass();
	public abstract boolean isGenericType();
	public abstract boolean hasTypeParameters();
	public abstract TypeVariable[] getTypeParameters();
	public abstract Type[] getActualTypeArguments();
	public abstract boolean hasActualTypeArguments();
	public abstract boolean isAssignableFrom(AbstractTypeWrapper type);
	//public abstract boolean canBindTypeVariables(Map<TypeVariable, Type> typeVariableMap);
	
	public boolean isPrimitive() {return asClass().isPrimitive();}
	
	public boolean isMemberClass() {return asClass().isMemberClass(); }
	
	public Class getEnclosingClass() {return asClass().getEnclosingClass(); }
	/**
	 * 
	 * @param length
	 * @return an array of the wrapped type
	 * the component type of the returned array is given by the class representation of the wrapped type
	 * This implies that for Variable Type the returned array will be Object[] and not VariableType[]
	 * This is because the class representation (in the current implementation) of variable types is Object
	 * Then the component type of an array of the wrapped type should be consistent with this class representation
	 */
	public Object[] asArray(int length) {
		return (Object[]) Array.newInstance(asClass(), length);
	}
	

	/**
	 * Collects all the type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are also included.
	 * @param types is the list collection the found type variable.
	 */
	protected abstract void collectTypeVariables(List<Type> types);
	
	/**
	 * @return all the type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are also included.
	 */
	public List<Type> getTypeVariables() {
		List<Type> typeVariables = new ArrayList<Type>();
		collectTypeVariables(typeVariables);
		return typeVariables;
	}
	
	/**
	 * @return the named type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are NOT included.
	 */
	public List<TypeVariable> getNamedTypeVariables() {
		List<TypeVariable> namedTypeVariables = new ArrayList<TypeVariable>();
		for(Type typeVariable : getTypeVariables()) {
			if(typeVariable instanceof TypeVariable)
				namedTypeVariables.add((TypeVariable) typeVariable);
		}
		return namedTypeVariables;
	}
	
	/**
	 *  
	 * @return a boolean indicating if the type has named type variables
	 */
	public boolean hasNamedTypeVariables() {
		return !getNamedTypeVariables().isEmpty();
	}
	
	/**
	 * 
	 * @param typeVariableMap is a map containing mappings from type variables to concrete types
	 * @return an equivalent type to the wrapped time, with the difference that all its named type variables have been substituted by types given by a map
	 */
	public abstract Type bindVariables(Map<TypeVariable, Type> typeVariableMap);
	
	public static Type bindVariables(Type type, Map<TypeVariable, Type> typeVariableMap) {
		return AbstractTypeWrapper.wrap(type).bindVariables(typeVariableMap);
	}
	
	public static Type[] bindVariables(Type[] types, Map<TypeVariable, Type> typeVariableMap) {
		Type[] boundTypes = new Type[types.length];
		for(int i=0; i<boundTypes.length; i++)
			boundTypes[i] = bindVariables(types[i], typeVariableMap);
		return boundTypes;
	}

	@Override
	public String toString() {
		return "("+getClass().getSimpleName()+")" + getWrappedType().toString();
	}
	
	public void print() {
		System.out.println(toString());
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractTypeWrapper))
			return false;
		AbstractTypeWrapper that = (AbstractTypeWrapper) obj;
		return this.getWrappedType().equals(that.getWrappedType());
	}
	
	public static AbstractTypeWrapper wrap(Type type) {
		if(ParameterizedType.class.isAssignableFrom(type.getClass()) || (Class.class.isAssignableFrom(type.getClass()) && !((Class)type).isArray()) )
			return new SingleTypeWrapper(type);
		else if(ArrayTypeWrapper.isArray(type))
			return new ArrayTypeWrapper(type);
		else
			return new VariableTypeWrapper(type);
	}
	
	public static AbstractTypeWrapper[] wrap(Type[] types) {
		AbstractTypeWrapper[] typeWrappers = new AbstractTypeWrapper[types.length];
		for(int i=0; i<types.length; i++) {
			typeWrappers[i] = wrap(types[i]);
		}
		return typeWrappers;
	}
	
	public static Type[] unwrap(AbstractTypeWrapper[] typeWrappers) {
		Type[] types = new Type[typeWrappers.length];
		for(int i=0; i<typeWrappers.length; i++) {
			types[i] = typeWrappers[i].getWrappedType();
		}
		return types;
	}
	

}
