package org.logicobjects.adapter.objectadapters;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jpc.LogicUtil;
import org.jpc.term.Term;
import org.logicobjects.adapter.TermToObjectAdapter;

public class TermToCalendarAdapter extends TermToObjectAdapter<Calendar> {

	/**
	 * In SWI Prolog time stamps are expressed as floating point numbers denoting the seconds since 1/1/1970:
	 * http://www.swi-prolog.org/pldoc/doc_for?object=section(3,'4.33.1',swi('/doc/Manual/system.html'))
	 * Calendar objects in Java use integers to denote the milliseconds from the same date:
	 * http://docs.oracle.com/javase/6/docs/api/java/util/Calendar.html
	 * @param term
	 * @return
	 */
	@Override
	public Calendar adapt(Term term) {
		double timeInSeconds = LogicUtil.toDouble(term);
		long timeInMilliSeconds = (long) (timeInSeconds * 1000);
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTimeInMillis(timeInMilliSeconds);
		return gregorianCalendar;
	}
	
	
}
