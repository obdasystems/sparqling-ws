package com.obdasystems.sparqling.parsers.graphol.impl.v2;

public class GraphOLOntologyEntityInformation {
	
	private String iri;
	private String type;
	private GraphOLDescription descr;
	
	public GraphOLOntologyEntityInformation() {
		
	}
	
	public GraphOLOntologyEntityInformation(String iri, String type, GraphOLDescription descr) {
		this.iri = iri;
		this.type = type;
		this.descr = descr;
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public GraphOLDescription getDescription() {
		return descr;
	}

	public void setDescription(GraphOLDescription descr) {
		this.descr = descr;
	}
}
