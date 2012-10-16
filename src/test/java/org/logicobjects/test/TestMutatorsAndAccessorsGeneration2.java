package org.logicobjects.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;
import org.logicobjects.annotation.LObject;
import org.logicobjects.core.LogicObjectFactory;
import org.reflectiveutils.BeansUtil;
import org.reflectiveutils.ReflectionUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;
import static org.logicobjects.LogicObjects.*;

/**
 * Test the special cases for the generation of mutators and accessors TODO complete!
 * @author scastro
 * 
 */
public class TestMutatorsAndAccessorsGeneration2 {

	@LObject(args = {"p"})
	public static abstract class ShouldNotDuplicateField {
		List<String> p;
		public abstract List<String> getP();
		public abstract void setP(List<String> p);
	}
	
	@LObject(args = {"p"})
	public static abstract class ShouldNotGenerateField {
		public List<String> getP() {return null;}
		public void setP(List<String> p) {}
	}
	
	@LObject(args = {"p"})
	public static abstract class ShouldNotGenerateAnything {
		List<String> p;
		public List<String> getP() {return p;}
		public void setP(List<String> p) {this.p = p;}
	}
	
	@LObject(args = {"p"})
	public static abstract class ShouldOverrideGetter {
		private List<String> p;
		List<String> getP() {return null;}
		public void setP(List<String> p) {}
	}
	
	@LObject(args = {"p"})
	public static abstract class ShouldOverrideSetter {
		private List<String> p;
		public List<String> getP() {return null;}
	    void setP(List<String> p) {}
	}
	
	@LObject(args = {"p"})
	public static abstract class ShouldOverrideGetterAndSetter {
		List<String> getP() {return null;}
	    void setP(List<String> p) {}
	}
	
	@LObject(args = {"p"})
	public static abstract class ShouldGenerateEverything {
	}
	
	private static final String TESTED_FIELD = "p";
	
	@Test
	public void testShouldNotDuplicateField() {
		Class testingClass = ShouldNotDuplicateField.class;
		Object lo = newLogicObject(testingClass);
		assertEquals(lo.getClass().getSuperclass(), testingClass);
		Field field;
		try {
			try {
				lo.getClass().getDeclaredField(TESTED_FIELD);
				throw new RuntimeException();
			} catch (NoSuchFieldException e) {
				//expected
			}
			field = testingClass.getDeclaredField(TESTED_FIELD);
			assertNotNull(field);
			Type fieldType = field.getGenericType();
			AbstractTypeWrapper fieldTypeWrapper = AbstractTypeWrapper.wrap(fieldType);
			
			Method getterMethod = lo.getClass().getDeclaredMethod(BeansUtil.getterName(TESTED_FIELD, fieldTypeWrapper.asClass()));
			assertNotNull(getterMethod);
			Type getterType = getterMethod.getGenericReturnType();
			AbstractTypeWrapper getterTypeWrapper = AbstractTypeWrapper.wrap(getterType);
			assertEquals(fieldTypeWrapper, getterTypeWrapper);
			
			Method setterMethod = lo.getClass().getDeclaredMethod(BeansUtil.setterName(TESTED_FIELD), fieldTypeWrapper.asClass());
			assertNotNull(setterMethod);
			Type setterParameterType = setterMethod.getGenericParameterTypes()[0];
			AbstractTypeWrapper setterParameterTypeWrapper = AbstractTypeWrapper.wrap(setterParameterType);
			assertEquals(fieldTypeWrapper, setterParameterTypeWrapper);
			
		} catch (SecurityException | NoSuchMethodException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	
	@Test
	public void testShouldOverrideGetter() {
		Class testingClass = ShouldOverrideGetter.class;
		Object lo = newLogicObject(testingClass);
		assertEquals(lo.getClass().getSuperclass(), testingClass);
		Field field;
		try {
			try {
				lo.getClass().getDeclaredField(TESTED_FIELD);
				throw new RuntimeException();
			} catch (NoSuchFieldException e) {
				//expected
			}
			field = testingClass.getDeclaredField(TESTED_FIELD);
			assertNotNull(field);
			Type fieldType = field.getGenericType();
			AbstractTypeWrapper fieldTypeWrapper = AbstractTypeWrapper.wrap(fieldType);
			
			Method getterMethod = testingClass.getDeclaredMethod(BeansUtil.getterName(TESTED_FIELD, fieldTypeWrapper.asClass()));
			assertNotNull(getterMethod);
			assertTrue(ReflectionUtil.hasPackageAccessModifier(getterMethod));
			Type getterType = getterMethod.getGenericReturnType();
			AbstractTypeWrapper getterTypeWrapper = AbstractTypeWrapper.wrap(getterType);
			assertEquals(fieldTypeWrapper, getterTypeWrapper);
			
			getterMethod = lo.getClass().getDeclaredMethod(BeansUtil.getterName(TESTED_FIELD, fieldTypeWrapper.asClass()));
			assertNotNull(getterMethod);
			assertTrue(ReflectionUtil.isPublic(getterMethod));
			getterType = getterMethod.getGenericReturnType();
			getterTypeWrapper = AbstractTypeWrapper.wrap(getterType);
			assertEquals(fieldTypeWrapper, getterTypeWrapper);
			
			Method setterMethod = testingClass.getDeclaredMethod(BeansUtil.setterName(TESTED_FIELD), fieldTypeWrapper.asClass());
			assertNotNull(setterMethod);
			assertTrue(ReflectionUtil.isPublic(setterMethod));
			Type setterParameterType = setterMethod.getGenericParameterTypes()[0];
			AbstractTypeWrapper setterParameterTypeWrapper = AbstractTypeWrapper.wrap(setterParameterType);
			assertEquals(fieldTypeWrapper, setterParameterTypeWrapper);
			
			try {//the setter is not overridden
				lo.getClass().getDeclaredMethod(BeansUtil.setterName(TESTED_FIELD), fieldTypeWrapper.asClass());
				throw new RuntimeException();
			} catch (NoSuchMethodException e) {
				//expected
			}
			
		} catch (SecurityException | NoSuchMethodException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Test
	public void testShouldOverrideGetterAndSetter() {
		Class testingClass = ShouldOverrideGetterAndSetter.class;
		Object lo = newLogicObject(testingClass);
		assertEquals(lo.getClass().getSuperclass(), testingClass);
		Field field;
		try {
			try {
				lo.getClass().getDeclaredField(TESTED_FIELD);
				throw new RuntimeException();
			} catch (NoSuchFieldException e) {
				//expected
			}

			
			Method getterMethod = testingClass.getDeclaredMethod(BeansUtil.nonBooleanGetterName(TESTED_FIELD));
			assertNotNull(getterMethod);
			assertTrue(ReflectionUtil.hasPackageAccessModifier(getterMethod));
			Type getterType = getterMethod.getGenericReturnType();
			AbstractTypeWrapper getterTypeWrapper = AbstractTypeWrapper.wrap(getterType);
			
			Method overriddingGetterMethod = lo.getClass().getDeclaredMethod(BeansUtil.nonBooleanGetterName(TESTED_FIELD));
			assertNotNull(overriddingGetterMethod);
			assertTrue(ReflectionUtil.isPublic(overriddingGetterMethod));
			Type overriddingGetterType = getterMethod.getGenericReturnType();
			AbstractTypeWrapper overriddingGetterTypeWrapper = AbstractTypeWrapper.wrap(overriddingGetterType);
			assertEquals(overriddingGetterTypeWrapper, getterTypeWrapper);
			
			Method setterMethod = testingClass.getDeclaredMethod(BeansUtil.setterName(TESTED_FIELD), getterTypeWrapper.asClass());
			assertNotNull(setterMethod);
			assertTrue(ReflectionUtil.hasPackageAccessModifier(setterMethod));
			Type setterParameterType = setterMethod.getGenericParameterTypes()[0];
			AbstractTypeWrapper setterParameterTypeWrapper = AbstractTypeWrapper.wrap(setterParameterType);
			assertEquals(overriddingGetterTypeWrapper, setterParameterTypeWrapper);
			
			setterMethod = lo.getClass().getDeclaredMethod(BeansUtil.setterName(TESTED_FIELD), getterTypeWrapper.asClass());
			assertNotNull(setterMethod);
			assertTrue(ReflectionUtil.isPublic(setterMethod));
			setterParameterType = setterMethod.getGenericParameterTypes()[0];
			setterParameterTypeWrapper = AbstractTypeWrapper.wrap(setterParameterType);
			assertEquals(overriddingGetterTypeWrapper, setterParameterTypeWrapper);
			
		} catch (SecurityException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Test
	public void testShouldGenerateEverything() {
		Class testingClass = ShouldGenerateEverything.class;
		Object lo = newLogicObject(testingClass);
		assertEquals(lo.getClass().getSuperclass(), testingClass);
		Field field;
		try{
			field = lo.getClass().getDeclaredField(TESTED_FIELD);
			assertNotNull(field);
			Type fieldType = field.getGenericType();
			AbstractTypeWrapper fieldTypeWrapper = AbstractTypeWrapper.wrap(fieldType);
			
			Method getterMethod = lo.getClass().getDeclaredMethod(BeansUtil.getterName(TESTED_FIELD, fieldTypeWrapper.asClass()));
			assertNotNull(getterMethod);
			Type getterType = getterMethod.getGenericReturnType();
			AbstractTypeWrapper getterTypeWrapper = AbstractTypeWrapper.wrap(getterType);
			assertEquals(fieldTypeWrapper, getterTypeWrapper);
			
			Method setterMethod = lo.getClass().getDeclaredMethod(BeansUtil.setterName(TESTED_FIELD), fieldTypeWrapper.asClass());
			assertNotNull(setterMethod);
			Type setterParameterType = setterMethod.getGenericParameterTypes()[0];
			AbstractTypeWrapper setterParameterTypeWrapper = AbstractTypeWrapper.wrap(setterParameterType);
			assertEquals(fieldTypeWrapper, setterParameterTypeWrapper);
			
		} catch (SecurityException | NoSuchMethodException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
