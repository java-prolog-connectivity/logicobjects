package org.logicobjects.adapter.objectadapters;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jpc.term.Term;
import org.logicobjects.adapter.TermToObjectAdapter;

public class TermToXMLGregorianCalendarAdapter extends TermToObjectAdapter<XMLGregorianCalendar>{

	@Override
	public XMLGregorianCalendar adapt(Term term) {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) new TermToCalendarAdapter().adapt(term);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
