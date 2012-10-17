package org.logicobjects;

import java.net.URL;
import java.util.Set;

import javassist.ClassPool;

import org.jpc.LogicUtil;
import org.jpc.logicengine.LogicEngineConfiguration;
import org.jpc.term.Term;
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
	
	public static final String LOGTALK_OPERATOR = "::";
	
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
	
	public static LogicEngineConfiguration getLogicEngineConfiguration(String packageName) {
		LogicEngineConfiguration logicEngineConfig = logicObjects.config.getContext().getLogicEngineConfiguration(packageName);
		return logicEngineConfig;
	}
	
	public static LogicEngineConfiguration getLogicEngineConfiguration(Package pakkage) {
		return getLogicEngineConfiguration(pakkage.getName());
	}
	
	public static LogicEngineConfiguration getLogicEngineConfiguration(Class clazz) {
		return getLogicEngineConfiguration(clazz.getPackage());
	}
	
	public static LogicUtil getLogicUtilFor(String packageName) {
		return new LogicUtil(getLogicEngineConfiguration(packageName).getEngine());
	}
	
	public static LogicUtil getLogicUtilFor(Package pakkage) {
		return new LogicUtil(getLogicEngineConfiguration(pakkage).getEngine());
	}
	
	public static LogicUtil getLogicUtilFor(Class clazz) {
		return new LogicUtil(getLogicEngineConfiguration(clazz).getEngine());
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
