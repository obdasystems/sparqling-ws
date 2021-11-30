package com.obdasystems.sparqling.parsers.graphol.edge.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLEdgePoint;
import org.semanticweb.owlapi.model.OWLAnnotation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphOLOntologyAxiomEdge extends GraphOLEdge {
	
	Set<OWLAnnotation> annotations;
	
	public GraphOLOntologyAxiomEdge() {
		super();
		this.annotations = new HashSet<OWLAnnotation>();
	}
	
	public GraphOLOntologyAxiomEdge(List<GraphOLEdgePoint> pointList, String type, String id, String srcId, String trgId, Set<OWLAnnotation> annotations) {
		super(pointList, type, id, srcId, trgId);
		this.annotations = annotations;
	}

	public Set<OWLAnnotation> getAnnotations() {
		return annotations;
	}

}
