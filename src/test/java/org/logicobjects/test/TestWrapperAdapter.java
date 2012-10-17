package org.logicobjects.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;
import org.logicobjects.adapter.methodresult.solutioncomposition.ArrayWrapperAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.ListWrapperAdapter;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.core.LogicMethod;
import org.logicobjects.instrumentation.ParsedLogicMethod;

public class TestWrapperAdapter extends LocalLogicTest {

	@LMethod
	public List<String> m() {return null;}
	
	@LMethod
	public String[] m2() {return null;}
	
	
	@Test
	public void testListWrapper() {
		Type methodType;
		Method method;
		try {
			method = getClass().getMethod("m");
			methodType = method.getGenericReturnType();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		//System.out.println(methodType);
		LogicMethod logicMethod = new LogicMethod(method);
		ParsedLogicMethod parsedLogicMethod = new ParsedLogicMethod(logicMethod, null, null, null);
		WrapperAdapter wrapperAdapter = new ListWrapperAdapter();
		wrapperAdapter.setParsedLogicMethod(parsedLogicMethod);
		assertEquals(wrapperAdapter.getEachSolutionType(), String.class);
	}
	
	
	@Test
	public void testArrayWrapper() {
		Type methodType;
		Method method;
		try {
			method = getClass().getMethod("m2");
			methodType = method.getGenericReturnType();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		//System.out.println(methodType);
		LogicMethod logicMethod = new LogicMethod(method);
		ParsedLogicMethod parsedLogicMethod = new ParsedLogicMethod(logicMethod, null, null, null);
		WrapperAdapter wrapperAdapter = new ArrayWrapperAdapter();
		wrapperAdapter.setParsedLogicMethod(parsedLogicMethod);
		assertEquals(wrapperAdapter.getEachSolutionType(), String.class);
	}
}
