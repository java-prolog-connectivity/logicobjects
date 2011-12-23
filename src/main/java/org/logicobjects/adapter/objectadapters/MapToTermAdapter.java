package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptingContext;

import jpl.Compound;
import jpl.Term;

public class MapToTermAdapter extends ObjectToTermAdapter<Map> {

	@Override
	public Term adapt(Map map, AdaptingContext adaptingContext) {
		return new ObjectToTermAdapter().adapt(map.entrySet(), adaptingContext);
	}
	
	public static class EntryToTermAdapter extends ObjectToTermAdapter<Entry> {
		@Override
		public Term adapt(Entry entry, AdaptingContext adaptingContext) {
			return new Compound("=", new Term[] {new ObjectToTermAdapter().adapt(entry.getKey(), adaptingContext), new ObjectToTermAdapter().adapt(entry.getValue(), adaptingContext)});
		}
	}
	
	
}


