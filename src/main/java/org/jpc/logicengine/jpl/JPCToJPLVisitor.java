package org.jpc.logicengine.jpl;

import java.util.List;

import org.jpc.JPCPreferences;
import org.jpc.JPCTransformationException;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.FloatTerm;
import org.jpc.term.IntegerTerm;
import org.jpc.term.LException;
import org.jpc.term.Term;
import org.jpc.term.Variable;
import org.jpc.termvisitor.TermTransformationVisitor;

public class JPCToJPLVisitor extends TermTransformationVisitor<jpl.Term> {

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
			Term nameTerm = compound.name();
			if(nameTerm.isAtom()) {
				transformed = new jpl.Compound(((Atom)nameTerm).name(), (jpl.Term[]) transformedChildren.toArray(new jpl.Term[]{}));
			} else {
				throw new JPCTransformationException(JPCPreferences.JPC_SHORT_NAME,
					"JPL",
					"JPL only supports atoms as names of compound terms");
			}
		} else
			throw new JPCTransformationException(JPCPreferences.JPC_SHORT_NAME,
					"JPL",
					"The object " + source + " is not a " + JPCPreferences.JPC_SHORT_NAME + " logic term");
		return transformed;
	}


}
