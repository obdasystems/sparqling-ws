package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLOntologyLiteralNode extends GraphOLOntologyElementNode {
	
	private String lexicalForm;
	private String datatypeIRI;
	private String language;
	
	public GraphOLOntologyLiteralNode() {
		super();
	}
	
	public GraphOLOntologyLiteralNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, 
			String lexicalForm, String datatypeIRI, String language) {
		super(nodeLabel,nodeGeometry,type,color,id);
		this.lexicalForm = lexicalForm; 
		this.datatypeIRI = datatypeIRI;
		this.language = language;
		
	}

	public String getLexicalForm() {
		return lexicalForm;
	}

	public void setLexicalForm(String lexicalForm) {
		this.lexicalForm = lexicalForm;
	}

	public String getDatatypeIRI() {
		return datatypeIRI;
	}

	public void setDatatypeIRI(String datatypeIRI) {
		this.datatypeIRI = datatypeIRI;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	
}
