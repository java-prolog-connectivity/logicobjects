package org.reflectiveutils.test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.Test;
import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.test.FixtureGenerics.Class7;

public class TestReflectiveUtils {

	@Test
	public void testGenericInference() {
		GenericsUtil util = new GenericsUtil();
		/*
		for(Type t : HashMap.class.getGenericInterfaces()) {
		//for(Type t : AbstractMap.class.getInterfaces()) {
			System.out.println(t);
		}
		
		System.out.println(hasImplementsDeclaration(HashMap.class, Cloneable.class));
		*/
		
		
		
		
		Field f;
		try {
			f = FixtureGenerics.class.getField("class4");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		ParameterizedType pt = (ParameterizedType) f.getGenericType();
		
/*
		//for(Type type : util.findTypeParameters(Map.class, Class6.class) ) {
		for(Type type : util.findAncestorTypeParameters(Class1.class, Class4.class) ) {
		//for(Type type : util.findTypeParameters(Class1.class, (Class)pt.getRawType()) ) {
		//for(Type type : util.findTypeParameters(Map.class, f.getGenericType()) ) {
			AbstractTypeWrapper wrapper = AbstractTypeWrapper.wrap(type);
			
			wrapper.print();
		}
*/	
		
		
		
		for(Type type : util.findDescendantTypeParameters(pt, Class7.class) ) {
			AbstractTypeWrapper wrapper = AbstractTypeWrapper.wrap(type);
			wrapper.print();
		}
		
	}
}
