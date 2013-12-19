package org.logicobjects;

import java.net.URL;
import java.util.Set;

import org.jgum.JGum;
import org.jpc.DefaultJpc;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.provider.PrologEngineProvider;
import org.jpc.term.Term;
import org.logicobjects.core.ClassPathContext;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.methodadapter.methodresult.solutioncomposition.WrapperAdapter;


public class LogicObjectsContext extends DefaultJpc {

	private final JGum jgum;
	private final LogicObjectFactory logicObjectFactory;
	private ClassPathContext context;
	
	public LogicObjectsContext() {
		this(new JGum());
	}
	
	public LogicObjectsContext(JGum jgum) {
		this.jgum = jgum;
		logicObjectFactory = new LogicObjectFactory();
	}
	
	public void addSearchFilter(String packageName) {
		getClassPathContext().addPackage(packageName);
	}

	public void addSearchUrl(URL url) {
		getClassPathContext().addUrls(url);
	}

	public Class findLogicClass(Term term) {
		return getClassPathContext().findLogicClass(term);
	}
	
	public Set<Class<? extends WrapperAdapter>> getWrapperAdapters() {
		return getClassPathContext().getCompositionAdapters();
	}
	
	public LogicObjectFactory getLogicObjectFactory() {
		return logicObjectFactory;
	}

	public <T extends PrologEngine> T getPrologEngine(String categoryName) {
		PrologEngineProvider<T> provider = jgum.forName(categoryName).<PrologEngineProvider<T>>getProperty(PrologEngineProvider.class).get();
		return provider.getPrologEngine();
	}
	

	private ClassPathContext getClassPathContext() {
		if(context == null) {
			context = ClassPathContext.forCaller();
		}
		return context;
	}

	public void setClassPathContext(ClassPathContext context) {
		this.context = context;
	}

}
