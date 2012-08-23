package org.reflectiveutils.test;

import static junit.framework.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.test.FixtureGenerics.Class6;
import org.reflectiveutils.test.FixtureGenerics.Class7;
import org.reflectiveutils.test.FixtureGenerics.MyMap;
import org.reflectiveutils.test.FixtureGenerics.MyMap3;
import org.reflectiveutils.test.FixtureGenerics.MyMap4;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;


/**
 * 
 * @author sergioc78
 *
 */
public class TestReflectiveUtils {

	@Test
	public void testUnifying() {
		Type ancestorType = MyMap3.class.getGenericSuperclass();
		Type descendantType = MyMap4.class;
		
		GenericsUtil util = new GenericsUtil();
		Map<TypeVariable, Type> typeVars = util.unify(ancestorType, descendantType);
		System.out.println("Ancestor type: " + ancestorType);
		System.out.println("Descendant type: " + descendantType);
		System.out.println("Unified type variables" + typeVars);
	}
	
	@Test
	public void testFindAncestorTypeParametersMap() {
		GenericsUtil util = new GenericsUtil();
		Map<TypeVariable, Type> typeArgumentsMap = util.findAncestorTypeParametersMap(MyMap.class, Class6.class);
		System.out.println("Arguments map: " + typeArgumentsMap);
		
		AbstractTypeWrapper typeWrapper1 = AbstractTypeWrapper.wrap(typeArgumentsMap.get(MyMap.class.getTypeParameters()[0]));
		AbstractTypeWrapper typeWrapper2 = AbstractTypeWrapper.wrap(typeArgumentsMap.get(MyMap.class.getTypeParameters()[1]));
		
		System.out.println(typeWrapper1);
		System.out.println(typeWrapper2);
		
		assertEquals(typeWrapper1.asClass(), Map.class);
		assertEquals(typeWrapper2.asClass(), List.class);
		
		System.out.println(typeArgumentsMap.get(MyMap.class.getTypeParameters()[0]));
		System.out.println(typeArgumentsMap.get(MyMap.class.getTypeParameters()[1]));
	}
	
	
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
			f = FixtureGenerics.class.getField("myField");
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
