package com.obdasystems.sparqling.parsers.graphol.impl.v2;

public class GraphOLIndividualInformation extends GraphOLOntologyEntityInformation {

	public GraphOLIndividualInformation() {
		
	}
	
	public GraphOLIndividualInformation(String iri, String type, GraphOLDescription descr) {
		super(iri, type, descr);
	}
	
	@Override
	public String toString() {
		String result = "#### Information about individual " + this.getIri() + "\n";
		result += "Description: " + this.getDescription().getDescriptionFormattedText();
		return result;
	}
}
