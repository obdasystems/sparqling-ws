package com.obdasystems.sparqling.parsers.graphol.impl.v2;

public class GraphOLClassInformation extends GraphOLOntologyEntityInformation {

	public GraphOLClassInformation() {
		
	}
	
	public GraphOLClassInformation(String iri, String type, GraphOLDescription descr) {
		super(iri, type, descr);
	}
	
	
	@Override
	public String toString() {
		String result = "#### Information about class " + this.getIri() + "\n";
		result += "Description: " + this.getDescription().getDescriptionFormattedText();
		return result;
	}
}
