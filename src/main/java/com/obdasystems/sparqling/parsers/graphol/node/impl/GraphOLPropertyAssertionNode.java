package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLPropertyAssertionNode extends GraphOLConstructorNode {

	private String firstEdgeID;
	private String secondEdgeID;
	
	public GraphOLPropertyAssertionNode() {
		
	}
	
	public GraphOLPropertyAssertionNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, 
			String firstId, String secId) {
		super(nodeLabel, nodeGeometry, type, color, id);
		this.firstEdgeID = firstId;
		this.secondEdgeID = secId;
	}

	public String getFirstEdgeID() {
		return firstEdgeID;
	}

	public String getSecondEdgeID() {
		return secondEdgeID;
	}
}
