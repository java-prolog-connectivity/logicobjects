package org.logicobjects.core;

/**
 * A class containing operations for logic meta-calls
 * Contains operations than objects in the logic side can receive, but that are not part of the interface of the method in the Java side
 * @author scastro
 *
 */
public class LogicMetaObject {
	
	private LogicClass logicClass;
	private Object object;
	private LogicObject logicObject;
	
	public LogicMetaObject(Class clazz) {
		this(new LogicClass(clazz));
	}
	
	public LogicMetaObject(LogicClass logicClass) {
		this.logicClass = logicClass;
	}

	public LogicMetaObject(Object object) {
		//TODO
	}
	
	//public get
	
}
