package org.logicobjects.descriptor;

import java.lang.reflect.Type;

import org.jpc.converter.JpcConverter;

import com.google.common.base.Optional;

/**
 * Describes an inter-language conversion using a Converter
 * @author sergioc
 *
 */
public class ConverterDescriptor {
	
	private final JpcConverter converter;
	private final Type preferedType;
	
	public ConverterDescriptor(JpcConverter converter) {
		this(converter, null);
	}
	
	public ConverterDescriptor(JpcConverter converter, Type preferedType) {
		this.converter = converter;
		this.preferedType = preferedType;
	}

	public JpcConverter getConverter() {
		return converter;
	}

	public Optional<Type> getPreferedType() {
		return Optional.fromNullable(preferedType);
	}

}
