package org.logicobjects.term;

/**
 * A class reifying a logic atom
 * @author scastro
 *
 */
public class Atom extends Compound {

	public static final Term TRUE_TERM = new Atom("true");
	public static final Term FALSE_TERM = new Atom("false");
	
	/**
	 * @param   name   the Atom's name (unquoted)
	 */
	public Atom(String name) {
		super(name);
	}
	
	
}
