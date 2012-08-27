package org.reflectiveutils.wrappertype;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;


public class VariableTypeWrapper extends AbstractTypeWrapper {

	public VariableTypeWrapper(Type wrappedType) {
		super(wrappedType);
	}

	@Override
	public boolean hasTypeParameters() {
		//if(true) throw new UnsupportedOperationException();
		return false;
	}
	
	@Override
	public TypeVariable[] getTypeParameters() {
		//if(true) throw new UnsupportedOperationException();
		return new TypeVariable[] {};
	}

	@Override
	public Type[] getActualTypeArguments() {
		//if(true) throw new UnsupportedOperationException();
		return new Type[] {};
	}
	
	@Override
	public boolean hasActualTypeArguments() {
		//if(true) throw new UnsupportedOperationException();
		return false;
	}
/*
	@Override
	public boolean isErased() {
		return true;
	}
	
	@Override
	public Type[] getParameters() {
		if(true) throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public boolean isArray() {
		if(true) throw new UnsupportedOperationException();
		return false;
	}
*/



	@Override
	public boolean isAssignableFrom(AbstractTypeWrapper type) {
		return true;
	}
	
	/*
	 * Answers if wrappedType is an instanceof WildcardType
	 */
	public boolean isWildcard() {
		return wrappedType instanceof WildcardType;
	}

	@Override
	public Class asClass() {
		return Object.class;
	}
	
	public String getName() {
		if(isWildcard())
			return "?"; //no name	
		else
			return ((TypeVariable)wrappedType).getName();
	}



	@Override
	public void print() {
		super.print();
		System.out.println("Name: "+getName());
		
	}

	@Override
	public void collectTypeVariables(List<Type> typeVariables) {
		if(!typeVariables.contains(wrappedType))
			typeVariables.add(wrappedType);
	}
/*
	@Override
	public boolean canBindTypeParameters(Map<TypeVariable, Type> typeVariableMap) {
		return typeVariableMap.get(wrappedType) != null;
	}
*/
	
	@Override
	public Type bindVariables(Map<TypeVariable, Type> typeVariableMap) {
		Type mappedType = typeVariableMap.get(wrappedType);
		if(mappedType != null)
			return mappedType;
		else
			return wrappedType;
	}
}