package org.reflectiveutils.wrappertype;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import org.reflectiveutils.javatype.GenericArrayTypeImpl;



public class ArrayTypeWrapper extends AbstractTypeWrapper {

	public ArrayTypeWrapper(Type wrappedType) {
		super(wrappedType);
	}
	
	public static boolean isArray(Type type) {
		return ( GenericArrayType.class.isAssignableFrom(type.getClass()) || (Class.class.isAssignableFrom(type.getClass()) && ((Class)type).isArray()) );
	}
	
	@Override
	public boolean hasTypeParameters() {
		return AbstractTypeWrapper.wrap(getBaseType()).hasTypeParameters();
	}

	@Override
	public TypeVariable[] getTypeParameters() {
		return AbstractTypeWrapper.wrap(getBaseType()).getTypeParameters();
	}
	
	@Override
	public Type[] getActualTypeArguments() {
		return AbstractTypeWrapper.wrap(getBaseType()).getActualTypeArguments();
	}
	
	@Override
	public boolean hasActualTypeArguments() {
		return AbstractTypeWrapper.wrap(getBaseType()).hasActualTypeArguments();
	}
/*
	@Override
	public Type[] getParameters() {
		if(isParameterized()) {
			if( ((GenericArrayType)wrappedType).getGenericComponentType() instanceof ParameterizedType) 
				return ((ParameterizedType)((GenericArrayType)wrappedType).getGenericComponentType()).getActualTypeArguments();
			else
				return new Type[] {((GenericArrayType)wrappedType).getGenericComponentType()};
		} else
		return new Type[] {};
	}
*/
	//if the base type is a parameterized type, the array type will be a GenericArrayType
	//this is also true for all the nested array types in a multidimensional array
	public Type getComponentType() {
		if(wrappedType instanceof GenericArrayType) {
			return ((GenericArrayType)wrappedType).getGenericComponentType();
		} else {
			return ((Class)wrappedType).getComponentType();
		}
	}
/*
	@Override
	public boolean isErased() {
		return false;
	}

	@Override
	public boolean isArray() {
		return true;
	}
*/
	
	public int dimensions() {
		int componentDimension = 0;
		if(isArray(getComponentType())) {
			componentDimension = new ArrayTypeWrapper(getComponentType()).dimensions();
		}
		return 1 + componentDimension;
	}
	
	public Type getBaseType() {
		if(isArray(getComponentType()))
			return (new ArrayTypeWrapper(getComponentType())).getBaseType();
		else
			return getComponentType();		
	}


	@Override
	public boolean isAssignableFrom(AbstractTypeWrapper type) {
		if(type instanceof VariableTypeWrapper)
			return true;
		if( !(type instanceof ArrayTypeWrapper) )
			return false;
		if(!(dimensions() == ArrayTypeWrapper.class.cast(type).dimensions()) )
			return false;
		return AbstractTypeWrapper.wrap(getBaseType()).isAssignableFrom(ArrayTypeWrapper.class.cast(type).getBaseType());
	}

	@Override
	public Class asClass() {
		if(!hasActualTypeArguments())
			return (Class) wrappedType;
		else {
			Class componentClass = AbstractTypeWrapper.wrap(getComponentType()).asClass();
			return Array.newInstance(componentClass, 0).getClass();
		}
	}

	@Override
	public void print() {
		super.print();
		System.out.println("Class: "+asClass().getName());
		System.out.println("Dimensions: "+dimensions());
		if(hasActualTypeArguments())
			System.out.println("Parameterized array");
		System.out.println("Base type: "+getBaseType().toString());
	}

	@Override
	public void collectTypeVariables(List<Type> typeVariables) {
		AbstractTypeWrapper.wrap(getBaseType()).collectTypeVariables(typeVariables);
	}

	@Override
	public Type bindVariables(Map<TypeVariable, Type> typeVariableMap) {
		Type boundType;
		Type unboundComponentType = getComponentType();
		AbstractTypeWrapper wrappedComponentType = AbstractTypeWrapper.wrap(unboundComponentType);
		Type boundComponentType = wrappedComponentType.bindVariables(typeVariableMap);
		if(unboundComponentType.equals(boundComponentType))
			boundType = wrappedType;
		else
			boundType = new GenericArrayTypeImpl(boundComponentType);
		
		/*
		if(hasActualTypeArguments()) {
			AbstractTypeWrapper wrappedComponentType = AbstractTypeWrapper.wrap(getComponentType());
			Type componentType = wrappedComponentType.bindVariables(typeVariableMap);
			boundType = new GenericArrayTypeImpl(componentType);
		} else {
			boundType = wrappedType;
		}
		*/
		return boundType;
	}



}
