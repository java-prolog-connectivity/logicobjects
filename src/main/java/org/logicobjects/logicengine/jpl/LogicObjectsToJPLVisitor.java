package org.logicobjects.logicengine.jpl;

import java.util.List;
import org.logicobjects.term.Term;
import org.logicobjects.term.Atom;
import org.logicobjects.term.Compound;
import org.logicobjects.term.FloatTerm;
import org.logicobjects.term.IntegerTerm;
import org.logicobjects.term.LException;
import org.logicobjects.term.Variable;
import org.logicobjects.util.termvisitor.TermTransformationVisitor;

public class LogicObjectsToJPLVisitor extends TermTransformationVisitor<jpl.Term> {

	@Override
	protected jpl.Term doTransform(Term source, List<jpl.Term> transformedChildren) {
		Class logicObjectClass = source.getClass();
		jpl.Term transformed = null;
		if(logicObjectClass.equals(Variable.class)) {
			Variable variable = (Variable) source;
			transformed = new jpl.Variable(variable.name());
		} else if(logicObjectClass.equals(IntegerTerm.class)) {
			IntegerTerm integerTerm = (IntegerTerm) source;
			transformed = new jpl.Integer(integerTerm.longValue());
		}else if(logicObjectClass.equals(FloatTerm.class)) {
			FloatTerm floatTerm = (FloatTerm) source;
			transformed = new jpl.Float(floatTerm.doubleValue());
		} else if(logicObjectClass.equals(Atom.class)) {
			Atom atom = (Atom) source;
			transformed = new jpl.Atom(atom.name());
		} else if(logicObjectClass.equals(Compound.class)) {
			Compound compound = (Compound) source;
			transformed = new jpl.Compound(compound.name(), (jpl.Term[]) transformedChildren.toArray(new jpl.Term[]{}));
		} else
			throw new LException("The object " + source + " is not a logic term");
		return transformed;
	}


}
