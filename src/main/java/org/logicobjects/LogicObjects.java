package org.logicobjects;

import static java.util.Arrays.asList;

import java.net.URL;
import java.util.Set;

import org.jcategory.JCategory;
import org.jpc.JpcImpl;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.provider.PrologEngineProvider;
import org.jpc.term.Term;
import org.logicobjects.core.ClassPathContext;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.methodadapter.methodresult.solutioncomposition.WrapperAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author scastro
 *
 */
public class LogicObjects extends JpcImpl {
	
	private static Logger logger = LoggerFactory.getLogger(LogicObjects.class);
	private static LogicObjects logicObjects;

	public static LogicObjects getDefault() {
		return logicObjects;
	}
	
	private static void bootstrapLogicObjects() {
		logger.info("Bootstrapping " + LogicObjectsPreferences.LOGIC_OBJECTS_NAME + " ... ");
		long startTime = System.nanoTime();
		logicObjects = new LogicObjects();
		long endTime = System.nanoTime();
		long total = (endTime - startTime)/1000000;
		logger.info("Done in " + total + " milliseconds");
	}
	
	public static <T> T newLogicObject(Class<T> clazz, Object... params) {
		return logicObjects.getLogicObjectFactory().create(clazz, params);
	}
	
	public static <T> T newLogicObject(Object declaringObject, Class<T> clazz, Object... params) {
		return logicObjects.getLogicObjectFactory().create(declaringObject, clazz, asList(params));
	}
	
	static {
		bootstrapLogicObjects();
	}
	

	
	
	private final JCategory categorization;
	private final LogicObjectFactory logicObjectFactory;
	private ClassPathContext context;
	private final LogicObjectsPreferences preferences;
	
	
	private LogicObjects() {
		this(new JCategory());
	}
	
	private LogicObjects(JCategory categorization) {
		this(categorization, new LogicObjectsPreferences());
	}
	
	private LogicObjects(JCategory categorization, LogicObjectsPreferences preferences) {
		this.categorization = categorization;
		this.preferences = preferences;
		logicObjectFactory = new LogicObjectFactory();
	}
	
	
	public LogicObjectFactory getLogicObjectFactory() {
		return logicObjectFactory;
	}
	
	public LogicObjectsPreferences getPreferences() {
		return preferences;
	}
	
	public ClassPathContext getClassPathContext() {
		if(context == null) {
			context = ClassPathContext.forCaller();
		}
		return context;
	}

	public void setClassPathContext(ClassPathContext context) {
		this.context = context;
	}
	
	public void addSearchFilter(String packageName) {
		getClassPathContext().addPackage(packageName);
	}

	public void addSearchUrl(URL url) {
		getClassPathContext().addUrls(url);
	}

	public Class<?> findLogicClass(String logicName, int args) {
		return getClassPathContext().findLogicClass(logicName, args);
	}
	
	public Class findLogicClass(Term term) {
		return getClassPathContext().findLogicClass(term);
	}
	
	public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		return getClassPathContext().getCompositionAdapters();
	}
	


	public <T extends PrologEngine> T getPrologEngine(String categoryName) {
		PrologEngineProvider<T> provider = categorization.forName(categoryName).<PrologEngineProvider<T>>getProperty(PrologEngineProvider.class).get();
		return provider.getPrologEngine();
	}

}
