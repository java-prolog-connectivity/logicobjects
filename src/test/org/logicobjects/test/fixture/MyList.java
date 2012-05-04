package org.logicobjects.test.fixture;

import java.util.Arrays;
import java.util.List;

import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LWrapper;
import org.logicobjects.lib.LList;

public abstract class MyList extends LList {
	
	public MyList() {
		addAll(Arrays.<String>asList("a", "b", "c"));
	}

	@LSolution("L")
	@LMethod(params={"$0", "L"})
	public abstract int length();

	@LMethod(name = "member", params={"_", "$0"})
	public abstract int length2();
	
	@LSolution(".($1, $0)")
	public abstract List<String> appendFirst(String s);
	
	@LSolution("L")
	@LMethod(name = "append", params={"$0", "$1", "L"})
	public abstract List<String> appendList(List<String> list);
	
	@LWrapper
	@LSolution("[A, B]")
	@LMethod(name = "append", params={"A", "B", "$1"})
	public abstract List<List<List<String>>> appendCombinations(List<String> list);
	
	
	
	@LSolution("L")
	@LMethod(name = "append", params={"$0", "/{java.util.Arrays.asList(new String[] {\"d\", \"e\"})/}", "L"})
	public abstract List<String> appendCustom();
	
	@LSolution(".(/{$2/}, L)")
	@LMethod(name = "append", params={"$0", "$1", "L"})
	public abstract List<String> appendCustom2(List<String> list, Object o);
	
	@LSolution(".($2, L)")
	@LMethod(name = "append", params={"$0", "$1", "L"})
	public abstract List<String> appendCustom3(List<String> list, Object o);

}
