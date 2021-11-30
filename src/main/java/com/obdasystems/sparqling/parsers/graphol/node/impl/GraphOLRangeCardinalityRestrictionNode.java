package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLClassExpressionStartingNodeI;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLRangeCardinalityRestrictionNode extends GraphOLConstructorNode implements GraphOLClassExpressionStartingNodeI {
	
	private int minCardinality;
	private int maxCardinality;
	
	boolean boundedMaxCardinality;	
	
	public GraphOLRangeCardinalityRestrictionNode() {
		
	}
	
	public GraphOLRangeCardinalityRestrictionNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, 
			int minCard, int maxCard) {
		super(nodeLabel, nodeGeometry, type, color, id);
		this.minCardinality = minCard;
		this.maxCardinality = maxCard;
		this.boundedMaxCardinality = true;
	}
	
	public GraphOLRangeCardinalityRestrictionNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, 
			int minCard) {
		super(nodeLabel, nodeGeometry, type, color, id);
		this.minCardinality = minCard;
		this.boundedMaxCardinality = false;
	}

	public int getMinCardinality() {
		return minCardinality;
	}

	public void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}

	public int getMaxCardinality() {
		return maxCardinality;
	}

	public void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}
	
	public boolean hasBoundedMaxCardinality() {
		return boundedMaxCardinality;
	}

	public void setBoundedMaxCardinality(boolean boundedMaxCardinality) {
		this.boundedMaxCardinality = boundedMaxCardinality;
	}
}
