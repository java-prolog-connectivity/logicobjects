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

import org.minitoolbox.reflection.BeansUtil;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public class CodeGenerationUtil {

	public static CtField createField(CtClass ctFieldClass, Type fieldType, String fieldName, CtClass ctDeclaringClass) {
		TypeWrapper wrappedType = TypeWrapper.wrap(fieldType);
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
	
	/**
	 * TODO change it to use the simpler: CtNewMethod.setter(String methodName, CtField field)  (verify if that method adds the type parameters of the field)
	 * @param propertyType
	 * @param propertyName
	 * @param ctClassDeclaringProperty
	 * @param ctClass
	 * @return
	 */
	public static CtMethod createGetter(Type propertyType, String propertyName, CtClass ctClassDeclaringProperty, CtClass ctClass) {
		CtMethod ctMethod = null; 
		TypeWrapper wrappedType = TypeWrapper.wrap(propertyType);
		String methodName = BeansUtil.getterName(propertyName, wrappedType.getRawClass());
		String returnType = wrappedType.getRawClass().getCanonicalName();
		String thiz = ctClassDeclaringProperty != null ? "((" + ctClassDeclaringProperty.getName() + ")this)." : "this.";
		try {
			String code = "public " + returnType + " " + methodName + "() { return " + thiz + propertyName+"; }";
			ctMethod = CtNewMethod.make(code, ctClass);
			
			if(wrappedType.isGenericType()) {
				String encodedSignature = getterSignature(JavassistTypeUtil.asJavassistType(wrappedType));
				ctMethod.setGenericSignature(encodedSignature);
			}
			ctClass.addMethod(ctMethod);
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}

		//JavassistUtil.makeNonAbstract(ctMethod);//verify this
		return ctMethod;
	}
	
	public static CtMethod createOverriddingGetter(Type propertyType, String propertyName, CtClass ctClass) {
		CtMethod ctMethod = null; 
		TypeWrapper wrappedType = TypeWrapper.wrap(propertyType);
		String methodName = BeansUtil.getterName(propertyName, wrappedType.getRawClass());
		String returnType = wrappedType.getRawClass().getCanonicalName();
		try {
			String code = "public " + returnType + " " + methodName + "() { return super." + methodName+"(); }";
			ctMethod = CtNewMethod.make(code, ctClass);
			
			if(wrappedType.isGenericType()) {
				String encodedSignature = getterSignature(JavassistTypeUtil.asJavassistType(wrappedType));
				ctMethod.setGenericSignature(encodedSignature);
			}
			ctClass.addMethod(ctMethod);
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}

		//JavassistUtil.makeNonAbstract(ctMethod);//verify this
		return ctMethod;
	}
	
	
	public static CtMethod createSetter(Type propertyType, String propertyName, CtClass ctClassDeclaringProperty, CtClass ctClass) {
		CtMethod ctMethod = null; 
		TypeWrapper wrappedType = TypeWrapper.wrap(propertyType);
		String methodName = BeansUtil.setterName(propertyName);
		String parameterType = wrappedType.getRawClass().getCanonicalName();
		String thiz = ctClassDeclaringProperty != null ? "((" + ctClassDeclaringProperty.getName() + ")this)." : "this.";
		try {
			String code = "public void " + methodName + "(" + parameterType + " " + propertyName + ") { " + thiz + propertyName +"=" + propertyName + "; }";
			ctMethod = CtNewMethod.make(code, ctClass);
			
			if(wrappedType.isGenericType()) {
				String encodedSignature = setterSignature(JavassistTypeUtil.asJavassistType(wrappedType));
				ctMethod.setGenericSignature(encodedSignature);
			}
			ctClass.addMethod(ctMethod);
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}

		//JavassistUtil.makeNonAbstract(ctMethod);//verify this
		return ctMethod;
	}
	
	public static CtMethod createOverriddingSetter(Type propertyType, String propertyName, CtClass ctClass) {
		CtMethod ctMethod = null; 
		TypeWrapper wrappedType = TypeWrapper.wrap(propertyType);
		String methodName = BeansUtil.setterName(propertyName);
		String parameterType = wrappedType.getRawClass().getCanonicalName();
		try {
			String code = "public void " + methodName + "(" + parameterType + " " + propertyName + ") { super." + methodName + "(" + propertyName + "); }";
			ctMethod = CtNewMethod.make(code, ctClass);
			
			if(wrappedType.isGenericType()) {
				String encodedSignature = setterSignature(JavassistTypeUtil.asJavassistType(wrappedType));
				ctMethod.setGenericSignature(encodedSignature);
			}
			ctClass.addMethod(ctMethod);
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}

		//JavassistUtil.makeNonAbstract(ctMethod);//verify this
		return ctMethod;
	}
	
	private static String getterSignature(javassist.bytecode.SignatureAttribute.Type javassistReturnType) {
		MethodSignature methodSignature = new MethodSignature(
			null,
			null,
			javassistReturnType,
			null
			);
		return methodSignature.encode();
	}
	
	private static String setterSignature(javassist.bytecode.SignatureAttribute.Type javassistParameterType) {
		MethodSignature methodSignature = new MethodSignature(
			null,
			new javassist.bytecode.SignatureAttribute.Type[]{javassistParameterType},
			new BaseType("void"),
			null
			);
		return methodSignature.encode();
	}

}
