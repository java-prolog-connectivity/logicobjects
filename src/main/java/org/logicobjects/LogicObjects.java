package org.logicobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author scastro
 *
 */
public class LogicObjects {
	
	private static Logger logger = LoggerFactory.getLogger(LogicObjects.class);
	private static LogicObjects logicObjects;

	static {
		bootstrapLogicObjects();
	}
	
	private static void bootstrapLogicObjects() {
		logger.info("Bootstrapping " + LogicObjectsPreferences.LOGIC_OBJECTS_NAME + " ... ");
		long startTime = System.nanoTime();
		logicObjects = new LogicObjects();
		long endTime = System.nanoTime();
		long total = (endTime - startTime)/1000000;
		logger.info("Done in " + total + " milliseconds");
	}
	
	public static LogicObjects getDefault() {
		return logicObjects;
	}
	
	private final LogicObjectsContext context;
	private final LogicObjectsPreferences preferences;
	
	
	private LogicObjects() {
		this(new LogicObjectsContext(), new LogicObjectsPreferences());
	}
	
	public LogicObjects(LogicObjectsContext context, LogicObjectsPreferences preferences) {
		this.context = context;
		this.preferences = preferences;
	}
	
	public LogicObjectsPreferences getPreferences() {
		return preferences;
	}
	
	public LogicObjectsContext getContext() {
		return context;
	}
	
	public static <T> T newLogicObject(Class<T> clazz, Object... params) {
		return logicObjects.context.getLogicObjectFactory().create(clazz, params);
	}
	
	public static <T> T newLogicObject(Object declaringObject, Class<T> clazz, Object... params) {
		return logicObjects.context.getLogicObjectFactory().create(declaringObject, clazz, params);
	}

}
