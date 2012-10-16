package org.logicobjects.instrumentation;

import java.util.List;

/**
 * Encapsulates the parsing data of a logic class
 * @author scastro
 *
 */
public class LogicClassParsingData {

	private String name;
	private List<String> classArguments;
	private String argumentsAsListProperty;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getClassArguments() {
		return classArguments;
	}
	public void setClassArguments(List<String> classArguments) {
		this.classArguments = classArguments;
	}
	public String getArgumentsAsListProperty() {
		return argumentsAsListProperty;
	}
	public void setArgumentsAsListProperty(String argumentsAsListProperty) {
		this.argumentsAsListProperty = argumentsAsListProperty;
	}


	
}
