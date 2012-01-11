package org.logicobjects.core;

import java.lang.reflect.Field;

import jpl.Atom;
import jpl.Compound;
import jpl.Query;
import jpl.Term;

import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.FieldAdaptingContext;
import org.logicobjects.adapter.objectadapters.ArrayToTermAdapter;

import com.google.code.guava.beans.Properties;
import com.google.code.guava.beans.Property;


public class LogicObject implements ITermObject {

	public static final String LOGTALK_OPERATOR = "::";
	
	public static Compound logtalkMessage(Term object, String messageName, Term[] messageArguments) {
		Term rightTerm = messageArguments.length > 0 ? new Compound(messageName, messageArguments) : new Atom(messageName);
		return new Compound(LOGTALK_OPERATOR, new Term[] {object, rightTerm});
	}
	
	
	private String name;
	private Object[] objectParams;
	private Term[] termParams;
	//private Term asTerm;
	
	public LogicObject(Term term) {
		this(term.name(), term.args());
	}
	
	public LogicObject(String name) {
		this(name, new Object[]{});
	}
	
	public LogicObject(String name, Object[] objectParams) {
		setName(name);
		if(objectParams == null)
			objectParams = new Object[]{};
		setObjectParams(objectParams);
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getObjectParams() {
		return objectParams;
	}

	public void setObjectParams(Object[] objectParams) {
		setTermParams(ArrayToTermAdapter.arrayAsTerms(objectParams));		
		this.objectParams = objectParams;
	}

	public Term[] getTermParams() {
		return termParams;
	}

	public void setTermParams(Term[] termParams) {
		this.termParams = termParams;
	}

	@Override
	public Term asTerm() {
		if( isParametrizedObject() )
			return new Compound(getName(), getTermParams());
		else
			return new Atom(getName());
	}
	
	
	public boolean isParametrizedObject() {
		return getTermParams().length > 0;
	}
	
	public Query invokeMethod(String methodName, Object[] messageArgs) {
		Compound compound = logtalkMessage(asTerm(), methodName, ArrayToTermAdapter.arrayAsTerms(messageArgs));
		Query query = new Query(compound);
		return query;
	}

	
	

	@Override
	public String toString() {
		return asTerm().toString();
	}
	
	
	public static void setParams(Object lObject, Term term , String[] params) {
		for(int i=0; i<params.length; i++) {
			String propertyName = params[i];
			//Field field = lObject.getClass().getField(propertyName);  //remember, the commented out code fails for private fields
			Property property = Properties.getPropertyByName(lObject, propertyName);
			Field field = property.getField();
			Object fieldValue = new TermToObjectAdapter().adapt(term.arg(i+1), field.getGenericType(), new FieldAdaptingContext(field));
			//field.set(lObject, fieldValue); ////remember, the commented out code fails for private fields
			try {
				property.setValueWithSetter(lObject, fieldValue); //try to use the setter if any
			} catch(NullPointerException e) { //setter no defined
				property.setFieldValue(lObject, fieldValue);
			}
		}
	}
	
	

}
