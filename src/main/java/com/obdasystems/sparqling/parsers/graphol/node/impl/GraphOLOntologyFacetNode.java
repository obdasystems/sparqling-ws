package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLOntologyFacetNode extends GraphOLOntologyElementNode {
	
	private String constrainingFacet;
	private String restrictionValue;
	private String valueDatatype;
	
	public GraphOLOntologyFacetNode() {
		super();
	}
	
	public GraphOLOntologyFacetNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, 
			String constrainingFacet, String restrictionValue) {
		super(nodeLabel,nodeGeometry,type,color,id);
		this.constrainingFacet = constrainingFacet; 
		this.restrictionValue = restrictionValue;
	}
	
	public GraphOLOntologyFacetNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, 
			String constrainingFacet, String restrictionValue, String datatype) {
		super(nodeLabel,nodeGeometry,type,color,id);
		this.constrainingFacet = constrainingFacet; 
		this.restrictionValue = restrictionValue;
		this.valueDatatype = datatype;
	}

	public String getConstrainingFacet() {
		return constrainingFacet;
	}

	public void setConstrainingFacet(String constrainingFacet) {
		this.constrainingFacet = constrainingFacet;
	}

	public String getRestrictionValue() {
		return restrictionValue;
	}

	public void setRestrictionValue(String restrictionValue) {
		this.restrictionValue = restrictionValue;
	}

	public String getValueDatatype() {
		return valueDatatype;
	}

	public void setValueDatatype(String valueDatatype) {
		this.valueDatatype = valueDatatype;
	}
}
