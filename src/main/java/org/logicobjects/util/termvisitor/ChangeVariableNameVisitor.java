package org.logicobjects.util.termvisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.logicobjects.term.Term;
import org.logicobjects.term.Variable;

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
