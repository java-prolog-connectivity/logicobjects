package org.logicobjects.adapter.objectadapters;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.logicobjects.adapter.ObjectToTermAdapter;

import jpl.Compound;
import jpl.Term;

public class MapToTermAdapter extends ObjectToTermAdapter<Map> {

	@Override
	public Term adapt(Map map, Field field) {
		return new ObjectToTermAdapter().adapt(map.entrySet(), field);
	}
	
	public static class EntryToTermAdapter extends ObjectToTermAdapter<Entry> {
		@Override
		public Term adapt(Entry entry, Field field) {
			return new Compound("=", new Term[] {new ObjectToTermAdapter().adapt(entry.getKey(), field), new ObjectToTermAdapter().adapt(entry.getValue(), field)});
		}
	}
	
	
}


