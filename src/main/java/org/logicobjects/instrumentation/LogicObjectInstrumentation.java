package org.logicobjects.instrumentation;

import static org.reflectiveutils.ReflectionUtil.isAbstract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.ObjectType;
import javassist.bytecode.SignatureAttribute.TypeArgument;
import javassist.bytecode.SignatureAttribute.TypeParameter;
import javassist.bytecode.SignatureAttribute.TypeVariable;
import javassist.bytecode.SyntheticAttribute;

import org.logicobjects.adapter.BadExpressionException;
import org.logicobjects.core.LogicBeanProperty;
import org.logicobjects.core.LogicObjectClass;
import org.logicobjects.core.LogicRoutine;
import org.logicobjects.core.NoLogicResultException;
import org.logicobjects.logicengine.LogicEngineConfiguration;
import org.logicobjects.util.javassist.CodeGenerationUtil;
import org.logicobjects.util.javassist.JavassistUtil;
import org.logicobjects.util.javassist.PrimitiveTypesWorkaround;
import org.reflectiveutils.BeansUtil;
import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

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
	public static final String GENERATED_INSTANCE_VAR_SUFFIX = "___LogicObjectsInstrumented"; //avoid the character "$" this could create problems since it has an special meaning in javassist
	public static final String LOGIC_ENGINE_CONFIG_FIELD_NAME = "logicEngineConfig" + GENERATED_INSTANCE_VAR_SUFFIX;
	
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
			addLogicEngineProperty(newCtClass);
			createGettersAndSetters(newCtClass);
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
					/**
					 * The Class Signature contains: 
					 *  - the type parameters of the class
					 *  - the generic super class
					 *  - an array of generic interfaces
					 */
					ClassSignature extendedClassSignature = SignatureAttribute.toClassSignature(genericSignature); //reifies the string representation of the generic class signature to a more convenient object representation
					
					/**
					 * this block collects a list of type arguments having the same names than the type parameters of the super type
					 */
					List<TypeArgument> typeArgumentsList = new ArrayList<TypeArgument>();
					for(TypeParameter typeParameter : extendedClassSignature.getParameters()) {
						ObjectType objectType = new TypeVariable(typeParameter.getName());
						typeArgumentsList.add(new TypeArgument(objectType));
					}
					
					//the generic signature of the super class declared by the generated class
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

	private void addLogicEngineProperty(CtClass son) {
		CtClass ctFieldClass = JavassistUtil.asCtClass(LogicEngineConfiguration.class, classPool);
		CodeGenerationUtil.createField(ctFieldClass, LogicEngineConfiguration.class, LOGIC_ENGINE_CONFIG_FIELD_NAME, son);
		CodeGenerationUtil.createGetter(LogicEngineConfiguration.class, LOGIC_ENGINE_CONFIG_FIELD_NAME, null, son);
		CodeGenerationUtil.createSetter(LogicEngineConfiguration.class, LOGIC_ENGINE_CONFIG_FIELD_NAME, null, son);
	}
	
	private void createGettersAndSetters(CtClass son) {
		ClassMap classMap = JavassistUtil.fixedClassMap(classToExtend, classPool);

		LogicObjectClass parentLogicObjectClass = LogicObjectClass.findLogicObjectClass(classToExtend);
		//Map<String, Field> visibleFields = ReflectionUtil.visibleFields(classToExtend);
		for(String arg : parentLogicObjectClass.getLObjectArgs()) {
			
			//Field visiblePropertyField = visibleFields.get(arg);
			
			LogicBeanProperty beanProperty = new LogicBeanProperty(classToExtend, arg);
			
			Type beanPropertyType = beanProperty.getPropertyType();
			Class beanPropertyClass;
			if(beanPropertyType != null)
				beanPropertyClass = AbstractTypeWrapper.wrap(beanPropertyType).asClass();
			else {//there is no property (no field, getter, or setter) with the given name in the bean, assuming the desired type of the field is Object.class
				beanPropertyType = Object.class;
				beanPropertyClass = Object.class;
			}
			
			
			Field propertyField = beanProperty.getPropertyField();
			Method currentGetter = beanProperty.getPropertyGetter();
			Method currentSetter = beanProperty.getPropertySetter();
			
			CtField ctPropertyField = null;

			
			if( (currentGetter == null || isAbstract(currentGetter)) //there is not a valid getter
				&& (currentSetter != null && !isAbstract(currentSetter)) //but there is a valid setter
				&& propertyField == null) //and there is not a visible field for the property
				throw new RuntimeException("Impossible to generate accessor for property " + arg + ". Mutator exists but field does not.");
			
			if( (currentSetter == null || isAbstract(currentSetter)) //there is not a valid getter
					&& (currentGetter != null && !isAbstract(currentGetter)) //but there is a valid setter
					&& propertyField == null) //and there is not a visible field for the property
					throw new RuntimeException("Impossible to generate mutator for property " + arg + ". Accessor exists but field does not.");
			
			if(propertyField == null) { //a field should be generated
				if( (currentGetter == null || isAbstract(currentGetter)) && (currentSetter == null || isAbstract(currentSetter))) { //no implementation for getter and setter
					CtClass ctFieldClass = JavassistUtil.asCtClass(beanPropertyClass, classPool);
					ctPropertyField = CodeGenerationUtil.createField(ctFieldClass, beanPropertyType, arg, son);
				}
			} else {
				ctPropertyField = JavassistUtil.asCtField(propertyField, classPool);
			}
			
/*
			//verify the access modifiers of the existing field
			if(ctPropertyField != null && 
					JavassistUtil.isPrivate(ctPropertyField) || 
					(JavassistUtil.hasPackageAccessModifier(ctPropertyField) && !ctPropertyField.getDeclaringClass().getPackageName().equals(son.getPackageName()))) {
				//throw new RuntimeException(new IllegalAccessException("The field " + propertyField + " should be declared as protected"));
				JavassistUtil.makeProtected(ctPropertyField); //this does not work since the extended class is already loaded and cannot be modified anymore (apparently it is not necessary anyway...)
			}
*/
			
			
			if(currentGetter == null || isAbstract(currentGetter)) {
				CtMethod ctGeneratedGetter = CodeGenerationUtil.createGetter(beanPropertyType, arg, ctPropertyField.getDeclaringClass(), son);
				if(currentGetter != null) { //then it is abstract
					CtMethod ctCurrentGetter = JavassistUtil.asCtMethod(currentGetter, classPool);
					JavassistUtil.copyAnnotationsAttribute(ctGeneratedGetter, ctCurrentGetter, classMap);
				}
			} else {
				if(!ReflectionUtil.isPublic(currentGetter)) {
					CtMethod ctGeneratedGetter = CodeGenerationUtil.createOverriddingGetter(beanPropertyType, arg, son);
					CtMethod ctCurrentGetter = JavassistUtil.asCtMethod(currentGetter, classPool);
					JavassistUtil.copyAnnotationsAttribute(ctGeneratedGetter, ctCurrentGetter, classMap);
					//JavassistUtil.makePublic(ctCurrentGetter);  //this does not work since the extended class is already loaded and cannot be modified anymore
					//throw new RuntimeException(new IllegalAccessException("The method " + currentGetter + " should be declared as public"));
				}
			}
			
			if(currentSetter == null || isAbstract(currentSetter)) {
				CtMethod ctGeneratedSetter = CodeGenerationUtil.createSetter(beanPropertyType, arg, ctPropertyField.getDeclaringClass(), son);
				if(currentSetter != null) { //then it is abstract
					CtMethod ctCurrentSetter = JavassistUtil.asCtMethod(currentSetter, classPool);
					JavassistUtil.copyAnnotationsAttribute(ctGeneratedSetter, ctCurrentSetter, classMap);
				}
			} else {
				if(!ReflectionUtil.isPublic(currentSetter)) {
					CtMethod ctGeneratedSetter = CodeGenerationUtil.createOverriddingSetter(beanPropertyType, arg, son);
					CtMethod ctCurrentSetter = JavassistUtil.asCtMethod(currentSetter, classPool);
					JavassistUtil.copyAnnotationsAttribute(ctGeneratedSetter, ctCurrentSetter, classMap);
					//JavassistUtil.makePublic(ctCurrentSetter);  //this does not work since the extended class is already loaded and cannot be modified anymore
					//throw new RuntimeException(new IllegalAccessException("The method " + currentSetter + " should be declared as public"));
				}
			}
			
		}
		
	}


	/*
	public void addGenericSignature(CtMethod ctMethod, String[] typeParameters,Type[] paramsTypes, Type returnType, Type[] exceptionsTypes) {
		MethodSignature methodSignature = new MethodSignature(
				extendedClassSignature.getParameters(), //same type parameters than the extending class
				extendingGenericSuperClassType, //setting the generic super class to be the parent class with arguments with same names than the parameter types in the parent class declaration
				new ClassType[]{} //no additional interfaces
		);

		ctMethod.setGenericSignature(methodSignature.encode());
	}
*/
	
	
	private void createConstructors(CtClass son) {
		CtConstructor[] parentConstructors = ctClassToExtend.getConstructors();
		ClassMap classMap = JavassistUtil.fixedClassMap(classToExtend, classPool);

		for(CtConstructor parentCtConstructor : parentConstructors) {
			try {
				CtConstructor newCtConstructor = new CtConstructor(parentCtConstructor, son, classMap);
				JavassistUtil.copyGenericSignature(newCtConstructor, parentCtConstructor);
				JavassistUtil.copyAnnotationsAttribute(newCtConstructor, parentCtConstructor, classMap);
				
				
				
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
		Map<String, String> auxiliaryMethodsMap = LogicMethodParser.create(method).parse().generatedMethodsMap();
		for(Entry<String, String> methodEntry : auxiliaryMethodsMap.entrySet()) {
			String methodName = methodEntry.getKey();
			String methodExpression = methodEntry.getValue();
			CtMethod ctMethod;
			try {
				String code = "public Object "+ methodName + "(" + getParamDeclarationString(method) + ") { return " + PrimitiveTypesWorkaround.class.getCanonicalName() + "." + OBJECT_CONVERSION_METHOD_NAME + "(" + methodExpression + "); }";
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
		List<Method> abstractMethods = ReflectionUtil.getAllAbstractMethods(c);
		List<Method> methodsToOverride = new ArrayList<Method>();
		for(Method abstractMethod: abstractMethods) {
			if(!BeansUtil.looksLikeBeanMethod(abstractMethod) || LogicRoutine.isAnnotatedAsLogicRoutine(abstractMethod))
				methodsToOverride.add(abstractMethod);
		}
		return methodsToOverride.toArray(new Method[]{});
		
		//isAnnotatedAsLogicRoutine
		/*
		List<Method> methods = new ArrayList<Method>();
		for(Method m : c.getMethods()) {
			//System.out.println("Method candidate: "+m.getName()+". Generic string: "+m.toGenericString());
			//if (m.getAnnotation(LMethod.class) != null || m.getAnnotation(LQuery.class) != null || m.getAnnotation(LSolution.class) != null) {
			if(ReflectionUtil.isAbstract(m)) {
				methods.add(m);
			} 
		}
		return methods.toArray(new Method[] {});
		*/
	}

	
	
	
	//TODO
	private void overrideMethod(CtClass targetClass, CtMethod ctMethod) {
		try {
			/**
			 * From the Javassist documentation:
			 * "By default, all the occurrences of the names of the class declaring m and the superclass are replaced with the name of the class and the superclass that the created method is added to. 
			 * This is done whichever map is null or not. To prevent this replacement, call ClassMap.fix()."
			 */
			ClassMap classMap = JavassistUtil.fixedClassMap(classToExtend, classPool);
			
			/**
			 * In the case that this class map is not included the following problem will occur:
			 * - Situation: The overriding method contains references (its return value for example) to the parent class where the extended method was originally located
			 * - Consequence: All these references to the parent class will be substituted by the instrumented class
			 * - Problem: For some reason, call to this method will throw at runtime an AbstractMethodError.
			 */
			CtMethod ctCopiedMethod = CtNewMethod.copy(ctMethod, targetClass, classMap);
			JavassistUtil.copyGenericSignature(ctCopiedMethod, ctMethod);
			JavassistUtil.copyAnnotationsAttribute(ctCopiedMethod, ctMethod, classMap);
				
			
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
			methodCodeBuilder.append(invokerClass.getCanonicalName()+" methodInvoker = new "+invokerClass.getCanonicalName()+"("+LOGIC_ENGINE_CONFIG_FIELD_NAME+"); ");

			methodCodeBuilder.append("result = "+"methodInvoker"+"."+invokerMethodName+"(this, thisMethod, args); ");
			
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
