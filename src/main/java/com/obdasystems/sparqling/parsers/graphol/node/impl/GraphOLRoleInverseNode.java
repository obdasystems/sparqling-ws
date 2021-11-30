package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;
import com.obdasystems.sparqling.parsers.graphol.GraphOLObjectPropertyExpressionStartingNodeI;

public class GraphOLRoleInverseNode extends GraphOLConstructorNode implements GraphOLObjectPropertyExpressionStartingNodeI{
	
	public GraphOLRoleInverseNode() {
		
	}
	
	public GraphOLRoleInverseNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id) {
		super(nodeLabel, nodeGeometry, type, color, id);
	}
}
