package org.logicobjects.descriptor;

import java.util.List;

import org.logicobjects.instrumentation.LogicClassParsingData;


/**
 * This class describes a logic object: Currently these are objects annotated with @LObject and @LDelegationObject
 * @author scastro
 *
 */
public class LogicObjectDescriptor {
	
	public static final int DEFAULT_TERM_INDEX = -1;
	
	private final String name;
	private final List<String> args;
	private final List<String> imports;
	private final List<String> modules;
	private final boolean automaticImport;
	private final boolean referenceTerm;
	private final int termIndex;
	
	public LogicObjectDescriptor(String name, List<String> args, List<String> imports, List<String> modules, boolean automaticImport, boolean referenceTerm) {
		this(name, args, imports, modules, automaticImport, referenceTerm, LogicObjectDescriptor.DEFAULT_TERM_INDEX);
	}
	
	public LogicObjectDescriptor(String name, List<String> args, List<String> imports, List<String> modules, boolean automaticImport, boolean referenceTerm, int termIndex) {
		this.name = name;
		this.args = args;
		this.imports = imports;
		this.modules = modules;
		this.automaticImport = automaticImport;
		this.referenceTerm = referenceTerm;
		this.termIndex = termIndex;
	}
	
	/**
	 * 
	 * @return the id of the logic object.
	 */
	public String name() {
		return name;
	}
	
	/**
	 * 
	 * @return the arguments of the logic object.
	 */
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
	
	public boolean referenceTerm() {
		return referenceTerm;
	}
	
	public int termIndex() {
		return termIndex;
	}
	
	//TODO delete
	public LogicClassParsingData getParsingData() {
		LogicClassParsingData parsingData = new LogicClassParsingData();
		parsingData.setClassArguments(args());
		return parsingData;
	}
	
}
