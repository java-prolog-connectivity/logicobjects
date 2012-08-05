package org.logicobjects.adapter.objectadapters;

import javax.xml.datatype.XMLGregorianCalendar;

import jpl.Term;

import org.logicobjects.adapter.ObjectToTermAdapter;

public class XMLGregorianCalendarToTermAdapter extends ObjectToTermAdapter<XMLGregorianCalendar> {

	@Override
	public Term adapt(XMLGregorianCalendar calendar) {
		return new CalendarToTermAdapter().adapt(calendar.toGregorianCalendar());
	}

}
