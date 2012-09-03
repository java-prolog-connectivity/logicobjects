package org.logicobjects.instrumentation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.ObjectType;
import javassist.bytecode.SignatureAttribute.TypeArgument;
import javassist.bytecode.SignatureAttribute.TypeParameter;
import javassist.bytecode.SignatureAttribute.TypeVariable;
import javassist.bytecode.SyntheticAttribute;

import org.logicobjects.adapter.BadExpressionException;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.core.LogicObjectClass;
import org.logicobjects.core.NoLogicResultException;
import org.logicobjects.util.JavassistUtil;
import org.logicobjects.util.JavassistUtil.ObjectConverter;

public class LogicObjectInstrumentation {
	
/*
	private Map<String, String> generatedMethodsToExpressionMap() {
		Map<String, String> generatedMethodsInfo = new HashMap<String, String>();
		Method[] methods = methodsToOverride();
		for(Method method: methods) {
			LMethod aLMethod = method.getAnnotation(LMethod.class);
			for(String param : aLMethod.parameters()) {
				
			}
		}
		return generatedMethodsInfo;
	}

*/


	private static final String TEST_DIRECTORY = "/test/";
	
	private Class classToExtend;
	private ClassPool classPool;
	private CtClass ctClassToExtend;
	
	public LogicObjectInstrumentation(Class classToExtend, ClassPool classPool) {
		this.classToExtend = classToExtend;
		this.classPool = classPool;
		this.ctClassToExtend = JavassistUtil.asCtClass(classToExtend, classPool);
	}


	//public static final String GENERATED_CLASS_SUFFIX = "_$LogicInstrumented";
	//public static final String GENERATED_PARAMETER_PREFIX = "$logicObjectsParam";
	public static final String GENERATED_CLASS_SUFFIX = "___LogicObjectsInstrumented";  //avoid the character "$" this could create problems since it has an special meaning in javassist
	public static final String GENERATED_PARAMETER_PREFIX = "logicObjectsParam";
	
	public static String instrumentedClassName(Class aClass) {
		return aClass.getName() + GENERATED_CLASS_SUFFIX;
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
		String extendingClassName = instrumentedClassName(classToExtend);  //derive the name of the extending class using the name of the base class

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
				String extendingClassName = instrumentedClassName(classToExtend);  //derive the name of the extending class using the name of the base class
				Class extendingClass = createExtendingClass(extendingClassName);  //create it
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
	private Class createExtendingClass(String extendingClassName) {
		CtClass newCtClass = classPool.makeClass(extendingClassName);//creating the new class with the given name
		
		try {
			newCtClass.setSuperclass(ctClassToExtend);
			createAccessorsAndMutators(newCtClass);
			createConstructors(newCtClass);
			createLogicMethods(newCtClass);
			
			JavassistUtil.makeNonAbstract(newCtClass); //Javassist makes a class abstract if an abstract method is added to the class. Then it has to be explicitly changed back to non-abstract
			JavassistUtil.createClassFile(TEST_DIRECTORY, newCtClass);  //just to show how the new class looks like (TODO DO NOT FORGET TO DELETE THIS LINE IN THE RELEASED IMPLEMENTATION !!!)
			
			String genericSignature = ctClassToExtend.getGenericSignature(); //the generic signature contains information about the type parameters and the generic superclass and interfaces
			/**
			 * In case that the parent class has generic signature data, part of this data should be present in the generated class
			 * For example, if the generated class is:
			 * public class A<X,Y> {...}
			 * 
			 * Then the generated class should be:
			 * public class B<X,Y> extends A<X,Y> {...}
			 */
			if(genericSignature != null) {  //there is generic signature data in the parent class, then we should copy this on the generated class
				try {
					ClassSignature extendedClassSignature = SignatureAttribute.toClassSignature(genericSignature); //reifies the string representation of the generic class signature to a more convenient object representation
					
					List<TypeArgument> typeArgumentsList = new ArrayList<TypeArgument>();
					for(TypeParameter typeParameter : extendedClassSignature.getParameters()) {
						ObjectType objectType = new TypeVariable(typeParameter.getName());
						typeArgumentsList.add(new TypeArgument(objectType));
					}
					
					ClassType extendingGenericSuperClassType = new ClassType(ctClassToExtend.getName(), typeArgumentsList.toArray(new TypeArgument[]{}));
					
					ClassSignature extendingClassSignature = new ClassSignature(
							extendedClassSignature.getParameters(), //same type parameters than the extending class
							extendingGenericSuperClassType, //setting the generic super class to be the parent class with arguments with same names than the parameter types in the parent class declaration
							new ClassType[]{} //no additional interfaces
					);

					newCtClass.setGenericSignature(extendingClassSignature.encode());
				} catch (BadBytecode e) {
					throw new RuntimeException(e);
				}
			}
			SyntheticAttribute syntheticAttribute = new SyntheticAttribute(ctClassToExtend.getClassFile().getConstPool());
			newCtClass.setAttribute(syntheticAttribute.getName(), syntheticAttribute.get()); //Marking the generated class as synthetic
			Class newClass = classPool.toClass(newCtClass, classPool.getClass().getClassLoader(), null);
			return newClass;
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}
	

	public static class A {
		private int a;
		protected int b;
		int c;
		public int d;
	}
	
	public static class B extends A {
		public int d;
		
		public static void main(String[] args) {
			A o = new A();
			Field f;
			/*
			try {
				f = A.class.getField("a");
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			System.out.println(f);
			*/
			/*
			try {
				f = A.class.getField("b");
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			System.out.println(f);
			*/
			
			try {
				f = A.class.getField("c");
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			System.out.println(f);
			
			try {
				f = A.class.getField("d");
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			try {
				f.set(o, 11);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			System.out.println(f);

		}
	}
	
	/**
	 * Adds a generic signature to a target CtBehavior object (e.g., a method or constructor) from a model CtBehavior
	 * Generics data is not strictly part of the method byte code, but rather "extra-data"
	 * this data concerning generic is copied from the original method to the new one
	 * @param target the target CtBehavior to which the generic signature will be added
	 * @param model the CtBehavior from which the generic signature will be copied
	 */
	private void copyGenericSignature(CtBehavior target, CtBehavior model) {
		String constructorGenericSignature = model.getGenericSignature(); 
		if(constructorGenericSignature != null) 
			target.setGenericSignature(constructorGenericSignature);
	}
	
	private void copyAnnotationsAttribute(CtBehavior target, CtBehavior model, ClassMap classMap) {
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
	
	private void createAccessorsAndMutators(CtClass son) {
		LogicObjectClass parentLogicObjectClass = LogicObjectClass.findLogicObjectClass(classToExtend);
		for(String arg : parentLogicObjectClass.getLObjectArgs()) {
			Field field;
			try {
				field = classToExtend.getDeclaredField(arg);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			if(field == null) {
				
			}
		}
		//if(parent.toClass())
	}
	
	
	private void createConstructors(CtClass son) {
		CtConstructor[] parentConstructors = ctClassToExtend.getConstructors();
		ClassMap classMap = new ClassMap();
		classMap.fix(ctClassToExtend);
		for(CtConstructor parentCtConstructor : parentConstructors) {
			try {
				CtConstructor newCtConstructor = new CtConstructor(parentCtConstructor, son, classMap);
				copyGenericSignature(newCtConstructor, parentCtConstructor);
				copyAnnotationsAttribute(newCtConstructor, parentCtConstructor, classMap);
				
				
				
				newCtConstructor.setBody("{ super($$); }");
				//JavassistUtil.makeNonAbstract(newCtConstructor);
				
				son.addConstructor(newCtConstructor);
				
			} catch (CannotCompileException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void createLogicMethods(CtClass ctClass) {
		for(Method m : methodsToOverride()) {
			CtMethod ctMethod = JavassistUtil.asCtMethod(m, classPool);
			createAuxiliaryMethods(ctClass, m);
			overrideMethod(ctClass, ctMethod);
		}
	}
	
	private String getParamDeclarationString(Method method) {
		StringBuilder paramDeclaration = new StringBuilder();
		Class[] parameterTypes = method.getParameterTypes();
		for(int i = 0; i<parameterTypes.length; i++) {
			paramDeclaration.append(parameterTypes[i].getCanonicalName() + " " + GENERATED_PARAMETER_PREFIX + (i+1) );
			if(i != parameterTypes.length-1)
				paramDeclaration.append(", ");
		}
		return paramDeclaration.toString();
	}
	
	private void createAuxiliaryMethods(CtClass ctClass, Method method) {
		final String OBJECT_CONVERSION_METHOD_NAME = "toObject";
		Map<String, String> auxiliaryMethodsMap = AbstractLogicMethodParser.create(method).parse().generatedMethodsMap();
		for(Entry<String, String> methodEntry : auxiliaryMethodsMap.entrySet()) {
			String methodName = methodEntry.getKey();
			String methodExpression = methodEntry.getValue();
			CtMethod ctMethod;
			try {
				String code = "public Object "+ methodName + "(" + getParamDeclarationString(method) + ") { return " + ObjectConverter.class.getCanonicalName() + "." + OBJECT_CONVERSION_METHOD_NAME + "(" + methodExpression + "); }";
				ctMethod = CtNewMethod.make(code, ctClass);
				ctClass.addMethod(ctMethod);
			} catch (CannotCompileException e) {
				throw new BadExpressionException(methodName, methodExpression);
			}
		}
	}
	
	/*
	 * @return an array of logic methods that should be overridden by the new generated class
	 */
	private Method[] methodsToOverride() {
		return methodsToOverride(classToExtend);
	}
	
	
	
	private static Method[] methodsToOverride(Class c) {
		List<Method> methods = new ArrayList<Method>();
		for(Method m : c.getMethods()) {
			//System.out.println("Method candidate: "+m.getName()+". Generic string: "+m.toGenericString());
			if (m.getAnnotation(LMethod.class) != null || m.getAnnotation(LQuery.class) != null || m.getAnnotation(LSolution.class) != null) {
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

	
	
	//TODO
	private void overrideMethod(CtClass targetClass, CtMethod ctMethod) {
		try {
			ClassMap classMap = new ClassMap();
			/**
			 * From the Javassist documentation:
			 * "By default, all the occurrences of the names of the class declaring m and the superclass are replaced with the name of the class and the superclass that the created method is added to. 
			 * This is done whichever map is null or not. To prevent this replacement, call ClassMap.fix()."
			 */
			classMap.fix(JavassistUtil.asCtClass(this.classToExtend, classPool));
			/**
			 * In the case that this class map is not included the following problem will occur:
			 * - Situation: The overriding method contains references (its return value for example) to the parent class where the extended method was originally located
			 * - Consequence: All these references to the parent class will be substituted by the instrumented class
			 * - Problem: For some reason, call to this method will throw at runtime an AbstractMethodError.
			 */
			CtMethod ctCopiedMethod = CtNewMethod.copy(ctMethod, targetClass, classMap);
			copyGenericSignature(ctCopiedMethod, ctMethod);
			copyAnnotationsAttribute(ctCopiedMethod, ctMethod, classMap);
				
			
			//System.out.println("Overridding method: "+ctCopiedMethod.getLongName());
			
			
			
			instrumentAsLogicMethod(ctCopiedMethod);
			
			
			targetClass.addMethod(ctCopiedMethod);
			
			
			

		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String newInstanceString(Class toInstantiate) {
		return toInstantiate.getCanonicalName()+".class.newInstance();";
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
				if(m.getReturnType().isPrimitive()) {
					methodCodeBuilder.append("if (result == null) {throw new " + NoLogicResultException.class.getCanonicalName() +"(); } ");
					
					//model: return ((WrapperType)result).primitiveValue()
					String castingResultType = ((CtPrimitiveType)m.getReturnType()).getWrapperName();
					methodCodeBuilder.append("return (("+ castingResultType +")result)."+m.getReturnType().getName()+"Value(); ");
				} else {
					//model: return (Type)result;
					String castingResultType = m.getReturnType().getName();
					methodCodeBuilder.append("return ("+ castingResultType +")result; ");
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
