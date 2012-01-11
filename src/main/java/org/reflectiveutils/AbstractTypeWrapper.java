package org.reflectiveutils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.logicobjects.annotation.LObject;

import com.google.code.guava.beans.Properties;
import com.google.code.guava.beans.Property;

/*
 * The objective of this class is to reduce the amount of castings and instanceof operations that otherwise certain adapters will have to do
 * 
 */
public abstract class AbstractTypeWrapper {
	
	Type wrappedType;
	
	public AbstractTypeWrapper(Type wrappedType) {
		setWrappedType(wrappedType);
	}
	
	public Type getWrappedType() {
		return wrappedType;
	}
	
	public void setWrappedType(Type wrappedType) {
		this.wrappedType = wrappedType;
	}
	
	public abstract Class asClass();
	public abstract boolean hasActualTypeArguments();
	public abstract boolean isAssignableFrom(Type type);
	
	public void print() {
		System.out.println("TypeWrapper class: "+getClass().getName());
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
	
	
	
	public static class SingleTypeWrapper extends AbstractTypeWrapper {
		
		public SingleTypeWrapper(Type wrappedType) {
			super(wrappedType);
		}

		boolean isInterface() {
			return asClass().isInterface();
		}

		boolean isAbstract() {
			return Modifier.isAbstract(asClass().getModifiers());  //primitive type classes answer yes to this
		}

		@Override
		public boolean hasActualTypeArguments() {
			return ParameterizedType.class.isAssignableFrom(wrappedType.getClass());
		}

		public Type[] getActualTypeArguments() {
			if(hasActualTypeArguments()) {
				return ((ParameterizedType)wrappedType).getActualTypeArguments();
			} else
				return new Type[] {};
		}

		public boolean hasTypeParameters() {
			return getTypeParameters().length>0;
		}
		
		public TypeVariable[] getTypeParameters() {
			return asClass().getTypeParameters();
		}
		
		@Override
		public Class asClass() {
			if(hasActualTypeArguments())
				return (Class)((ParameterizedType)wrappedType).getRawType();
			else
				return (Class)wrappedType;
		}

		@Override
		public boolean isAssignableFrom(Type type) {
			AbstractTypeWrapper paramWrapper = AbstractTypeWrapper.wrap(type);
			if(paramWrapper instanceof VariableTypeWrapper)
				return true;
			if(!(paramWrapper instanceof SingleTypeWrapper))
				return false;
			return asClass().isAssignableFrom(SingleTypeWrapper.class.cast(paramWrapper).asClass());
		}

		@Override
		public void print() {
			super.print();
			if(isInterface())
				System.out.println("Interface");
			else if(isAbstract())
				System.out.println("Abstract class");
			else
				System.out.println("Concrete class");
			System.out.println("Class: "+asClass().getName());
			if(hasActualTypeArguments())
				System.out.println("Parameters: "+getActualTypeArguments().length);
			for(int i = 0; i<getActualTypeArguments().length; i++) {
				System.out.println("Parameter: "+i+": "+getActualTypeArguments()[i].toString());
			}
		}

/*
		@Override
		public boolean isErased() {
			return false;
		}

		@Override
		public boolean isArray() {
			return false;
		}
*/
	}
	
	
	public static class ArrayTypeWrapper extends AbstractTypeWrapper {

		public ArrayTypeWrapper(Type wrappedType) {
			super(wrappedType);
		}
		
		public static boolean isArray(Type type) {
			return ( GenericArrayType.class.isAssignableFrom(type.getClass()) || (Class.class.isAssignableFrom(type.getClass()) && ((Class)type).isArray()) );
		}
		
		@Override
		public boolean hasActualTypeArguments() {
			return GenericArrayType.class.isAssignableFrom(wrappedType.getClass());
		}
/*
		@Override
		public Type[] getParameters() {
			if(isParameterized()) {
				if( ((GenericArrayType)wrappedType).getGenericComponentType() instanceof ParameterizedType) 
					return ((ParameterizedType)((GenericArrayType)wrappedType).getGenericComponentType()).getActualTypeArguments();
				else
					return new Type[] {((GenericArrayType)wrappedType).getGenericComponentType()};
			} else
			return new Type[] {};
		}
*/
		
		public Type getComponentType() {
			if(hasActualTypeArguments()) {
				return ((GenericArrayType)wrappedType).getGenericComponentType();
			} else {
				return ((Class)wrappedType).getComponentType();
			}
				
		}
/*
		@Override
		public boolean isErased() {
			return false;
		}

		@Override
		public boolean isArray() {
			return true;
		}
*/
		
		public int dimensions() {
			int componentDimension = 0;
			if(isArray(getComponentType())) {
				componentDimension = new ArrayTypeWrapper(getComponentType()).dimensions();
			}
			return 1 + componentDimension;
		}
		
		public Type getBaseType() {
			if(isArray(getComponentType()))
				return (new ArrayTypeWrapper(getComponentType())).getBaseType();
			else
				return getComponentType();		
		}

		@Override
		public boolean isAssignableFrom(Type type) {
			AbstractTypeWrapper paramWrapper = AbstractTypeWrapper.wrap(type);
			if(paramWrapper instanceof VariableTypeWrapper)
				return true;
			if( !(paramWrapper instanceof ArrayTypeWrapper) )
				return false;
			if(!(dimensions() == ArrayTypeWrapper.class.cast(paramWrapper).dimensions()) )
				return false;
			return AbstractTypeWrapper.wrap(getBaseType()).isAssignableFrom(ArrayTypeWrapper.class.cast(paramWrapper).getBaseType());
		}

		@Override
		public Class asClass() {
			if(!hasActualTypeArguments())
				return (Class) wrappedType;
			else {
				Class componentClass = AbstractTypeWrapper.wrap(getComponentType()).asClass();
				return Array.newInstance(componentClass, 0).getClass();
			}
		}

		@Override
		public void print() {
			super.print();
			System.out.println("Class: "+asClass().getName());
			System.out.println("Dimensions: "+dimensions());
			if(hasActualTypeArguments())
				System.out.println("Parameterized array");
			System.out.println("Base type: "+getBaseType().toString());
		}
	}
	
	
	public static class VariableTypeWrapper extends AbstractTypeWrapper {

		public VariableTypeWrapper(Type wrappedType) {
			super(wrappedType);
		}



		@Override
		public boolean hasActualTypeArguments() {
			if(true) throw new UnsupportedOperationException();
			return false;
		}
/*
		@Override
		public boolean isErased() {
			return true;
		}
		
		@Override
		public Type[] getParameters() {
			if(true) throw new UnsupportedOperationException();
			return null;
		}

		@Override
		public boolean isArray() {
			if(true) throw new UnsupportedOperationException();
			return false;
		}
*/



		@Override
		public boolean isAssignableFrom(Type type) {
			return true;
		}
		
		/*
		 * Answers if wrappedType is an instanceof WildcardType
		 */
		public boolean isWildcard() {
			return wrappedType instanceof WildcardType;
		}

		@Override
		public Class asClass() {
			return Object.class;
		}
		
		public String getName() {
			if(isWildcard())
				return "?"; //no name
				
			else
				return ((TypeVariable)wrappedType).getName();
		}



		@Override
		public void print() {
			super.print();
			System.out.println("Name: "+getName());
			
		}
	}





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
		System.out.println("---------- Property name: "+propertyName);
		try {
			Property property = Properties.getPropertyByName(clazz, propertyName);
			Field field = property.getField();
			Type type = field.getGenericType();
			return type;

		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	

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
