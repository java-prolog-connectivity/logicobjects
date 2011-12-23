package org.reflectiveutils.test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FixtureGenerics {

	public static interface MyMap<X,Y> extends Map<Y,X> {} 
	
	public static abstract class Class1<O,P,Q> implements MyMap<O,Q>{}
		
	public static abstract class Class2<R,S> extends Class1<R, String, S> {}
	
	public static abstract class Class3<U> extends Class2<U,List<String>> {}
	
	public static abstract class Class4<W> extends Class2<Set<String>,W> {}
	
	public static abstract class Class5 extends Class3<Map> {}
	
	public static abstract class Class6 extends Class5 {}
	
	public Class4<Iterator<Map<?,String>>> class4;
}
