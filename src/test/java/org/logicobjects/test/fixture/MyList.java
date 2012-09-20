package org.logicobjects.test.fixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LComposition;
import org.logicobjects.lib.LList;


public abstract class MyList extends LList<String> {
	
	public MyList() {
		//addAll(Arrays.<String>asList("a", "b", "c"));
	}

	@LSolution("L")
	@LMethod(args={"$0", "L"})
	public abstract int length();

	@LMethod(name = "member", args={"_", "$0"})
	public abstract int membersLength();
	
	@LExpression
	//@LSolution(".($1, $0)") //this will produce the same effect
	@LSolution("[$1|$0]")
	public abstract List<String> addFirst(String s);
	
	
	
	
	@LSolution("L")
	@LMethod(name = "append", args={"$0", "$1", "L"})
	public abstract List<String> appendList(List<String> list);
	
	@LComposition
	@LSolution("[A, B]")
	@LMethod(name = "append", args={"A", "B", "$0"})
	public abstract List<List<List<String>>> appendCombinations();
	
	
	
	@LSolution("L")
	@LMethod(name = "append", args={"$0", "${java.util.Arrays.asList(new String[] {\"d\", \"e\"})}", "L"})
	public abstract List<String> appendCustom();
	
	@LSolution(".(${$2}, L)")
	@LMethod(name = "append", args={"$0", "$1", "L"})
	public abstract List<String> appendCustom2(List<String> list, Object o);
	
	@LSolution(".($2, L)")
	@LMethod(name = "append", args={"$0", "$1", "L"})
	public abstract List<String> appendCustom3(List<String> list, Object o);

}
