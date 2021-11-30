package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLOntologyElementNode extends GraphOLNode {
	
	public GraphOLOntologyElementNode() {
		super();
	}
	
	public GraphOLOntologyElementNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id) {
		super(nodeLabel,nodeGeometry,type,color,id);
	}
	
	
	
}
