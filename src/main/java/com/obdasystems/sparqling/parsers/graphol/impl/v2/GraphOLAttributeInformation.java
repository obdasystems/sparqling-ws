package com.obdasystems.sparqling.parsers.graphol.impl.v2;

public class GraphOLAttributeInformation extends GraphOLOntologyEntityInformation {
	
	private boolean functional;
	
	public GraphOLAttributeInformation() {
		
	}
	
	public GraphOLAttributeInformation(String iri, String type, GraphOLDescription descr, boolean funct) {
		super(iri, type, descr);
		this.setFunctional(funct);
	}

	public boolean isFunctional() {
		return functional;
	}

	public void setFunctional(boolean functional) {
		this.functional = functional;
	}
	
	@Override
	public String toString() {
		String result = "#### Information about attribute " + this.getIri() + "\n";
		result += "Is functional? " + this.functional;
		result += "Description: " + this.getDescription().getDescriptionFormattedText();
		return result;
	}
}
