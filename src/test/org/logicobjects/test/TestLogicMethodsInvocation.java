package org.logicobjects.test;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.lib.example.MyTestList;

public class TestLogicMethodsInvocation extends LocalLogicTest {

	

	
	@Test
	public void testList() {
		MyTestList logicList = LogicObjectFactory.getDefault().create(MyTestList.class);
		//System.out.println("Length: "+logicList.length());
	}



	/*
	@Test
	public void testX() {
		System.out.println(J.class.getSuperclass());
		
		System.out.println("Testing interfaces: ");
		for(Class clazz : J.class.getInterfaces()) {
			System.out.println(clazz);
		}
	}
	*/
	
	
	public static interface I {}
	
	public static interface J extends I {}
	
	public static class A implements J {}
	
	public static class B extends A {};
	
	
	
}
