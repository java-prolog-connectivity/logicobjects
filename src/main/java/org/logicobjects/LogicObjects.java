package org.logicobjects;

import java.net.URL;
import java.util.Set;

import javassist.ClassPool;

import org.jpc.engine.prolog.driver.AbstractPrologEngineDriver;
import org.jpc.term.Term;
import org.jpc.util.PrologUtil;
import org.logicobjects.adapter.methodresult.solutioncomposition.WrapperAdapter;
import org.logicobjects.core.LContext;
import org.logicobjects.core.LogicObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author scastro
 *
 */
public class LogicObjects {
	private static Logger logger = LoggerFactory.getLogger(LogicObjects.class);
	
	private static LogicObjects logicObjects = bootstrapLogicObjects();

	private static LogicObjects bootstrapLogicObjects() {
//		LogicObjects logicObjects = null;
//		logger.info("Bootstrapping " + LogicObjectsPreferences.LOGIC_OBJECTS_NAME + " ... ");
//		long startTime = System.nanoTime();
		logicObjects = new LogicObjects();
//		long endTime = System.nanoTime();
//		long total = (endTime - startTime)/1000000;
//		logger.info("Done in " + total + " milliseconds");
		return logicObjects;
	}
	
	private LogicObjectsPreferences preferences;
	//private EnginePool enginePool;
	private Configuration config;
	
	public LogicObjects() {
		this(new LogicObjectsPreferences(), new Configuration());
	}
	
	public LogicObjects(LogicObjectsPreferences preferences, Configuration config) {
		this.preferences = preferences;
		this.config = config;
		//enginePool = new EnginePool();
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
	
	
	
	public static class Configuration {
		private LogicObjectFactory logicObjectFactory;
		private LContext context;
		private ClassPool classPool;
		
		public void addSearchFilter(String packageName) {
			getContext().addPackage(packageName);
		}

		public void addSearchUrl(URL url) {
			getContext().addSearchUrls(url);
		}

		public Class findLogicClass(Term term) {
			return getContext().findLogicClass(term);
		}
		
		public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
			return getContext().getWrapperAdapters();
		}
		
		public LogicObjectFactory getLogicObjectFactory() {
			if(logicObjectFactory==null)
				logicObjectFactory = new LogicObjectFactory(getClassPool());
			return logicObjectFactory;
		}

		public ClassPool getClassPool() {
			if(classPool == null)
				classPool = ClassPool.getDefault();
			return classPool;
		}

		public void setClassPool(ClassPool classPool) {
			this.classPool = classPool;
			if(logicObjectFactory != null)
				logicObjectFactory = new LogicObjectFactory(classPool);
		}

		private LContext getContext() {
			if(context == null) {
				context = new LContext(true);
			}
			return context;
		}

		public void setContext(LContext context) {
			this.context = context;
		}
	}

}
