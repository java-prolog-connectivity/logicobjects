package org.logicobjects;

import java.net.URL;
import java.util.Set;

import javassist.ClassPool;

import org.jpc.engine.prolog.driver.AbstractPrologEngineDriver;
import org.jpc.term.Term;
import org.jpc.util.PrologUtil;
import org.logicobjects.core.LContext;
import org.logicobjects.methodadapter.methodresult.solutioncomposition.WrapperAdapter;
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
		logger.trace("Bootstrapping " + LogicObjectsPreferences.LOGIC_OBJECTS_NAME + " ... ");
		long startTime = System.nanoTime();
		logicObjects = new LogicObjects();
		long endTime = System.nanoTime();
		long total = (endTime - startTime)/1000000;
		logger.trace("Done in " + total + " milliseconds");
	}
	
	private LogicObjectsPreferences preferences;
	private LogicObjectsConfiguration config;
	
	private LogicObjects() {
		this(new LogicObjectsPreferences(), new LogicObjectsConfiguration());
	}
	
	public LogicObjects(LogicObjectsPreferences preferences, LogicObjectsConfiguration config) {
		this.preferences = preferences;
		this.config = config;
	}
	
	public static <T> T newLogicObject(Class<T> clazz, Object... params) {
		return logicObjects.config.getLogicObjectFactory().create(clazz, params);
	}
	
	public static <T> T newLogicObject(Object declaringObject, Class<T> clazz, Object... params) {
		return logicObjects.config.getLogicObjectFactory().create(declaringObject, clazz, params);
	}
	
	public static AbstractPrologEngineDriver getLogicEngineConfiguration(String packageName) {
		AbstractPrologEngineDriver logicEngineConfig = logicObjects.config.getContext().getLogicEngineConfiguration(packageName);
		return logicEngineConfig;
	}
	
	public static AbstractPrologEngineDriver getLogicEngineConfiguration(Package pakkage) {
		return getLogicEngineConfiguration(pakkage.getName());
	}
	
	public static AbstractPrologEngineDriver getLogicEngineConfiguration(Class clazz) {
		return getLogicEngineConfiguration(clazz.getPackage());
	}
	
	public static PrologUtil getLogicUtilFor(String packageName) {
		return new PrologUtil(getLogicEngineConfiguration(packageName).getEngine());
	}
	
	public static PrologUtil getLogicUtilFor(Package pakkage) {
		return new PrologUtil(getLogicEngineConfiguration(pakkage).getEngine());
	}
	
	public static PrologUtil getLogicUtilFor(Class clazz) {
		return new PrologUtil(getLogicEngineConfiguration(clazz).getEngine());
	}
	
	//TODO this method should not be here
	public static void setClassPool(ClassPool classPool) {
		logicObjects.config.setClassPool(classPool);
	}
	
	public static LContext getContext() {
		return logicObjects.config.getContext();
	}
	
	public static void setContext(LContext context) {
		logicObjects.config.setContext(context);
	}
	
	public static void addSearchUrl(URL url) {
		logicObjects.config.addSearchUrl(url);
	}
	
	
	public static LogicObjectsPreferences getPreferences() {
		return logicObjects.preferences;
	}
	
	public static Class findLogicClass(Term term) {
		return logicObjects.config.findLogicClass(term);
	}
	
	public static Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		return logicObjects.config.getWrapperAdapters();
	}
	/*
	public static <T extends LogicEngineConfiguration> LogicEngine getLogicEngine(Class<T> logicEngineConfigurationClass) {
		return logicObjects.enginePool.getOrCreateLogicEngine(logicEngineConfigurationClass);
	}
	*/

}
