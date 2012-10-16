package org.logicobjects.logicengine.jpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.logicobjects.term.Term;
import org.logicobjects.term.Atom;
import org.logicobjects.term.Compound;
import org.logicobjects.term.FloatTerm;
import org.logicobjects.term.IntegerTerm;
import org.logicobjects.term.LException;
import org.logicobjects.term.Variable;
import org.logicobjects.util.termvisitor.AbstractTransformationVisitor;

public class JPLToLogicObjectsVisitor extends AbstractTransformationVisitor {

	@Override
	protected Term doTransform(Object source, List transformedChildren) {
		Class jplClass = source.getClass();
		Term transformed = null;
		if(jplClass.equals(jpl.Variable.class)) {
			jpl.Variable variable = (jpl.Variable) source;
			transformed = new Variable(variable.name());
		} else if(jplClass.equals(jpl.Integer.class)) {
			jpl.Integer integerTerm = (jpl.Integer) source;
			transformed = new IntegerTerm(integerTerm.longValue());
		}else if(jplClass.equals(jpl.Float.class)) {
			jpl.Float floatTerm = (jpl.Float) source;
			transformed = new FloatTerm(floatTerm.doubleValue());
		} else if(jplClass.equals(jpl.Atom.class)) {
			jpl.Atom atom = (jpl.Atom) source;
			transformed = new Atom(atom.name());
		} else if(jplClass.equals(jpl.Compound.class)) {
			jpl.Compound compound = (jpl.Compound) source;
			transformed = new Compound(compound.name(), transformedChildren);
		} else
			throw new LException("The object " + source + " is not a JPL logic term");
		return transformed;
	}
	
	@Override
	protected List getChildren(Object source) {
		if(source.getClass().equals(jpl.Compound.class)) {
			jpl.Compound compound = (jpl.Compound) source;
			return Arrays.asList(compound.args());
		} else
			return Collections.emptyList();
	}
}
