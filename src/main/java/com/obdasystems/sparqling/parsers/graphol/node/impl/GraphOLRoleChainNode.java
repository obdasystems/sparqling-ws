package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLInclusionObjectPropertyExpressionStartingNodeI;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

import java.util.List;

public class GraphOLRoleChainNode extends GraphOLConstructorNode implements GraphOLInclusionObjectPropertyExpressionStartingNodeI{
	
	private List<String> inputEdgeList;
	
	public GraphOLRoleChainNode() {
		
	}
	
	public GraphOLRoleChainNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, 
			List<String> inputEdgeList) {
		super(nodeLabel, nodeGeometry, type, color, id);
		this.inputEdgeList = inputEdgeList;
	}

	public List<String> getInputEdgeList() {
		return inputEdgeList;
	}
}
