package org.logicobjects.adapter.objectadapters;

import javax.xml.datatype.XMLGregorianCalendar;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.term.Term;

public class XMLGregorianCalendarToTermAdapter extends ObjectToTermAdapter<XMLGregorianCalendar> {

	@Override
	public Term adapt(XMLGregorianCalendar calendar) {
		return new CalendarToTermAdapter().adapt(calendar.toGregorianCalendar());
	}

}
