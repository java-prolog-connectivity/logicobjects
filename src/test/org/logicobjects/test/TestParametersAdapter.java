package org.logicobjects.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.logicobjects.adapter.methodparameters.TermParametersAdapter;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.lib.LList;
import org.logicobjects.lib.examples.MyTestList;


public class TestParametersAdapter extends AbstractLogicTest {

	@Test
	public void testExpressionsReplacement() {
		MyTestList logicList = LogicObjectFactory.getDefault().create(MyTestList.class);
	}

	@Test
	public void testAppend() {
		MyTestList logicList = LogicObjectFactory.getDefault().create(MyTestList.class);
		
		System.out.println("append result: " + logicList.append());
		System.out.println("append result: " + logicList.append01("y"));
		
		System.out.println("append result: " + logicList.append02(Arrays.<String>asList("d", "e"), "X"));
		System.out.println("append result: " + logicList.append03(Arrays.<String>asList("d", "e"), "X"));
		
		System.out.println("append result: " + logicList.append1(Arrays.<String>asList("d", "e")));
		List<List<List<String>>> allResults = logicList.append2(Arrays.<String>asList("a", "b", "c", "d", "e"));
		System.out.println("all results of append(A, B, [a,b,c,d,e]): " + allResults);
		for(int i=0; i<allResults.size(); i++) {
			System.out.println("Result  "+ (i+1) + ": " + allResults.get(i));
		}
	}

	/*
	@Test
	public void testParamsReplacement() {
		Method method;
		try {
			method = LList.class.getMethod("size");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		assertNotNull(method);
		

		MyTestList logicList = LogicObjectFactory.getDefault().create(MyTestList.class);
		TermParametersAdapter adapter = new TermParametersAdapter(logicList, method);
		adapter.setParameters(
				"$0",
				"functor($$,abc)", 
				"$1", 
				"$2", 
				"[$2, $1]"
		);
		Object adapted[] = adapter.adapt(new Object[] {"atom1", "atom2"}); //method parameters
		System.out.println("Number of parameters: "+adapted.length);
		for(Object o : adapted) {
			System.out.println("Param: "+o+" class: "+o.getClass());
		}
	}
*/
	
}
