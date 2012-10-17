package org.jpc.termvisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jpc.term.Term;
import org.jpc.term.Variable;

/**
 * This visitor replace variable names for another one
 */
public class ChangeVariableNameVisitor extends ReplaceVariableVisitor {

	public ChangeVariableNameVisitor(Map<String, String> map) {
		super(asVariableReplacementMap(map));
	}
	
	private static Map<String, Term> asVariableReplacementMap(Map<String, String> variableNamesMap) {
		Map<String, Term> termReplacementMap = new HashMap<>();
		for(Entry<String, String> entry : variableNamesMap.entrySet()) {
			termReplacementMap.put(entry.getKey(), new Variable(entry.getValue()));
		}
		return termReplacementMap;
	}

}
