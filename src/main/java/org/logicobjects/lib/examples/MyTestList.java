package org.logicobjects.lib.examples;

import java.util.Arrays;
import java.util.List;

import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LWrapper;
import org.logicobjects.lib.LList;

@LDelegationObject(name="list", imports="library(types_loader)") //TODO verify what happens if this is located in the parent class
public abstract class MyTestList extends LList {
	
	public MyTestList() {
		addAll(Arrays.<String>asList("a", "b", "c"));
		
	}

	@LSolution("L")
	@LQuery("/{1/}=L")
	public abstract int methodExpressionTest();
	

	@LSolution("L")
	@LMethod(parameters={"$0", "/{java.util.Arrays.asList(new String[] {\"d\", \"e\"})/}", "L"})
	public abstract List<String> append();
	
	@LSolution("L")
	@LMethod(name = "append", parameters={"$0", "$1", "L"})
	public abstract List<String> append1(List<String> list);
	
	@LWrapper
	@LSolution("[A, B]")
	@LMethod(name = "append", parameters={"A", "B", "$1"})
	public abstract List<List<List<String>>> append2(List<String> list);
	
	
	

	
	
	@LSolution("L")
	@LMethod(parameters={"$0", "L"})
	public abstract int length();
	

	@LMethod(parameters={"$0", "_"})
	public abstract int member();
	
	
}
