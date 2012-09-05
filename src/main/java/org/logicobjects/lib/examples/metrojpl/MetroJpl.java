package org.logicobjects.lib.examples.metrojpl;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;

public class MetroJpl {

	public static String LOADER = "logic_lib/examples_metro_load_all";
	
	public static void loadAll() {
		Term logtalkLoadTerm = new Compound("logtalk_load", new Term[]{new Atom(LOADER)});
		Query query = new Query(logtalkLoadTerm);
		query.hasSolution();
	}
	
	
	
}
