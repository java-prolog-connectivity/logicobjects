package org.reflectiveutils.wrappertype;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.logicobjects.annotation.LObject;
import org.reflectiveutils.ReflectionUtil;

/*
 * The objective of this class is to reduce the amount of castings and instanceof operations that otherwise classes dealing with Java type classes will have to do
 * 
 */
public abstract class AbstractTypeWrapper {
	
	protected Type wrappedType;
	
	public AbstractTypeWrapper(Type wrappedType) {
		setWrappedType(wrappedType);
	}
	
	public Type getWrappedType() {
		return wrappedType;
	}
	
	public void setWrappedType(Type wrappedType) {
		this.wrappedType = wrappedType;
	}
	
	public boolean isAssignableFrom(Type type) {
		return isAssignableFrom(AbstractTypeWrapper.wrap(type));
	}
	
	public abstract Class asClass();
	public abstract boolean hasTypeParameters();
	public abstract TypeVariable[] getTypeParameters();
	public abstract Type[] getActualTypeArguments();
	public abstract boolean hasActualTypeArguments();
	public abstract boolean isAssignableFrom(AbstractTypeWrapper type);
	//public abstract boolean canBindTypeVariables(Map<TypeVariable, Type> typeVariableMap);
	
	/**
	 * 
	 * @param length
	 * @return an array of the wrapped type
	 * the component type of the returned array is given by the class representation of the wrapped type
	 * This implies that for Variable Type the returned array will be Object[] and not VariableType[]
	 * This is because the class representation (in the current implementation) of variable types is Object
	 * Then the component type of an array of the wrapped type should be consistent with this class representation
	 */
	public Object[] asArray(int length) {
		return (Object[]) Array.newInstance(asClass(), length);
	}
	

	/**
	 * Collects all the type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are also included.
	 * @param types is the list collection the found type variable.
	 */
	protected abstract void collectTypeVariables(List<Type> types);
	
	/**
	 * @return all the type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are also included.
	 */
	public List<Type> getTypeVariables() {
		List<Type> typeVariables = new ArrayList<Type>();
		collectTypeVariables(typeVariables);
		return typeVariables;
	}
	
	/**
	 * @return the named type variables nested in the type. Type Variables are inserted in the order they are found from left to right. No duplicates are collected. Wildcard Types are NOT included.
	 */
	public List<TypeVariable> getNamedTypeVariables() {
		List<TypeVariable> namedTypeVariables = new ArrayList<TypeVariable>();
		for(Type typeVariable : getTypeVariables()) {
			if(typeVariable instanceof TypeVariable)
				namedTypeVariables.add((TypeVariable) typeVariable);
		}
		return namedTypeVariables;
	}
	
	/**
	 *  
	 * @return a boolean indicating if the type has named type variables
	 */
	public boolean hasNamedTypeVariables() {
		return !getNamedTypeVariables().isEmpty();
	}
	
	/**
	 * 
	 * @param typeVariableMap is a map containing mappings from type variables to concrete types
	 * @return an equivalent type to the wrapped time, with the difference that all its named type variables have been substituted by types given by a map
	 */
	public abstract Type bindVariables(Map<TypeVariable, Type> typeVariableMap);
	
	public static Type bindVariables(Type type, Map<TypeVariable, Type> typeVariableMap) {
		return AbstractTypeWrapper.wrap(type).bindVariables(typeVariableMap);
	}
	
	public static Type[] bindVariables(Type[] types, Map<TypeVariable, Type> typeVariableMap) {
		Type[] boundTypes = new Type[types.length];
		for(int i=0; i<boundTypes.length; i++)
			boundTypes[i] = bindVariables(types[i], typeVariableMap);
		return boundTypes;
	}

	@Override
	public String toString() {
		return "("+getClass().getSimpleName()+")" + getWrappedType().toString();
	}
	
	public void print() {
		System.out.println(toString());
	}
	
	public static AbstractTypeWrapper wrap(Type type) {
		if(ParameterizedType.class.isAssignableFrom(type.getClass()) || (Class.class.isAssignableFrom(type.getClass()) && !((Class)type).isArray()) )
			return new SingleTypeWrapper(type);
		else if(ArrayTypeWrapper.isArray(type))
			return new ArrayTypeWrapper(type);
		else
			return new VariableTypeWrapper(type);
	}
	
	public static AbstractTypeWrapper[] wrap(Type[] types) {
		AbstractTypeWrapper[] typeWrappers = new AbstractTypeWrapper[types.length];
		for(int i=0; i<types.length; i++) {
			typeWrappers[i] = wrap(types[i]);
		}
		return typeWrappers;
	}
	
	public static Type[] unwrap(AbstractTypeWrapper[] typeWrappers) {
		Type[] types = new Type[typeWrappers.length];
		for(int i=0; i<typeWrappers.length; i++) {
			types[i] = typeWrappers[i].getWrappedType();
		}
		return types;
	}
	
	
	
	
	
	


	
	
	
	//TODO delete
	public static void infoType(Type type) {
		try {
			AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
			System.out.println("Info for type: " + type);
			if(typeWrapper instanceof VariableTypeWrapper) {
				System.out.println("Erased type");
			} else if(typeWrapper instanceof  ArrayTypeWrapper) {
				ArrayTypeWrapper arrayTypeWrapper = (ArrayTypeWrapper)typeWrapper;
				System.out.println("Array type ");
				System.out.println("Parameterized: "+arrayTypeWrapper.hasActualTypeArguments());
				System.out.println("Base type: "+arrayTypeWrapper.getBaseType());
				System.out.println("Dimensions: "+arrayTypeWrapper.dimensions());
				System.out.println("Info component:");
				infoType(arrayTypeWrapper.getComponentType());
			} else {
				SingleTypeWrapper simpleTypeWrapper = (SingleTypeWrapper)typeWrapper;
				System.out.println("Simple type: "+simpleTypeWrapper.asClass());
				System.out.println("Interface: "+simpleTypeWrapper.isInterface());
				System.out.println("Abstract: "+simpleTypeWrapper.isAbstract());
				System.out.println("Parameterized: "+simpleTypeWrapper.hasActualTypeArguments());
				if(simpleTypeWrapper.hasActualTypeArguments()) {
					System.out.println("Info parameters:");
					for(Type t : simpleTypeWrapper.getActualTypeArguments()) {
						infoType(t);
					}
				}
				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
/*
	public static void infoType(Type type) {
		try {
			System.out.println("Info for type: " + type);
			if(Class.class.isAssignableFrom(type.getClass())) {
				Class clazz = (Class)type;
				if(clazz.isArray()) {
					System.out.println("Concrete array type");
					infoType(clazz.getComponentType());
				}
				else {
					System.out.println("Concrete simple type");
				}
			} else if( ParameterizedType.class.isAssignableFrom(type.getClass()) ) {
				System.out.println("parameterized type");
				System.out.println(((Class)((ParameterizedType) type).getRawType()).getName());
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type typeArgs[] = parameterizedType.getActualTypeArguments();
				for(int i=0; i<typeArgs.length;i++) {
					System.out.println("Processing parameter: "+(i+1));
					infoType(typeArgs[i]);
				}
			} else if(GenericArrayType.class.isAssignableFrom(type.getClass())) {
				System.out.println("paramaterized Array type");
				GenericArrayType parametrizedArrayType = (GenericArrayType) type;
				System.out.println("processing parameterized array type");
				infoType(parametrizedArrayType.getGenericComponentType());
			} else {
				System.out.println("Anonymous or type-variable type found");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	*/
	
	public static Type getType(Class clazz, String propertyName) {
		//System.out.println("---------- Property name: "+propertyName);
		try {
			Field field = ReflectionUtil.getField(clazz, propertyName);
			Type type = field.getGenericType();
			return type;

		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	

	
	
	
	
	
	//TODO delete
	public static class P<T> {
		@LObject(name = "x")
		int i;
		List l;
		List<String> ls;
		List<List<String>> lls;
		List<?> lx;
		Map<String,String> m;
		List al[];
		List<String> als[];
		List<List<String>> alls[];
		List<String> aals[][];
		String[] as;
		T[] t;
		public int getI() {
			return i;
		}
		public void setI(int i) {
			this.i = i;
		}
		public List getL() {
			return l;
		}
		public void setL(List l) {
			this.l = l;
		}
		public List<String> getLs() {
			return ls;
		}
		public void setLs(List<String> ls) {
			this.ls = ls;
		}
		public List<List<String>> getLls() {
			return lls;
		}
		public void setLls(List<List<String>> lls) {
			this.lls = lls;
		}
		public List<?> getLx() {
			return lx;
		}
		public void setLx(List<?> lx) {
			this.lx = lx;
		}
		public Map<String, String> getM() {
			return m;
		}
		public void setM(Map<String, String> m) {
			this.m = m;
		}
		public List[] getAl() {
			return al;
		}
		public void setAl(List[] al) {
			this.al = al;
		}
		public List<String>[] getAls() {
			return als;
		}
		public void setAls(List<String>[] als) {
			this.als = als;
		}
		public List<List<String>>[] getAlls() {
			return alls;
		}
		public void setAlls(List<List<String>>[] alls) {
			this.alls = alls;
		}
		public List<String>[][] getAals() {
			return aals;
		}
		public void setAals(List<String>[][] aals) {
			this.aals = aals;
		}
		public String[] getAs() {
			return as;
		}
		public void setAs(String[] as1) {
			as = as1;
		}
		public T[] getT() {
			return t;
		}
		public void setT(T[] t) {
			this.t = t;
		}
		
		
		
		
		

	}
	
	public static class C extends P {

	}

	
	public static void main(String[] args) {
		
		Type type = null;
		
		/*
		type = getType(C.class, "i");
		infoType(type);
		type = getType(C.class, "l");
		infoType(type);
		type = getType(C.class, "ls");
		infoType(type);
		type = getType(C.class, "lls");
		infoType(type);
		type = getType(C.class, "lx");
		infoType(type);
		type = getType(C.class, "m");
		infoType(type);
		type = getType(C.class, "al");
		infoType(type);
		type = getType(C.class, "als");
		infoType(type);
		type = getType(C.class, "alls");
		infoType(type);
		type = getType(C.class, "aals");
		infoType(type);
		type = getType(C.class, "t");
		infoType(type);
		*/
		/*
		type = getType(C.class, "aals");

		ArrayTypeWrapper typeWrapper = new ArrayTypeWrapper(type);
		System.out.println((Class)(typeWrapper.getComponentType().getClass()));
		Object o = Array.newInstance(typeWrapper.getComponentType().getClass(), 1);
		System.out.println(o);
		List<String> item = new ArrayList<String>();
		Array.set(o, 0, item);
		
		System.out.println(typeWrapper.getBaseType());
		*/
		type = getType(C.class, "aals");
		ArrayTypeWrapper typeWrapper = new ArrayTypeWrapper(type);
		System.out.println(typeWrapper.asClass());
		new ArrayList<String>();
		List<String>[][] l1;
		List<String>[][] l2;
		l1 = new ArrayList[1][1];
		l2 = new ArrayList[10][11];
		System.out.println(l1.getClass());
		System.out.println(l2.getClass());
		System.out.println(l1.getClass().equals(l2.getClass()));
		/*
		type = getType(C.class, "as");
		
		System.out.println(type);
		System.out.println(type.getClass());
		*/
		//ArrayType a;
		//int i[] = new int[]{};
		/*
		String s[] = new String[] {};
		System.out.println(s.getClass().getComponentType());
		System.out.println(s.getClass().getClass().getCanonicalName());
		*/
		/*
		System.out.println(  s.getClass() ) ;
		System.out.println(  s.getClass().getClass() ) ;
		System.out.println(type.equals(s.getClass()));
		*/
		

		/*
		String s[][];
		s = new String[][] {{"1", "2"}, {"1", "2", "3"}};
		int[] d = new int[] {2,3};
		s = (String[][]) Array.newInstance(String[].class, 2);
		System.out.println(s.getClass());
		*/
	}


}
