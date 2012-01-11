package org.logicobjects.instrumentation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;

import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.util.JavassistUtil;

public class LogicObjectInstrumentation {
	private static final String TEST_DIRECTORY = "/test/";
	
	private Class targetClass;
	private ClassPool classPool;
	
	public LogicObjectInstrumentation(Class targetClass, ClassPool classPool) {
		this.targetClass = targetClass;
		this.classPool = classPool;
	}
	
	public static String instrumentedClassName(Class aClass) {
		return aClass.getName() +"_LogicInstrumented";
	}

	
	/*
	 * Answers if an extending class has already been created
	 */
	public boolean isExtendingClassLoaded() {
		return loadedExtendingClass() != null;
	}
	
	/*
	 * Answers the already loaded extending class. If the class is not loaded returns null
	 */
	public Class loadedExtendingClass() {
		String extendingClassName = instrumentedClassName(targetClass);  //derive the name of the extending class using the name of the base class

		try {
			return classPool.getClassLoader().loadClass(extendingClassName);
			//return Class.forName(extendingClassName);
		} catch (ClassNotFoundException e) {  //if we arrive here, the class has not been loaded
			return null;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * If the extending class is already in the classloader return it, otherwise create it dynamically
	 */
	public Class getExtendingClass() {
		
		try {
			Class alreadyLoadedClass = loadedExtendingClass();
			if(alreadyLoadedClass != null)	{
				return alreadyLoadedClass;
			}
			else {
				String extendingClassName = instrumentedClassName(targetClass);  //derive the name of the extending class using the name of the base class
				Class extendingClass = createExtendingClass(extendingClassName, JavassistUtil.asCtClass(targetClass, classPool));  //create it
				return extendingClass;
			}	
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * @param extendingClassName the name of the class to be created
	 * @param parent the base class of the new class
	 */
	private Class createExtendingClass(String extendingClassName, CtClass parent) {
		//ClassPool pool = ClassPool.getDefault();

		CtClass newCtClass = classPool.makeClass(extendingClassName);//creating the new class with the given name
		
		
		try {
			newCtClass.setSuperclass(parent);
			createLogicMethods(newCtClass);
			
			JavassistUtil.makeNonAbstract(newCtClass); //Javassist makes a class abstract if an abstract method is added to the class. Then it has to be explicitly changed back to non-abstract
			//JavassistUtil.createClassFile(TEST_DIRECTORY, newCtClass);  //just to show how the new class looks like
			
			
			//Class newClass = newCtClass.toClass();
			//Class newClass = classPool.toClass(newCtClass, IntensionalViewsPlugin.getDefault().getClass().getClassLoader(), null);
			//Class newClass = classPool.toClass(newCtClass, classPool.getClassLoader(), null);
			Class newClass = classPool.toClass(newCtClass, classPool.getClass().getClassLoader(), null);
			return newClass;
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void createLogicMethods(CtClass ctClass) {
		for(Method m : methodsToOverride()) {
			overrideMethod(ctClass, JavassistUtil.asCtMethod(m));
		}
	}
	

	/*
	 * @return an array of logic methods that should be overridden by the new generated class
	 */
	private Method[] methodsToOverride() {
		return methodsToOverride(targetClass);
	}
	
	private static Method[] methodsToOverride(Class c) {
		List<Method> methods = new ArrayList<Method>();
		for(Method m : c.getMethods()) {
			//System.out.println("Method candidate: "+m.getName()+". Generic string: "+m.toGenericString());
			if (m.getAnnotation(LMethod.class) != null || m.getAnnotation(LQuery.class) != null) {
				methods.add(m);
				//System.out.println("(Logic method)");
				/*
				if(!alreadyFound.containsKey(m.toGenericString())) {
					System.out.println("(Not found method)");
					alreadyFound.put(m.toGenericString(), m);
				} else {
					System.out.println("(Already found method)");
				}
				*/
			} 
		}
		return methods.toArray(new Method[] {});
	}
	
	
	private void overrideMethod(CtClass targetClass, CtMethod ctMethod) {
		try {
			CtMethod ctCopiedMethod = CtNewMethod.copy(ctMethod, targetClass, null);
			try {
				for(Object object : ctMethod.getAnnotations()) {
					java.lang.annotation.Annotation langAnnotation = (java.lang.annotation.Annotation)object;
					
					//System.out.println(langAnnotation.annotationType().getName());

					
					MethodInfo methodInfo = ctMethod.getMethodInfo();
					AnnotationsAttribute attr = (AnnotationsAttribute)methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
					/*
					for (Annotation a : attr.getAnnotations()) {
						System.out.println(a.toString());
					}
					*/
					attr = (AnnotationsAttribute)attr.copy(targetClass.getClassFile().getConstPool(), null);
					/*
					//((CtBehavior)object).toString();
					((javassist.bytecode.annotation.Annotation)object).toString();
					//AnnotationsAttribute attrx = ctMethod.getAttribute(AnnotationsAttribute.visibleTag);
					//ctMethod.
					
					
					ConstPool constPool = targetClass.getClassFile().getConstPool();
					AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
					
					
					//javassist.bytecode.annotation.Annotation javassistAnnotation = new javassist.bytecode.annotation.Annotation(targetClass.getClassFile().getConstPool(), ClassPool.getDefault().get(langAnnotation.annotationType().getName()));
					javassist.bytecode.annotation.Annotation javassistAnnotation = new javassist.bytecode.annotation.Annotation(langAnnotation.annotationType().getName(), targetClass.getClassFile().getConstPool());
					
					attr.setAnnotation(javassistAnnotation);
					*/
					ctCopiedMethod.getMethodInfo().addAttribute(attr);
					
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			/*
			for(Annotation annotation : ctMethod.getAnnotations()) {
				ctCopiedMethod.getMethodInfo().addAttribute(annotation);
			}
			*/
			
			targetClass.addMethod(ctCopiedMethod);
			//System.out.println("Overridding method: "+ctCopiedMethod.getLongName());
			instrumentAsLogicMethod(ctCopiedMethod);

			
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private void instrumentAsLogicObject(CtClass c) {
		if(alreadyInstrumented(c))
			return;
		
		CtMethod logicMethods[] = getLogicMethods(c);
		for(CtMethod m : logicMethods) {
			instrumentAsLogicMethod(m);
		}

		/*
		//System.out.println(logicMethods.length);
		//System.out.println(getAbstractMethods(c).length);
		if(logicMethods.length>0 && getAbstractMethods(c).length == 0) {
			System.out.println("Making class non-abstract");
			makeNonAbstract(c);  //this only works if the class has not been already loaded !
		}
		*/
		/*
		try {
			c.toClass();
			c.defrost();
		} catch (CannotCompileException e1) {
			throw new RuntimeException(e1);
		}
		*/
		
		CtClass superClass;
		try {
			superClass = c.getSuperclass();
			if(!superClass.getName().equals(Object.class.getCanonicalName())) {
				instrumentAsLogicObject(superClass);
			}
		} catch(SecurityException e) {
			//e.printStackTrace();  //assuming that security exceptions are cause only by classes that should not be instrumented
		} catch (Exception e) {
			if(e.getCause() == null || !(e.getCause() instanceof SecurityException) )  //same explanation than the SecurityException above, but applied to the cause of the exception
				throw new RuntimeException(e);
		}
		
	}
	
	
	private boolean alreadyInstrumented(CtClass c) {
		if( c.isModified() )
			return true; //it has been already instrumented (assuming that this is the only instrumentation routine)
		else {
			for(CtMethod m : getLogicMethods(c) ) {
				if(JavassistUtil.isAbstract(m))
					return false;
			}
			try {
				System.out.println("Super: "+c.getSuperclass());
				System.out.println(c.getName());
				if(!c.getSuperclass().getName().equals(Object.class.getName())) {
					return alreadyInstrumented(c.getSuperclass());
				}
					
				else
					return true;
			} catch (NotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	//TODO this method is duplicated with methodsToOverride
	private CtMethod[] getLogicMethods(CtClass c) {
		List<CtMethod> logicMethods = new ArrayList<CtMethod>();
		for(CtMethod m : c.getMethods()) {
			try {
				if(m.getAnnotation(LMethod.class) != null || m.getAnnotation(LQuery.class) != null)
					logicMethods.add(m);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return logicMethods.toArray(new CtMethod[] {});
	}
	
	private String newInstanceString(Class toInstantiate) {
		return toInstantiate.getCanonicalName()+".class.newInstance();";
	}
	
	private String newInstanceString(Class toInstantiate, Class cast) {
		return "("+cast.getCanonicalName()+")"+newInstanceString(toInstantiate);
	}
	
	public static String asNewClassArrayString(CtClass[] classes) {
		if(classes.length == 0) {   //for some mysterious reason Javassist does not allow to create empty arrays like new Class[] {}
			return "new Class[0]";  //that is the reason of the existance of this 'if' block
		}
		StringBuilder sb = new StringBuilder();
		sb.append("new Class[]{ ");
		for(int i=0; i<classes.length; i++) {
			sb.append(classes[i].getName()+".class");
			if(i<classes.length-1)
				sb.append(", ");
		}
		sb.append(" }");
		return sb.toString();
	}
	
	public void instrumentAsLogicMethod(CtMethod m) {
		instrumentAsLogicMethod(m, LogicMethodInvoker.class, "invoke");
	}
		
	public void instrumentAsLogicMethod(CtMethod m, Class invokerClass, String invokerMethodName) {
		String methodCode = null;
		try {
			String methodName = m.getName();
			String methodParameterTypesString = asNewClassArrayString(m.getParameterTypes());
			StringBuilder methodCodeBuilder = new StringBuilder("{ ");

			methodCodeBuilder.append("Object result = null; ");
			methodCodeBuilder.append("try { ");
			methodCodeBuilder.append("String methodName = \""+methodName+"\"; ");
			methodCodeBuilder.append("Object[] args = $args; ");
			
			methodCodeBuilder.append(Method.class.getCanonicalName()+" thisMethod = getClass().getMethod(methodName, "+methodParameterTypesString+"); ");
			

			methodCodeBuilder.append("result = "+invokerClass.getCanonicalName()+"."+invokerMethodName+"(this, thisMethod, args); ");
			
			methodCodeBuilder.append("} catch (Exception e) {throw new RuntimeException(e);} ");
			

			
			
			
			if(!JavassistUtil.isVoid(m)) {
				methodCodeBuilder.append("return ");				
				if(m.getReturnType().isPrimitive()) {
					//model: return ((WrapperType)result).primitiveValue()
					methodCodeBuilder.append("(("+((CtPrimitiveType)m.getReturnType()).getWrapperName()+")result)."+m.getReturnType().getName()+"Value(); ");
				} else {
					//model: return (Type)result;
					methodCodeBuilder.append("("+m.getReturnType().getName()+")result; ");
				}
					
				
			}
			
			methodCodeBuilder.append(" }");
			methodCode = methodCodeBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		/*
		String logicMethodName = null;
		Class resultAdapterClass = null;
		Class parametersAdapterClass = null;
		LogicMethod logicMethodAnnotation;
		try {
			logicMethodAnnotation = (LogicMethod)m.getAnnotation(LogicMethod.class);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		if(logicMethodAnnotation != null) {
			if(!logicMethodAnnotation.name().equals(""))
				logicMethodName = logicMethodAnnotation.name();
			if(!logicMethodAnnotation.resultAdapter().equals(LogicMethod.NO_ADAPTER.class)) {
				try {
					resultAdapterClass = logicMethodAnnotation.resultAdapter();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			parametersAdapterClass = logicMethodAnnotation.parametersAdapter();
		}
		if(logicMethodName == null)  //not set in the annotation
			logicMethodName = m.getName();
		

		

		String methodCode = "{ "
			+"String logicMethodName = \""+logicMethodName+"\"; "
			+"Object[] args = $args; "
			+LogtalkObject.class.getCanonicalName()+" lo = "+LogtalkObjectAdapter.class.getCanonicalName()+".asLogtalkObject(this); ";
		if(resultAdapterClass == null)  //if no result adapter was found, the expected return value is a Query object
			methodCode += "return lo.invokeMethod(logicMethodName, args); ";
		else {
			//System.out.println("SSSSS SHOWING CANONICAL NAME");
			//System.out.println(resultAdapterClass.getCanonicalName());
			//methodCode += QueryAdapter.class.getCanonicalName()+" resultAdapter = null; "
			methodCode += resultAdapterClass.getCanonicalName()+" resultAdapter = null; "
			
					+ "try {"
					//+"resultAdapter = ("+QueryAdapter.class.getCanonicalName()+")"+resultAdapterClass.getCanonicalName()+".class.newInstance();"
					+"resultAdapter = ("+resultAdapterClass.getCanonicalName()+")"+resultAdapterClass.getCanonicalName()+".class.newInstance();"
					+ "} catch (Exception e) {throw new RuntimeException(e);} ";
			
		if(!JavassistUtil.isVoid(m))
			methodCode+= "return ";
		methodCode+= "resultAdapter.adapt(lo.invokeMethod(logicMethodName, args)); ";
		}
		methodCode+=" }";
*/

/*		
		System.out.println("CODE:");
		System.out.println(methodCode);
*/
		try {
			m.setBody(methodCode);
			//JavassistUtil.createClassFile(TEST_DIRECTORY, targetClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		JavassistUtil.makeNonAbstract(m);
	}
	
	

	
	
	

	

	
	public CtMethod[] getAbstractMethods(CtClass c) {
		List<CtMethod> abstractMethods = new ArrayList<CtMethod>();
		for(CtMethod m : c.getMethods()) {
			if(JavassistUtil.isAbstract(m))
				abstractMethods.add(m);
		}
		return abstractMethods.toArray(new CtMethod[] {});
	}
	
}