package org.logicobjects.test.fixture;

import org.logicobjects.adapter.TextToTermAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;

@LObject
public abstract class MyRawQueries {

	@LSolution("L")
	@LQuery("L=$1")
	public abstract int returnsParameter(int p);
	
	@LSolution("L")
	@LQuery("${1}=L")
	public abstract int intMethod1();
	
	@LSolution("L")
	@LQuery("1=L")
	public abstract int intMethod2();
	
	@LSolution("L")
	@LQuery("${true}=L")
	public abstract boolean trueMethod1();
	
	@LSolution("L")
	@LQuery("true=L")
	public abstract boolean trueMethod2();
	
	@LSolution("L")
	@LQuery("${false}=L")
	public abstract boolean falseMethod1();
	
	@LSolution("L")
	@LQuery("false=L")
	public abstract boolean falseMethod2();
	
	@LQuery("${1==1}")
	public abstract boolean shouldSucceed();
	
	@LQuery("${1==2}")
	public abstract boolean shouldFail();
	
	@LSolution("FlagValue")
	@LQuery(predicate = "current_prolog_flag", args={"dialect", "FlagValue"})
	public abstract String prologDialect();
	
	@LSolution("FlagValue")
	@LQuery(predicate = "$1", args={"dialect", "FlagValue"})
	public abstract String customMethodNamePrologDialect(String prologDialectString);
	
	@LSolution("FlagValue")
	@LQuery(args={"$1", "FlagValue"})
	public abstract String currentPrologFlag(String flagName);
	
	@LQuery
	public abstract boolean currentPrologFlag(String flagName, String flagValue);
	
	@LQuery("$$")
	public abstract boolean scripting(@LTermAdapter(TextToTermAdapter.class) String query);
	
}
