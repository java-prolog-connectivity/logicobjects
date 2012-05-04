package org.logicobjects.test.fixture;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;

@LObject(name = "user")
public abstract class MyRawQueries {

	@LSolution("L")
	@LQuery("/{1/}=L")
	public abstract int intMethod1();
	
	@LSolution("L")
	@LQuery("1=L")
	public abstract int intMethod2();
	
	@LSolution("L")
	@LQuery("/{true/}=L")
	public abstract boolean trueMethod1();
	
	@LSolution("L")
	@LQuery("true=L")
	public abstract boolean trueMethod2();
	
	@LSolution("L")
	@LQuery("/{false/}=L")
	public abstract boolean falseMethod1();
	
	@LSolution("L")
	@LQuery("false=L")
	public abstract boolean falseMethod2();
	
}
