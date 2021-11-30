package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLOntologyIndividualNode extends GraphOLOntologyIRIElementNode {
	
	public GraphOLOntologyIndividualNode() {
		super();
	}
	
	public GraphOLOntologyIndividualNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, String iri, 
			String nameSpace, String simpleName) {
		super(nodeLabel,nodeGeometry,type,color,id, iri, nameSpace, simpleName);
	}
}
