package org.logicobjects.util.javassist;


import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;

import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

public class JavassistUtil {
	
	
	/**
	 * From the Javassist documentation:
	 * "By default, all the occurrences of the names of the class declaring m and the superclass are replaced with the name of the class and the superclass that the created method is added to. 
	 * This is done whichever map is null or not. To prevent this replacement, call ClassMap.fix()."
	 */
	public static ClassMap fixedClassMap(CtClass ctClazz, ClassPool classPool) {
		ClassMap classMap = new ClassMap();
		classMap.fix(ctClazz);
		CtClass superClass = null;
		try {
			superClass = ctClazz.getSuperclass();
		} catch (NotFoundException e) {
		}
		if(superClass != null)
			classMap.fix(superClass);
		try {
			for(CtClass interfaze : ctClazz.getInterfaces())
				classMap.fix(interfaze);
		} catch (NotFoundException e) {
		}
		return classMap;
		
	}
	
	public static ClassMap fixedClassMap(Class clazz, ClassPool classPool) {
		return fixedClassMap(JavassistUtil.asCtClass(clazz, classPool), classPool);
	}
	
/*
	public static CtClass asCtClass(Class c) {
		return asCtClass(c, ClassPool.getDefault());
	}
*/
	
	public static CtClass asCtClass(Type type, ClassPool pool) {
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		CtClass ctClass = asCtClass(typeWrapper.asClass(), pool);
		return ctClass;
	}
	
	public static CtClass asCtClass(Class c, ClassPool pool) {
		try {
			return pool.get(c.getName());	
		} catch (NotFoundException e) {
			try {
				ClassLoader targetClassLoader = c.getClassLoader();
				pool.appendClassPath(new LoaderClassPath(targetClassLoader));
				return pool.get(c.getName());
			} catch (NotFoundException e1) {
				throw new RuntimeException(e1);
			}
		}
	}
	
	private static CtClass[] asCtClasses(Class[] classes, ClassPool pool) {
		CtClass[] ctClasses = new CtClass[classes.length];
		for(int i=0; i<classes.length; i++) {
			ctClasses[i] = asCtClass(classes[i], pool);
		}
		return ctClasses;
	}
	
	/*
	public static CtClass asCtClass(Class c, ClassLoader cl) {
		ClassPool pool = ClassPool.getDefault();
		pool.appendClassPath(new LoaderClassPath(cl));
		try {
			return pool.get(c.getName());
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	*/

	public static CtMethod asCtMethod(Method m, ClassPool pool) {
		CtClass ctDeclaringClass = asCtClass(m.getDeclaringClass(), pool);
		try {
			CtClass[] ctParamClasses = asCtClasses(m.getParameterTypes(), pool);
			return ctDeclaringClass.getDeclaredMethod(m.getName(), ctParamClasses);
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static CtField asCtField(Field f, ClassPool pool) {
		CtClass ctDeclaringClass = asCtClass(f.getDeclaringClass(), pool);
		try {
			return ctDeclaringClass.getField(f.getName());
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void createClassFile(String basePath, Class aClass) {
		try {
			createClassFile(basePath, ClassPool.getDefault().get(aClass.getCanonicalName()));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void createClassFile(String basePath, CtClass ctClass) {
		ClassFile cf;
		try {
			cf = ctClass.getClassFile();
			String extension = ".class";
			FileOutputStream os = new FileOutputStream(basePath+ctClass.getSimpleName()+extension);
			cf.write(new DataOutputStream(os));
			os.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isAbstract(CtClass clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}
	
	//this does not work if the class has already been loaded
	public static void makeNonAbstract(CtClass c) {
		c.setModifiers(c.getModifiers() & ~Modifier.ABSTRACT);
	}

	public static void makeNonAbstract(CtBehavior m) {
		m.setModifiers(m.getModifiers() & ~Modifier.ABSTRACT);
	}

	public static boolean isVoid(CtMethod m) {
		try {
			return m.getReturnType().getName().equals("void");
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void makePublic(CtMember ctMember) {
		ctMember.setModifiers( (ctMember.getModifiers() & ~Modifier.PROTECTED & ~Modifier.PRIVATE) | Modifier.PUBLIC);
	}
	
	public static void makeProtected(CtMember ctMember) {
		ctMember.setModifiers( (ctMember.getModifiers() & ~Modifier.PUBLIC & ~Modifier.PRIVATE) | Modifier.PROTECTED);
	}
	
	public static void makePrivate(CtMember ctMember) {
		ctMember.setModifiers( (ctMember.getModifiers() & ~Modifier.PUBLIC & ~Modifier.PROTECTED) | Modifier.PRIVATE);
	}
	
	public static void makePackageAccess(CtMember ctMember) {
		ctMember.setModifiers(ctMember.getModifiers() & ~Modifier.PUBLIC & ~Modifier.PROTECTED & ~Modifier.PRIVATE);
	}
	
	public static boolean isAbstract(CtMember ctMember) {
		return Modifier.isAbstract(ctMember.getModifiers());
	}
	
	public static boolean isPublic(CtMember ctMember) {
		return Modifier.isPublic(ctMember.getModifiers());
	}
	
	public static boolean isProtected(CtMember ctMember) {
		return Modifier.isProtected(ctMember.getModifiers());
	}
	
	public static boolean isPrivate(CtMember ctMember) {
		return Modifier.isPrivate(ctMember.getModifiers());
	}
	
	public static boolean hasPackageAccessModifier(CtMember ctMember) {
		return ReflectionUtil.hasPackageAccessModifier(ctMember.getModifiers());
	}
	
	

	
	
	
	/**
	 * Adds a generic signature to a target CtBehavior object (e.g., a method or constructor) from a model CtBehavior
	 * Generics data is not strictly part of the method byte code, but rather "extra-data"
	 * this data concerning generic is copied from the original method to the new one
	 * @param target the target CtBehavior to which the generic signature will be added
	 * @param model the CtBehavior from which the generic signature will be copied
	 */
	public static void copyGenericSignature(CtMember target, CtMember model) {
		String genericSignature = model.getGenericSignature(); 
		if(genericSignature != null) 
			target.setGenericSignature(genericSignature);
	}
	
	public static void copyAnnotationsAttribute(CtBehavior target, CtBehavior model, ClassMap classMap) {
		MethodInfo methodInfo = model.getMethodInfo();

		AnnotationsAttribute methodAnnotationsAttribute = (AnnotationsAttribute)methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
		if(methodAnnotationsAttribute != null) {
			methodAnnotationsAttribute = (AnnotationsAttribute)methodAnnotationsAttribute.copy(model.getDeclaringClass().getClassFile().getConstPool(), classMap);
			target.getMethodInfo().addAttribute(methodAnnotationsAttribute);
		}
		
		ParameterAnnotationsAttribute parameterAnnotationsAttribute = (ParameterAnnotationsAttribute)methodInfo.getAttribute(ParameterAnnotationsAttribute.visibleTag);
		if(parameterAnnotationsAttribute != null) {
			parameterAnnotationsAttribute = (ParameterAnnotationsAttribute)parameterAnnotationsAttribute.copy(model.getDeclaringClass().getClassFile().getConstPool(), classMap);
			target.getMethodInfo().addAttribute(parameterAnnotationsAttribute);
		}
	}
	
	
	


	
}
