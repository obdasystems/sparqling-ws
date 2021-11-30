package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;
import com.obdasystems.sparqling.parsers.graphol.GraphOLObjectPropertyExpressionStartingNodeI;

public class GraphOLOntologyRoleNode extends GraphOLOntologyIRIElementNode implements GraphOLObjectPropertyExpressionStartingNodeI{
	
	public GraphOLOntologyRoleNode() {
		super();
	}
	
	public GraphOLOntologyRoleNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id, String iri, 
			String nameSpace, String simpleName) {
		super(nodeLabel,nodeGeometry,type,color,id, iri, nameSpace, simpleName);
	}
}
