package org.logicobjects.test;

import java.util.Set;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.lib.example.MyTestList;

public class TestLogicObjectsCreation extends LocalLogicTest {
	@Test
	public void testCreateLogicObject() {
		MyTestList tuple = LogicObjectFactory.getDefault().create(MyTestList.class);
		System.out.println(tuple);
	}
	
	@Test
	public void testFindLogicObject() {
		Set<Class<?>> classes = LogicObjectFactory.getDefault().getContext().getLogicClasses();
		for(Class clazz : classes) {
			System.out.println(clazz);
		}
	}
	
	

}
