package org.logicobjects.adapter.objectadapters;

import java.util.Calendar;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.term.FloatTerm;
import org.logicobjects.term.Term;

public class CalendarToTermAdapter extends ObjectToTermAdapter<Calendar> {

	@Override
	public Term adapt(Calendar calendar) {
		long timeInMilliSeconds = calendar.getTimeInMillis();
		double timeInSeconds = ((double)timeInMilliSeconds)/1000;
		return new FloatTerm(timeInSeconds);
	}

}
