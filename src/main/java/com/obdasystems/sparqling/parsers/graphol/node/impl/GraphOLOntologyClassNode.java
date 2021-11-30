package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLClassExpressionStartingNodeI;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLOntologyClassNode extends GraphOLOntologyIRIElementNode implements GraphOLClassExpressionStartingNodeI {
	
	public GraphOLOntologyClassNode() {
		super();
	}
	
	public GraphOLOntologyClassNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, String iri, 
			String nameSpace, String simpleName) {
		super(nodeLabel,nodeGeometry,type,color,id, iri, nameSpace, simpleName);
	}
}
