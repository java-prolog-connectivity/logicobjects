package org.logicobjects.util.javassist;

import java.lang.reflect.Type;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.SignatureAttribute.BaseType;
import javassist.bytecode.SignatureAttribute.MethodSignature;
import javassist.bytecode.SignatureAttribute.ObjectType;

import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

public class CodeGenerationUtil {

	public static CtField createField(CtClass ctFieldClass, Type fieldType, String fieldName, CtClass ctDeclaringClass) {
		AbstractTypeWrapper wrappedType = AbstractTypeWrapper.wrap(fieldType);
		try {
			CtField ctField = new CtField(ctFieldClass, fieldName, ctDeclaringClass);
			if(wrappedType.isGenericType()) {
				javassist.bytecode.SignatureAttribute.ObjectType javassistFieldType = (ObjectType) JavassistTypeUtil.asJavassistType(fieldType);
				ctField.setGenericSignature(javassistFieldType.encode());
			}
			
			ctDeclaringClass.addField(ctField);
			return ctField;
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static CtMethod createGetter(Type propertyType, String propertyName, CtClass ctClass) {
		CtMethod ctMethod = null; 
		AbstractTypeWrapper wrappedType = AbstractTypeWrapper.wrap(propertyType);
		String methodName = ReflectionUtil.getterName(propertyName, wrappedType.asClass());
		String returnType = wrappedType.asClass().getCanonicalName();
		try {
			String code = "public " + returnType + " " + methodName + "() { return " +propertyName+"; }";
			ctMethod = CtNewMethod.make(code, ctClass);
			
			if(wrappedType.isGenericType()) {
				javassist.bytecode.SignatureAttribute.Type javassistReturnType = JavassistTypeUtil.asJavassistType(wrappedType);
				MethodSignature methodSignature = new MethodSignature(
						null,
						null,
						javassistReturnType,
						null
					);
				String encodedSignature = methodSignature.encode();
				ctMethod.setGenericSignature(encodedSignature);
			}
			ctClass.addMethod(ctMethod);
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}

		JavassistUtil.makeNonAbstract(ctMethod);
		return ctMethod;
	}
	
	
	public static CtMethod createSetter(Type propertyType, String propertyName, CtClass ctClass) {
		CtMethod ctMethod = null; 
		AbstractTypeWrapper wrappedType = AbstractTypeWrapper.wrap(propertyType);
		String methodName = ReflectionUtil.setterName(propertyName);
		String parameterType = wrappedType.asClass().getCanonicalName();
		try {
			String code = "public void " + methodName + "(" + parameterType + " " + propertyName + ") { this." + propertyName +"=" + propertyName + "; }";
			ctMethod = CtNewMethod.make(code, ctClass);
			
			if(wrappedType.isGenericType()) {
				javassist.bytecode.SignatureAttribute.Type javassistParameterType = JavassistTypeUtil.asJavassistType(wrappedType);
				MethodSignature methodSignature = new MethodSignature(
					null,
					new javassist.bytecode.SignatureAttribute.Type[]{javassistParameterType},
					new BaseType("void"),
					null
				);
				String encodedSignature = methodSignature.encode();
				ctMethod.setGenericSignature(encodedSignature);
			}
			ctClass.addMethod(ctMethod);
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}

		JavassistUtil.makeNonAbstract(ctMethod);
		return ctMethod;
	}

}
