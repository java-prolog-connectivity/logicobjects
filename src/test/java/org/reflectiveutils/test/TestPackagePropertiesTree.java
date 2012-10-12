package org.reflectiveutils.test;

import org.junit.Test;
import org.reflectiveutils.PackagePropertiesTree;
import static junit.framework.Assert.*;

public class TestPackagePropertiesTree {

	@Test
	public void testPackageName() {
		PackagePropertiesTree root = new PackagePropertiesTree();
		assertEquals(root.getPackageName(), "");
		PackagePropertiesTree fragment1 = new PackagePropertiesTree("p1", root);
		assertEquals(fragment1.getPackageName(), "p1");
		PackagePropertiesTree fragment2 = new PackagePropertiesTree("p2", fragment1);
		assertEquals(fragment2.getPackageName(), "p1.p2");
	}
	
	@Test
	public void testAddProperty() {
		PackagePropertiesTree root = new PackagePropertiesTree();
		String packageP1 = "p1";
		String packageP2 = "p1.p2";
		String packageP4 = "p3.p4";
		String rootProperty = "rootProperty";
		String rootPropertyValue = "rootPropertyValue";
		String p1Property = "p1Property";
		String p1PropertyValue = "p1PropertyValue";
		String p2Property = "p2Property";
		String p2PropertyValue = "p2PropertyValue";
		String p4Property = "p4Property";
		String p4PropertyValue = "p4PropertyValue";
		
		assertNull(root.findProperty("", rootProperty));
		assertNull(root.findProperty(packageP1, rootProperty));
		assertNull(root.findProperty(packageP2, rootProperty));
		
		root.addProperty("", rootProperty, rootPropertyValue, false);
		root.addProperty(packageP1, p1Property, p1PropertyValue, false);
		root.addProperty(packageP2, p2Property, p2PropertyValue, false);
		root.addProperty(packageP4, p4Property, p4PropertyValue, false);
		
		assertEquals(root.findProperty("", rootProperty), rootPropertyValue);
		assertEquals(root.findProperty(packageP1, p1Property), p1PropertyValue);
		assertEquals(root.findProperty(packageP2, p2Property), p2PropertyValue);
		assertEquals(root.findProperty(packageP2, p1Property), p1PropertyValue); //the property is not defined in p2, so it should inherit from p1
		assertEquals(root.findProperty(packageP4, p4Property), p4PropertyValue);
		
		//now let's override one property in one subpackage
		root.addProperty(packageP2, p1Property, p2PropertyValue, false);
		assertEquals(root.findProperty(packageP2, p1Property), p2PropertyValue);
		
		//override the same property allowing overrides
		root.addProperty(packageP2, p1Property, p2PropertyValue, true);
		
		try {
			//attempting to override the same property without allowing overrides
			root.addProperty(packageP2, p1Property, p2PropertyValue, false);
			fail("Expected exception not thrown when overridding package property");
		} catch(Exception e) {
			//expected
		}
	}

}
