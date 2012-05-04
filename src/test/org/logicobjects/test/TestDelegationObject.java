package org.logicobjects.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.fixture.MyList;


public class TestDelegationObject extends LocalLogicTest {

	@Test
	public void testAppend() {
		MyList logicList = LogicObjectFactory.getDefault().create(MyList.class);
		
		System.out.println("length: " + logicList.length());
		System.out.println("length2: " + logicList.length2());
		
		System.out.println("append first result: " + logicList.appendFirst("x"));
		
		System.out.println("append result: " + logicList.appendList(Arrays.<String>asList("d", "e")));
		
		List<String> l = Arrays.<String>asList("a", "b", "c", "d", "e");
		List<List<List<String>>> allResults = logicList.appendCombinations(l);
		System.out.println("all results of append(A, B, "+ l + "): " + allResults);
		for(int i=0; i<allResults.size(); i++) {
			System.out.println("Result  "+ (i+1) + ": " + allResults.get(i));
		}
		
		System.out.println("append custom1: " + logicList.appendCustom());
		System.out.println("append custom2: " + logicList.appendCustom2(Arrays.<String>asList("d", "e"), "X"));
		System.out.println("append custom2: " + logicList.appendCustom3(Arrays.<String>asList("d", "e"), "X"));
	}



}
