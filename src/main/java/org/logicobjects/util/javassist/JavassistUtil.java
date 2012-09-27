package org.logicobjects.util.javassist;


import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute.BaseType;

import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import org.reflectiveutils.wrappertype.SingleTypeWrapper;

public class JavassistUtil {
	
	
	/**
	 * From the Javassist documentation:
	 * "By default, all the occurrences of the names of the class declaring m and the superclass are replaced with the name of the class and the superclass that the created method is added to. 
	 * This is done whichever map is null or not. To prevent this replacement, call ClassMap.fix()."
	 */
	public static ClassMap fixedClassMap(Class clazz, ClassPool classPool) {
		ClassMap classMap = new ClassMap();
		classMap.fix(JavassistUtil.asCtClass(clazz, classPool));
		Class superClass = clazz.getSuperclass();
		if(superClass != null)
			classMap.fix(JavassistUtil.asCtClass(superClass, classPool));
		for(Class interfaze : clazz.getInterfaces())
			classMap.fix(JavassistUtil.asCtClass(interfaze, classPool));
		
		return classMap;
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
		CtClass rtClass = asCtClass(m.getDeclaringClass(), pool);
		try {
			CtClass[] ctParamClasses = asCtClasses(m.getParameterTypes(), pool);
			return rtClass.getDeclaredMethod(m.getName(), ctParamClasses);
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
	
	public static boolean isVoid(CtMethod m) {
		try {
			return m.getReturnType().getName().equals("void");
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	//this does not work if the class has already been loaded
	public static void makeNonAbstract(CtClass c) {
		c.setModifiers(c.getModifiers() & ~Modifier.ABSTRACT);
	}

	public static void makeNonAbstract(CtBehavior m) {
		m.setModifiers(m.getModifiers() & ~Modifier.ABSTRACT);
	}

	public static boolean isAbstract(CtBehavior m) {
		return Modifier.isAbstract(m.getModifiers());
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
	
	
	
	/**
	 * 
	 * This class is a workaround to the issue that Javassist works often with the java 1.4 specification.
	 * This is useful in certain method generation routines, where the generated method returns an object from a given expression
	 * This class just converts primitive types to objects
	 * TODO find an equivalent in Guava or something like that
	 *
	 */
	public static class ObjectConverter {
		
		public static Object toObject(Object o) {
			return o;
		}
		
		public static Object toObject(boolean o) {
			return o;
		}
		
		public static Object toObject(byte o) {
			return o;
		}
		
		public static Object toObject(char o) {
			return o;
		}
		
		public static Object toObject(short o) {
			return o;
		}
		
		public static Object toObject(int o) {
			return o;
		}
		
		public static Object toObject(long o) {
			return o;
		}
		
		public static Object toObject(float o) {
			return o;
		}
		
		public static Object toObject(Double o) {
			return o;
		}
		
		/*
		public static Object toObject(Object o) {
			return o;
		}
		
		public static Object toObject(boolean o) {
			return Boolean.valueOf(o);
		}
		
		public static Object toObject(byte o) {
			return Byte.valueOf(o);
		}
		
		public static Object toObject(char o) {
			return Character.valueOf(o);
		}
		
		public static Object toObject(short o) {
			return Short.valueOf(o);
		}
		
		public static Object toObject(int o) {
			return Integer.valueOf(o);
		}
		
		public static Object toObject(long o) {
			return Long.valueOf(o);
		}
		
		public static Object toObject(float o) {
			return Float.valueOf(o);
		}
		
		public static Object toObject(Double o) {
			return Double.valueOf(o);
		}
		*/
	}
	
}
