package org.jpc.term;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

/**
 * A class reifying a logic atom
 * @author scastro
 *
 */
public class Atom extends Term {

	public static final Term TRUE_TERM = new Atom("true");
	public static final Term FALSE_TERM = new Atom("false");
	
	protected final String name;
	
	/**
	 * @param   name   the Atom's name (unquoted)
	 */
	public Atom(String name) {
		checkNotNull(name);
		this.name = name;
	}
	
	public String name() {
		return name;
	}

	@Override
	public List<Term> args() {
		return Collections.emptyList();
	}

	@Override
	public boolean hasFunctor(Term nameTerm, int arity) {
		return equals(nameTerm) && arity == 0;
	}
	
	public boolean equals(Object obj) {
		return (this == obj || (obj instanceof Atom && name.equals(((Atom)obj).name)));
	}

}
