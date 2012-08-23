package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import jpl.Compound;
import jpl.Term;

import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptingContext;
import org.logicobjects.util.LogicUtil;
import org.reflectiveutils.wrappertype.SingleTypeWrapper;

public class TermToMapAdapter extends TermToObjectAdapter<Map> {

	public Map adapt(Term listTerm) {
		return adapt(listTerm, ImplementationMap.getDefault().implementationFor(Map.class));
	}
	
	@Override
	public Map adapt(Term listTerm, Type type, AdaptingContext adaptingContext) {
		Map map = (Map) ImplementationMap.getDefault().instantiateObject(type);
		fillMap(listTerm, type, adaptingContext, map);//this does not seem to be the right type
		return map;
	}

	
	public Map fillMap(Term listTerm, Type type, AdaptingContext adaptingContext, Map map) {
		//SingleTypeWrapper typeWrapper = (SingleTypeWrapper) AbstractTypeWrapper.wrap(type);
		Type entryType = getEntryType(map);
		//Type[] mapTypeParameters = AbstractTypeWrapper.unwrap(new GenericsUtil().findParametersInstantiations(Map.class, type));
		for(Term termItem : LogicUtil.listToTermArray(listTerm)) {
			Entry entry = (Entry) new TermToObjectAdapter().adapt(termItem, entryType, adaptingContext);
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	public Type getEntryType(Map map) {
		SingleTypeWrapper entrySetType = new SingleTypeWrapper(map.entrySet().getClass().getGenericSuperclass());
		SingleTypeWrapper entryType = new SingleTypeWrapper(entrySetType.getActualTypeArguments()[0]);
		return entryType.getWrappedType();
	}
	
	
	public static class TermToEntryAdapter extends TermToObjectAdapter<Entry> {
		
		
		public Entry adapt(Compound term) {
			return adapt(term, Object.class, Object.class, null);
		}
		
		public Entry adapt(Compound term, Type keyType, Type valueType, AdaptingContext adaptingContext) {
			Hashtable hash = new Hashtable();
			hash.put(new TermToObjectAdapter().adapt(term.arg(1), keyType, adaptingContext), adapt(term.arg(2), valueType, adaptingContext));
			return (Entry)hash.entrySet().toArray()[0];
		}
		
	}
	

	

}






