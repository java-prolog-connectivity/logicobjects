package org.reflectiveutils.test.fieldreflection;

import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.junit.Test;
import org.reflectiveutils.test.fieldreflection.Fixture.Fixture2;

import com.google.code.guava.beans.Properties;
import com.google.code.guava.beans.Property;

public class TestFieldAccess extends TestCase {

	
	
	
	@Test
	public void testPrivateRead1() {
		Fixture2 f = new Fixture2();
		try {
			//Field field = Fixture2.class.getDeclaredField("privateField1"); //getDeclaredField only detects fields in the current class, not in its ancestors
			Field field = Fixture.class.getField("privateField1"); //this fails since field1 is private
		} catch (NoSuchFieldException e) {
			try {
				Property property = Properties.getPropertyByName(f, "privateField1"); //this fails since there is no getter
				//Field field = property.getField();
			} catch(IllegalStateException e2) { //Unknown property
				System.out.println("Finished as expected");
			}
		} 
	}
	
	@Test
	public void testPrivateRead2() {
		Fixture2 f = new Fixture2();
		try {
			Field field = Fixture.class.getField("privateField2"); //this fails since field1 is private
		} catch (NoSuchFieldException e) {
			try {
				Property property = Properties.getPropertyByName(f, "privateField2"); //this succeed since there is a getter
				Field field = property.getField();
				field.setAccessible(true); //otherwise we will have an illegal access exception
				try {
					System.out.println(field.get(f));
					System.out.println("Finished as expected");
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				} 
			} catch(IllegalStateException e2) { //Unknown property
				throw new RuntimeException(e2); //we never arrive here
			}
		} 
	}
	
	@Test
	public void testPublicRead1() {
		Fixture2 f = new Fixture2();
		try {
			Property property = Properties.getPropertyByName(f, "publicField1"); //this fails since there is no getter
			//Field field = property.getField();
		} catch(IllegalStateException e2) { //Unknown property
			try {
				Field field = Fixture.class.getField("publicField1");
				//field.setAccessible(true); //not necessary since the field is public
				try {
					System.out.println(field.get(f));
					System.out.println("Finished as expected");
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				} 
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} 
		}
	}
	
	@Test
	public void testPublicRead2() {
		Fixture2 f = new Fixture2();
		try {
			Property property = Properties.getPropertyByName(f, "publicField2"); //this succeeds since there is a getter
			Field field = property.getField();
			//field.setAccessible(true); //not necessary since the field is public
			try {
				System.out.println(field.get(f));
				System.out.println("Finished as expected");
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			} 
		} catch(Exception e2) { //Unknown property
			throw new RuntimeException(e2);
		}
	}
}
