package org.reflectiveutils.wrappertype;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import org.reflectiveutils.javatype.ParameterizedTypeImpl;


public class SingleTypeWrapper extends AbstractTypeWrapper {
	
	public SingleTypeWrapper(Type wrappedType) {
		super(wrappedType);
	}

	boolean isInterface() {
		return asClass().isInterface();
	}

	boolean isAbstract() {
		return Modifier.isAbstract(asClass().getModifiers());  //primitive type classes answer yes to this
	}

	@Override
	public boolean hasActualTypeArguments() {
		return ParameterizedType.class.isAssignableFrom(wrappedType.getClass());
	}

	@Override
	public Type[] getActualTypeArguments() {
		if(hasActualTypeArguments()) {
			return ((ParameterizedType)wrappedType).getActualTypeArguments();
		} else
			return new Type[] {};
	}

	@Override
	public boolean isGenericType() {
		return hasActualTypeArguments();
	}
	
	@Override
	public boolean hasTypeParameters() {
		return getTypeParameters().length>0;
	}
	
	@Override
	public TypeVariable[] getTypeParameters() {
		return asClass().getTypeParameters();
	}
	
	@Override
	public Class asClass() {
		if(wrappedType instanceof ParameterizedType)
			return (Class)((ParameterizedType)wrappedType).getRawType();
		else
			return (Class)wrappedType;
	}

	@Override
	public boolean isAssignableFrom(AbstractTypeWrapper type) {
		if(type instanceof VariableTypeWrapper)
			return true;
		if(!(type instanceof SingleTypeWrapper))
			return false;
		return asClass().isAssignableFrom(SingleTypeWrapper.class.cast(type).asClass());
	}

	@Override
	public void collectTypeVariables(List<Type> typeVariables) {
		for(Type typeArgument : getActualTypeArguments()) {
			AbstractTypeWrapper.wrap(typeArgument).collectTypeVariables(typeVariables);
		}
	}
	
	@Override
	public void print() {
		super.print();
		if(isInterface())
			System.out.println("Interface");
		else if(isAbstract())
			System.out.println("Abstract class");
		else
			System.out.println("Concrete class");
		System.out.println("Class: "+asClass().getName());
		if(hasActualTypeArguments())
			System.out.println("Parameters: "+getActualTypeArguments().length);
		for(int i = 0; i<getActualTypeArguments().length; i++) {
			System.out.println("Parameter: "+i+": "+getActualTypeArguments()[i].toString());
		}
	}
	
	public Type getOwnerType() {
		if(wrappedType instanceof ParameterizedType)
			return ((ParameterizedType)wrappedType).getOwnerType();
		else
			return ((Class)wrappedType).getEnclosingClass();
	}
	
	
	private boolean bindAtLeastOneTypeVariable(TypeVariable[] typeVariables, Map<TypeVariable, Type> typeVariableMap) {
		for(TypeVariable typeVariable: typeVariables) {
			if(typeVariableMap.containsKey(typeVariable))
				return true;
		}
		return false;
	}
	
	public boolean canBindTypeParameters(Map<TypeVariable, Type> typeVariableMap) {
		if(hasTypeParameters())
			return bindAtLeastOneTypeVariable(getTypeParameters(), typeVariableMap);
		return false;
	}
	/*
	@Override
	public boolean canBindTypeVariables(Map<TypeVariable, Type> typeVariableMap) {
		return hasActualTypeArguments() || canBindTypeParameters(typeVariableMap);
	}
	*/
	
	/**
	 * Bind the type variables sent in the map to any free type variables in the actual arguments list
	 * if the wrapped type is not a Parameterized Type (i.e., without actual type arguments) the method does not do anything
	 */
	@Override
	public Type bindVariables(Map<TypeVariable, Type> typeVariableMap) {
		Type boundType = null;
		if(hasActualTypeArguments() || hasTypeParameters()) {
			Type[] actualTypeArguments = null;
			if(hasActualTypeArguments())
				actualTypeArguments = bindVariables(getActualTypeArguments(), typeVariableMap);
			else if(canBindTypeParameters(typeVariableMap)) {
				actualTypeArguments = bindVariables(getTypeParameters(), typeVariableMap);
			}
			if(actualTypeArguments != null)
				boundType = new ParameterizedTypeImpl(actualTypeArguments, getOwnerType(), asClass());
		}
		if(boundType == null) {
			boundType = wrappedType;
		}
		return boundType;
	}



/*
	@Override
	public boolean isErased() {
		return false;
	}

	@Override
	public boolean isArray() {
		return false;
	}
*/
}