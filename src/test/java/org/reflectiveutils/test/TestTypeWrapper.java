package org.reflectiveutils.test;

import static junit.framework.Assert.assertEquals;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.reflectiveutils.test.FixtureAbstractTypeWrapper.B;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

public class TestTypeWrapper {

	@Test
	public void testFindTypeVariables() {
		Type type = B.class.getGenericSuperclass(); //A<java.util.Map<Z, Y>, java.lang.String, Y>
		//System.out.println(type);
		
		AbstractTypeWrapper typeWrapper = AbstractTypeWrapper.wrap(type);
		List<TypeVariable> namedTypeVariables = typeWrapper.getNamedTypeVariables(); //two type variables: Z,Y
		//System.out.println("Named type variables: " + namedTypeVariables);
		assertEquals(namedTypeVariables.size(), 2);
		TypeVariable variableZ = namedTypeVariables.get(0);
		TypeVariable variableY = namedTypeVariables.get(1);
		assertEquals(variableZ.getName(), "Z");
		assertEquals(variableY.getName(), "Y");
		
		
		Map<TypeVariable, Type> map = new HashMap<TypeVariable, Type>();
		map.put(variableZ, String.class); //replace Z by String
		
		type = typeWrapper.bindVariables(map); //new bound type
		//System.out.println(type);
		typeWrapper = AbstractTypeWrapper.wrap(type);
		namedTypeVariables = typeWrapper.getNamedTypeVariables(); //only Y remains, Z was replaced by String
		assertEquals(namedTypeVariables.size(), 1);
		variableY = namedTypeVariables.get(0);
		assertEquals(variableY.getName(), "Y");
		
		
		map = new HashMap<TypeVariable, Type>();
		map.put(variableY, String.class); //replace Y by String
		
		type = typeWrapper.bindVariables(map);
		//System.out.println(type);
		typeWrapper = AbstractTypeWrapper.wrap(type);
		namedTypeVariables = typeWrapper.getNamedTypeVariables();
		assertEquals(namedTypeVariables.size(), 0); //no type variables left
	}
}
