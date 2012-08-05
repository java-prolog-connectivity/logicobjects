package org.logicobjects.test.fixture;

import org.logicobjects.annotation.method.LSolution;

public abstract class MyLogicExpressions {

	@LSolution("text")
	public abstract String methodExpression1();
	
	@LSolution("${\"text\"}")
	public abstract String methodExpression2();

	@LSolution("true")
	public abstract boolean methodTrue1();
	
	@LSolution("${true}")
	public abstract boolean methodTrue2();	
	
	@LSolution("false")
	public abstract boolean methodFalse1();
	
	@LSolution("${false}")
	public abstract boolean methodFalse2();	
	
}
