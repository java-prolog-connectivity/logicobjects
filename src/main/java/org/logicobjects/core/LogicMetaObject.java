package org.logicobjects.core;

/**
 * A class containing operations for logic meta-calls
 * Contains operations than objects in the logic side can receive, but that are not part of the interface of the method in the Java side
 * @author scastro
 *
 */
public class LogicMetaObject {
	
	private LogicClass logicObjectClass;
	private Object object;
	private LogicObject logicObject;
	
	public LogicMetaObject(Class logicClazz) {
		this(new LogicClass(logicClazz));
	}
	
	public LogicMetaObject(LogicClass logicObjectClass) {
		this.logicObjectClass = logicObjectClass;
	}

	public LogicMetaObject(Object object) {
		//TODO
	}
	
	//public get
	
}
