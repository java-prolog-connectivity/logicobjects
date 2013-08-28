package org.logicobjects;

import java.net.URL;
import java.util.Set;

import javassist.ClassPool;

import org.jpc.term.Term;
import org.logicobjects.core.LContext;
import org.logicobjects.core.LogicObjectFactory;
import org.logicobjects.methodadapter.methodresult.solutioncomposition.WrapperAdapter;


public class LogicObjectsConfiguration {

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

	//TODO this method should not be here
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
