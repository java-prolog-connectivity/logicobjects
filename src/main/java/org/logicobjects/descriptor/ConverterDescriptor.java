package org.logicobjects.descriptor;

import java.lang.reflect.Type;

import org.jpc.converter.JpcConverter;

/**
 * Describes an inter-language conversion using JpcConverter
 * @author sergioc
 *
 */
public class ConverterDescriptor {
	
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
