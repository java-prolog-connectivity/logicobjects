package org.logicobjects.util.javassist;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.bytecode.SignatureAttribute.BaseType;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.NestedClassType;
import javassist.bytecode.SignatureAttribute.ObjectType;
import javassist.bytecode.SignatureAttribute.TypeArgument;
import javassist.bytecode.SignatureAttribute.TypeVariable;

import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.ArrayTypeWrapper;
import org.reflectiveutils.wrappertype.SingleTypeWrapper;
import org.reflectiveutils.wrappertype.VariableTypeWrapper;

public class JavassistTypeUtil {

	
	/////////////////instantiating a Javassist TypeParameter
	
	public static javassist.bytecode.SignatureAttribute.TypeParameter asJavassistTypeParameter(String name, Type superClass, Type[] superInterfaces) {
		ObjectType objectTypeSuperClass = null;
		if(superClass != null)
			objectTypeSuperClass = (ObjectType) asJavassistType(superClass);
		ObjectType[] objectTypesSuperInterfaces = null;
		if(superInterfaces != null)
			objectTypesSuperInterfaces = Arrays.asList(asJavassistTypes(superInterfaces)).toArray(new ObjectType[] {});
		return new javassist.bytecode.SignatureAttribute.TypeParameter(name, objectTypeSuperClass, objectTypesSuperInterfaces);
	}
	
	public static javassist.bytecode.SignatureAttribute.TypeParameter[] asJavassistTypeParameters(String[] names) {
		List<javassist.bytecode.SignatureAttribute.TypeParameter> typeParameters = new ArrayList<>();
		for(String name : names) {
			typeParameters.add(new javassist.bytecode.SignatureAttribute.TypeParameter(name));
		}
		return typeParameters.toArray(new javassist.bytecode.SignatureAttribute.TypeParameter[] {});
	}
	
	
	/////////////////instantiating a Javassist TypeVariable
	
	public static TypeVariable asJavassistTypeVariable(Type type) {
		return asJavassistTypeVariable(AbstractTypeWrapper.wrap(type));
	}
	
	public static TypeVariable asJavassistTypeVariable(AbstractTypeWrapper typeWrapper) {
		VariableTypeWrapper variableTypeWrapper = (VariableTypeWrapper) typeWrapper;
		return new TypeVariable(variableTypeWrapper.getName());
	}
	
	public static TypeVariable[] asJavassistTypeVariables(String[] typesNames) {
		List<TypeVariable> typeVariables = new ArrayList<>();
		for(String s: typesNames) {
			typeVariables.add(new TypeVariable(s));
		}
		return typeVariables.toArray(new TypeVariable[]{});
	}
	
	
	/////////////////instantiating a Javassist TypeArgument
	
	public static TypeArgument asJavassistTypeArgument(Type type) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		if(typeWrapper instanceof VariableTypeWrapper && VariableTypeWrapper.class.cast(typeWrapper).isWildcard())
			return new TypeArgument();
		else
			return new TypeArgument((ObjectType) asJavassistType(type));
	}
	
	public static TypeArgument[] asJavassistTypeArguments(Type[] types) {
		List<TypeArgument> typeArguments = new ArrayList<>();
		for(Type type: types) {
			typeArguments.add(asJavassistTypeArgument(type));
		}
		return typeArguments.toArray(new TypeArgument[]{});
	}
	

	/////////////////instantiating a Javassist BaseType
	
	public static BaseType asJavassistBaseType(Type type) {
		return asJavassistBaseType(AbstractTypeWrapper.wrap(type));
	}
	
	public static BaseType asJavassistBaseType(AbstractTypeWrapper typeWrapper) {
		return new BaseType(typeWrapper.asClass().getCanonicalName());
	}
	
	
	/////////////////instantiating a Javassist ClassType
	
	public static ClassType asJavassistClassType(Type type) {
		return asJavassistClassType(AbstractTypeWrapper.wrap(type));
	}
	
	public static ClassType asJavassistClassType(AbstractTypeWrapper typeWrapper) {
		TypeArgument[] typeArguments = null;
		if(typeWrapper.getActualTypeArguments().length > 0)
			typeArguments = asJavassistTypeArguments(typeWrapper.getActualTypeArguments());
		if(typeWrapper.isMemberClass()) {
			ClassType parent = (ClassType) asJavassistType(typeWrapper.getEnclosingClass());
				return new NestedClassType(parent, typeWrapper.asClass().getCanonicalName(), typeArguments);
		} else {
				return new ClassType(typeWrapper.asClass().getCanonicalName(), typeArguments); //REMEMBER: typeArguments cannot be an empty list, if there are no arguments it should be null instead !! (learned the hard way ... )
		}
	}
	
	
	/////////////////instantiating a Javassist TypeVariable
	
	public static javassist.bytecode.SignatureAttribute.ArrayType asJavassistArrayType(Type type) {
		return asJavassistArrayType(AbstractTypeWrapper.wrap(type));
	}
	
	public static javassist.bytecode.SignatureAttribute.ArrayType asJavassistArrayType(AbstractTypeWrapper typeWrapper) {
		ArrayTypeWrapper arrayTypeWrapper = (ArrayTypeWrapper)typeWrapper;
		return new javassist.bytecode.SignatureAttribute.ArrayType(arrayTypeWrapper.getDimension(), asJavassistType(arrayTypeWrapper.getBaseType()));
	}
	
	
	/////////////////instantiating a Javassist Type
	
	public static javassist.bytecode.SignatureAttribute.Type asJavassistType(Type type) {
		return asJavassistType(AbstractTypeWrapper.wrap(type));
	}
	
	public static javassist.bytecode.SignatureAttribute.Type asJavassistType(AbstractTypeWrapper typeWrapper) {
		javassist.bytecode.SignatureAttribute.Type javassistType = null;
		if(typeWrapper.isPrimitive()) {
			javassistType = asJavassistBaseType(typeWrapper);
		} else if(typeWrapper instanceof SingleTypeWrapper) {
			javassistType = asJavassistClassType(typeWrapper);
		} else if(typeWrapper instanceof ArrayTypeWrapper) {
			javassistType = asJavassistArrayType(typeWrapper);
		} else if(typeWrapper instanceof VariableTypeWrapper) {
			javassistType = asJavassistTypeVariable(typeWrapper);
		}
		return javassistType;
	}
	
	
	public static javassist.bytecode.SignatureAttribute.Type[] asJavassistTypes(Type[] types) {
		List<javassist.bytecode.SignatureAttribute.Type> javassistTypes = new ArrayList<>();
		for(Type t: types) {
			javassistTypes.add(asJavassistType(t));
		}
		return javassistTypes.toArray(new javassist.bytecode.SignatureAttribute.Type[]{});
	}
}
