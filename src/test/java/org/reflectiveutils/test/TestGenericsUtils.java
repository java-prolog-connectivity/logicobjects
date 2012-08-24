package org.reflectiveutils.test;

import static junit.framework.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.test.FixtureGenericsUtil.Class6;
import org.reflectiveutils.test.FixtureGenericsUtil.Class7;
import org.reflectiveutils.test.FixtureGenericsUtil.MyMap;
import org.reflectiveutils.test.FixtureGenericsUtil.MyMap3;
import org.reflectiveutils.test.FixtureGenericsUtil.MyMap4;
import org.reflectiveutils.test.FixtureGenericsUtil.MyMap5;
import org.reflectiveutils.test.FixtureGenericsUtil.MyMap6;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

/**
 * 
 * @author scastro
 *
 */
public class TestGenericsUtils {

	@Test
	public void testUnifying() {
		Type ancestorType = MyMap3.class.getGenericSuperclass(); //MyMap2<java.util.List<X>, X>
		Type descendantType = MyMap4.class; //MyMap4
		
		GenericsUtil util = new GenericsUtil();
		Map<TypeVariable, Type> typeVars = util.unifyWithDescendant(ancestorType, descendantType);
		
		TypeVariable typeVar = (TypeVariable) typeVars.keySet().toArray()[0];
		assertEquals(typeVar.getName(), "X");
		Type t = typeVars.get(typeVar);
		assertEquals(t, String.class);
		/*
		System.out.println("Ancestor type: " + ancestorType);
		System.out.println("Descendant type: " + descendantType);
		System.out.println("Unified type variables" + typeVars);
		*/
	}
	
	@Test
	public void testUnifyingArrayType() {
		Type ancestorType = MyMap5.class.getGenericSuperclass(); //MyMap2<java.util.List<X>, X>
		Type descendantType = MyMap6.class; //MyMap4
		
		GenericsUtil util = new GenericsUtil();
		Map<TypeVariable, Type> typeVars = util.unifyWithDescendant(ancestorType, descendantType);
		
		TypeVariable typeVar = (TypeVariable) typeVars.keySet().toArray()[0];
		assertEquals(typeVar.getName(), "X");
		Type t = typeVars.get(typeVar);
		assertEquals(t, String.class);
		
		System.out.println("Ancestor type: " + ancestorType);
		System.out.println("Descendant type: " + descendantType);
		System.out.println("Unified type variables" + typeVars);
		
	}
	
	@Test
	public void testFindAncestorTypeParametersMap() {
		GenericsUtil util = new GenericsUtil();
		Map<TypeVariable, Type> typeArgumentsMap = util.findAncestorTypeParametersMap(MyMap.class, Class6.class);
		//System.out.println("Arguments map: " + typeArgumentsMap);
		
		AbstractTypeWrapper typeWrapper1 = AbstractTypeWrapper.wrap(typeArgumentsMap.get(MyMap.class.getTypeParameters()[0]));
		AbstractTypeWrapper typeWrapper2 = AbstractTypeWrapper.wrap(typeArgumentsMap.get(MyMap.class.getTypeParameters()[1]));
		
		//System.out.println(typeWrapper1);
		//System.out.println(typeWrapper2);
		
		assertEquals(typeWrapper1.asClass(), Map.class);
		assertEquals(typeWrapper2.asClass(), List.class);
		
		//System.out.println(typeArgumentsMap.get(MyMap.class.getTypeParameters()[0]));
		//System.out.println(typeArgumentsMap.get(MyMap.class.getTypeParameters()[1]));
	}
	
	
	@Test
	public void testGenericInference() {
		GenericsUtil util = new GenericsUtil();

		Field f;
		try {
			f = FixtureGenericsUtil.class.getField("myField");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		ParameterizedType pt = (ParameterizedType) f.getGenericType(); //Class4<java.util.Iterator<java.util.Map<?, java.lang.String>>>
		

		Type[] descendantTypes = util.findDescendantTypeParameters(pt, Class7.class);
		assertEquals(descendantTypes.length, 2);
		assertEquals(((TypeVariable)descendantTypes[0]).getName(), "X");
		assertEquals(AbstractTypeWrapper.wrap(descendantTypes[1]).asClass(), Iterator.class);
		
	}
}
