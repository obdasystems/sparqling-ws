package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLDataPropertyExpressionStartingNodeI;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLOntologyAttributeNode extends GraphOLOntologyIRIElementNode implements GraphOLDataPropertyExpressionStartingNodeI{
	
	public GraphOLOntologyAttributeNode() {
		super();
	}
	
	public GraphOLOntologyAttributeNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, String iri, 
			String nameSpace, String simpleName) {
		super(nodeLabel,nodeGeometry,type,color,id, iri, nameSpace, simpleName);
	}
}
