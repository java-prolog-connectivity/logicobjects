package org.logicobjects.adapter.adaptercontext;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;

public abstract class AdapterContext {
	
	public static TermToObjectAdapter getTermToObjectAdapter(LObjectAdapter annotation) {
		try {
			TermToObjectAdapter objectAdapter = (TermToObjectAdapter)annotation.adapter().newInstance();
			objectAdapter.setParameters(annotation.args());
			return objectAdapter;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ObjectToTermAdapter getObjectToTermAdapter(LTermAdapter annotation) {
		try {
			ObjectToTermAdapter objectAdapter = (ObjectToTermAdapter)annotation.adapter().newInstance();
			objectAdapter.setParameters(annotation.args());
			return objectAdapter;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract TermToObjectAdapter getTermToObjectAdapter();
	
	public boolean hasTermToObjectAdapter() {
		return getTermToObjectAdapter() != null;
	}
	
	public abstract ObjectToTermAdapter getObjectToTermAdapter();
	
	public boolean hasObjectToTermAdapter() {
		return getObjectToTermAdapter() != null;
	}
	
	public abstract LObject getLogicObjectAnnotation();
	
	public boolean hasLogicObjectAnnotation() {
		return getLogicObjectAnnotation() != null;
	}
	
	
}
