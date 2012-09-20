package org.logicobjects.lib.examples.metro;

import java.util.List;

import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.annotation.method.LComposition;

public interface IMetro {
	//@LSolution("line(L)")
	@LComposition 
	@LMethod(name="line", args={"L"})
	public abstract List<ILine> lines();
	
	//@LSolution("line($1)")
	//@LMethod
	public abstract ILine line(String s);
	
}
