package org.reflectiveutils.test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.junit.Test;
import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.test.FixtureGenerics.Class6;

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
		for(AbstractTypeWrapper wrapper : util.findParametersInstantiations(Map.class, Class6.class) ) {
		//for(AbstractTypeWrapper wrapper : util.findParametersInstantiations(Class1.class, Class4.class) ) {
		//for(AbstractTypeWrapper wrapper : util.findParametersInstantiations(Class1.class, (Class)pt.getRawType()) ) {
		//for(AbstractTypeWrapper wrapper : util.findParametersInstantiations(Map.class, f.getGenericType()) ) {
			wrapper.print();
		}
		
		
		
		/*
		for(AbstractTypeWrapper wrapper : util.findParametersInstantiations(Map.class, Class5.class) ) {
			wrapper.print();
		}
		*/
	}
}
