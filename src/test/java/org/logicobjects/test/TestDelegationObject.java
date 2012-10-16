package org.logicobjects.test;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.test.fixture.MyList;
import static org.junit.Assert.*;
import static org.logicobjects.LogicObjects.*;

public class TestDelegationObject extends LocalLogicTest {

	@Test
	public void testAddFirst() {
		MyList logicList = newLogicObject(MyList.class);
		logicList.addAll(Arrays.<String>asList("a", "b", "c"));
		List<String> newList = logicList.addFirst("x");
		assertEquals(logicList.size() + 1, newList.size()); //succeeds since newList has one member more than logicList
		assertEquals(newList.size(), 4);
		assertEquals(newList.get(0), "x");
		System.out.print("Initial list: " + logicList);
		System.out.println(". Appended list: " + newList);
	}

	@Test
	public void testLength() {
		MyList logicList = newLogicObject(MyList.class);
		logicList.addAll(Arrays.<String>asList("a", "b", "c"));
		assertEquals(logicList.length(), logicList.membersLength());
		System.out.println("length: " + logicList.length());
		System.out.println("length2: " + logicList.membersLength());
		
	}
	
	@Test
	public void testAppendAllCombinations() {
		MyList logicList = newLogicObject(MyList.class);
		logicList.addAll(Arrays.<String>asList("a", "b", "c"));
		//List<String> l = Arrays.<String>asList("a", "b", "c", "d", "e");
		List<List<List<String>>> allResults = logicList.appendCombinations();
		System.out.println("all results of append(A, B, "+ logicList + "): " + allResults);
		for(int i=0; i<allResults.size(); i++) {
			System.out.println("Result  "+ (i+1) + ": " + allResults.get(i));
		}
	}
	
	@Test
	public void testAppend() {
		MyList logicList = newLogicObject(MyList.class);
		logicList.addAll(Arrays.<String>asList("a", "b", "c"));
		System.out.println("append result: " + logicList.appendList(Arrays.<String>asList("d", "e")));
	}

	@Test
	public void testAppendCustom() {
		MyList logicList = newLogicObject(MyList.class);
		logicList.addAll(Arrays.<String>asList("a", "b", "c"));
		System.out.println("append custom1: " + logicList.appendCustom());
		System.out.println("append custom2: " + logicList.appendCustom2(Arrays.<String>asList("d", "e"), "X"));
		System.out.println("append custom2: " + logicList.appendCustom3(Arrays.<String>asList("d", "e"), "X"));
	}

}
