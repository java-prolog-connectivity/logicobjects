package org.logicobjects.descriptor;

import java.util.List;

import org.logicobjects.instrumentation.LogicClassParsingData;


/**
 * This class describes a logic object: Currently these are objects annotated with @LObject and @LDelegationObject
 * @author scastro
 *
 */
public abstract class LogicObjectDescriptor {
	
	private String name;
	private List<String> args;
	private List<String> imports;
	private List<String> modules;
	private boolean automaticImport;
	private String id;
	
	public LogicObjectDescriptor(String name, List<String> args, List<String> imports, List<String> modules, boolean automaticImport, String id) {
		this.name = name;
		this.args = args;
		this.imports = imports;
		this.modules = modules;
		this.automaticImport = automaticImport;
		this.id = id;
	}
	
	/**
	 * 
	 * @return the name of the logic object. By default it is inferred from the name of the class.
	 */
	public String name() {
		return name;
	}
	
	public List<String> args() {
		return args;
	}

	public List<String> imports() {
		return imports;
	}
	
	public List<String> modules() {
		return modules;
	}
	
	public boolean automaticImport() {
		return automaticImport;
	}
	
	public String id() {
		return id;
	}
	
	//TODO delete
	public LogicClassParsingData getParsingData() {
		LogicClassParsingData parsingData = new LogicClassParsingData();
		parsingData.setClassArguments(args());
		return parsingData;
	}
	
}
