package org.logicobjects.experimental;

import java.lang.reflect.Method;
//to delete
public class ExperimentsMethodReflection {

	public static void main(String[] args) {
		int i = 0;
		Class clazz = int.class;
		Object o[] = new Object[] {i};
		
		System.out.println(o[0].getClass());
		System.out.println(int.class);
		System.out.println(int.class.getSimpleName());
		System.out.println(clazz.getCanonicalName());
		
		
	}
	
	public void m() {
		
	}
	
	public void m(int n) {
		
	}
	
	public void m(Object o) {
		
	}
	
	public void m(Object o, String s) {
		
	}
	

	
	public void test() {
		try {
			
			//Method m1 = this.getClass().getMethod("m");
			Method m1 = this.getClass().getMethod("m", null);
			//Method m1 = this.getClass().getMethod("m", new Class[] {});
			System.out.println(m1.toString());
			//Method m2 = this.getClass().getMethod("m", int.class);
			Method m2 = this.getClass().getMethod("m", new Class[] {int.class});
			System.out.println(m2.toString());
			//Method m3 = this.getClass().getMethod("m", Object.class);
			Method m3 = this.getClass().getMethod("m", new Class[] {Object.class});
			System.out.println(m3.toString());
			//Method m4 = this.getClass().getMethod("m", Object.class, String.class);
			Method m4 = this.getClass().getMethod("m", new Class[] {Object.class, String.class});
			System.out.println(m4.toString());
			//System.out.println(asNewClassArrayString(new Class[] {Object.class, String.class}));
			//System.out.println(asNewClassArrayString(m4.getParameterTypes()));
			//m3.getParameterTypes();
			
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
		
	}
	/*
	public static void main(String[] args) {
		new ExperimentsMethodReflection().test();
	}*/
	
}
