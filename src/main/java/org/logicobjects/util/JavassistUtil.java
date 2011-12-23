package org.logicobjects.util;


import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;

public class JavassistUtil {

	public static CtClass asCtClass(Class c) {
		return asCtClass(c, ClassPool.getDefault());
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
	public static CtMethod asCtMethod(Method m) {
		CtClass rtClass = asCtClass(m.getDeclaringClass());
		try {
			return rtClass.getDeclaredMethod(m.getName());
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

	public static void makeNonAbstract(CtMethod m) {
		m.setModifiers(m.getModifiers() & ~Modifier.ABSTRACT);
	}

	public static boolean isAbstract(CtMethod m) {
		return Modifier.isAbstract(m.getModifiers());
	}
	
}
