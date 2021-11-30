package com.obdasystems.sparqling.parsers.graphol.edge.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLEdgePoint;
import org.semanticweb.owlapi.model.OWLAnnotation;

import java.util.List;
import java.util.Set;

public class GraphOLOntologySameAsEdge extends GraphOLOntologyAxiomEdge {

	public GraphOLOntologySameAsEdge() {
		super();
	}
	
	public GraphOLOntologySameAsEdge(List<GraphOLEdgePoint> pointList, String type, String id, String srcId, String trgId, Set<OWLAnnotation> annotations) {
		super(pointList, type, id, srcId, trgId, annotations);
	}
}
