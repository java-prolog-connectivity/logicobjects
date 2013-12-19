package org.logicobjects.methodadapter.methodresult.eachsolution;

import java.util.Map;

import org.jpc.term.Term;
import org.logicobjects.converter.old.TermToObjectConverter;

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
		term = term.replaceVariables(bindings);
		//System.out.println("after replacing variables: "+term);
		//Type methodType = compositionAdapter.getMethod().getGenericReturnType();
		
		
		 //TODO the signature of the method needs to be changed:
		//the TermToObjectAdapter needs also the method as a parameter, to see if there is a LObjectAdapter annotation present
		return new TermToObjectConverter().adapt(term, getCompositionAdapter().getEachSolutionType()); 
		
		
		//return new TermToObjectAdapter().adapt(term, methodType);
	}

}


