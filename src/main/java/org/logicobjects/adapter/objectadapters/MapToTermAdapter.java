package org.logicobjects.adapter.objectadapters;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.adaptingcontext.AdaptationContext;

public class MapToTermAdapter extends ObjectToTermAdapter<Map> {

	@Override
	public Term adapt(Map map, AdaptationContext adaptingContext) {
		return new ObjectToTermAdapter().adapt(map.entrySet(), adaptingContext);
	}
	
	public static class EntryToTermAdapter extends ObjectToTermAdapter<Entry> {
		@Override
		public Term adapt(Entry entry, AdaptationContext adaptingContext) {
			return new Compound("=", Arrays.asList(new ObjectToTermAdapter().adapt(entry.getKey(), adaptingContext), new ObjectToTermAdapter().adapt(entry.getValue(), adaptingContext)));
		}
	}
	
	
}


