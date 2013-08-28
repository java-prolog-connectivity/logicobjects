package org.logicobjects.descriptor;

import java.lang.reflect.Type;

import org.jpc.converter.JpcConverter;
import org.logicobjects.annotation.LConverter;
import org.logicobjects.annotation.LConverter.LConverterUtil;
import org.logicobjects.annotation.LDelegationObjectConverter;
import org.logicobjects.annotation.LDelegationObjectConverter.LDelegationObjectConverterUtil;

/**
 * Describes an inter-language conversion using JpcConverter
 * @author sergioc
 *
 */
public class ConverterDescriptor {

	public static ConverterDescriptor create(LConverter lConverter) {
		return new ConverterDescriptor(LConverterUtil.getConverterClass(lConverter), lConverter.preferedClass());
	}
	
	public static ConverterDescriptor create(LDelegationObjectConverter lConverter) {
		return new ConverterDescriptor(LDelegationObjectConverterUtil.getConverterClass(lConverter), lConverter.preferedClass());
	}
	
	private JpcConverter converter;
	private Type preferedType;
	
	public ConverterDescriptor(Class<? extends JpcConverter> converterClass) {
		try {
			this.converter = converterClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ConverterDescriptor(Class<? extends JpcConverter> converterClass, Type preferedType) {
		this(converterClass);
		this.preferedType = preferedType;
	}

	public JpcConverter getConverter() {
		return converter;
	}

	public Type getPreferedType() {
		return preferedType;
	}

}
