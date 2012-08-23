package org.reflectiveutils.test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FixtureGenericsUtil {

	public static interface MyMap<X,Y> extends Map<Y,X> {} 
	
	public static abstract class Class1<O,P,Q> implements MyMap<O,Q>{}
		
	public static abstract class Class2<R,S> extends Class1<R, String, S> {}

	public static abstract class Class3<U> extends Class2<U,List<U>> {}
	
	public static abstract class Class4<W> extends Class3<Map> {}
	
	
	public static abstract class Class5 extends Class4<Map> {}
	
	public static abstract class Class6 extends Class5 {}
	
	public static abstract class Class7<X, Y> extends Class4<Y> {}
	
	public Class4<Iterator<Map<?,String>>> myField;
	
	
	
	
	
	
	public static abstract class MyMap2<X, Y> implements Map<List<X>, Y> {}
	
	public static abstract class MyMap3<X> extends MyMap2<List<X>, X> {}
	
	public static abstract class MyMap4 extends MyMap3<String> {}
	
	public static abstract class MyMap5<X> extends MyMap2<X[], X> {}
	
	public static abstract class MyMap6 extends MyMap5<String> {}
}

