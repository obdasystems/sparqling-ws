package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLOntologyIRIElementNode extends GraphOLOntologyElementNode{
	
	protected String iri;
	protected String nameSpace;
	protected String simpleName;
	
	public GraphOLOntologyIRIElementNode() {
		super();
	}
	
	public GraphOLOntologyIRIElementNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, String iri, 
			String nameSpace, String simpleName) {
		super(nodeLabel,nodeGeometry,type,color,id);
		this.iri = iri;
		this.nameSpace = nameSpace;
		this.simpleName = simpleName;
	}
	
	public void setIri(String iri) {
		this.iri = iri;
	}
	
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	
	public void setSimpleName(String name) {
		this.simpleName = name;
	}
	
	public String getIri() {
		return this.iri;
	}
	
	public String getNameSpace() {
		return this.nameSpace;
	}
	
	public String getSimpleName() {
		return this.simpleName;
	}
	
}
