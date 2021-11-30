package com.obdasystems.sparqling.parsers.graphol.exception;

public class GraphOLUnknownPrefixException extends Exception {
	
	
	public GraphOLUnknownPrefixException() {
		super();
	}

	public GraphOLUnknownPrefixException(String msg) {
		super(msg);
	}
	
	public GraphOLUnknownPrefixException(String prefix, String simpleName) {
		super();
		String msg = "Found ontology element " + prefix + ":" +simpleName + " whose prefix is not declared in the current document [prefix=\'"+ prefix + "\']"; 
	}
}
