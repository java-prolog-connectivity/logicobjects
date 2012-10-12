package org.logicobjects.adapter.methodresult.eachsolution;

import java.util.Map;

import jpl.Term;

import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.core.LogicEngine;
import org.logicobjects.instrumentation.LogicMethodParser;
import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.logicobjects.util.termvisitor.ReplaceVariableVisitor;

public class SolutionToLObjectAdapter extends
		EachSolutionAdapter<Object> { /* The reason this is parameterized as Object and not as Term is that though the arguments are first converted to a term, afterwards they will be converted to the return type of the method if possible */

	public SolutionToLObjectAdapter() {
	}

	@Override
	public Object adapt(Map bindings) {
		
		/*
		LogicEngine engine = LogicEngine.getDefault();
		//String arg = (String)getParameters()[0];
		String arg = getParsedLogicMethod().parsedResult; //the result of the logic method after replacing symbols and expressions
		Term term = engine.textToTerm(arg);
		*/
		Term term = getParsedLogicMethod().getEachSolutionTerm();
		
		ReplaceVariableVisitor replaceVariableVisitor = new ReplaceVariableVisitor(bindings);
		
		/*
		for(Object o : bindings.entrySet()) {
			Entry entry = (Entry)o;
		}
		*/
		
		//System.out.println("before replacing variables: "+term);
		term = replaceVariableVisitor.visit(term);
		//System.out.println("after replacing variables: "+term);
		//Type methodType = compositionAdapter.getMethod().getGenericReturnType();
		
		
		 //TODO the signature of the method needs to be changed:
		//the TermToObjectAdapter needs also the method as a parameter, to see if there is a LObjectAdapter annotation present
		return new TermToObjectAdapter().adapt(term, getCompositionAdapter().getEachSolutionType()); 
		
		
		//return new TermToObjectAdapter().adapt(term, methodType);
	}

}


