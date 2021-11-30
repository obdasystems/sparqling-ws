package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLClassExpressionStartingNodeI;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLDomainExistentialNode extends GraphOLConstructorNode implements GraphOLClassExpressionStartingNodeI {
	
	public GraphOLDomainExistentialNode() {
		
	}
	
	public GraphOLDomainExistentialNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id) {
		super(nodeLabel, nodeGeometry, type, color, id);
	}
}
