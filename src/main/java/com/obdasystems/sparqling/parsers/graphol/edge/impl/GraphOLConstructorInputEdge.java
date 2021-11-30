package com.obdasystems.sparqling.parsers.graphol.edge.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLEdgePoint;

import java.util.List;

public class GraphOLConstructorInputEdge extends GraphOLEdge {

	public GraphOLConstructorInputEdge() {
		
	}
	
	public GraphOLConstructorInputEdge(List<GraphOLEdgePoint> pointList, String type, String id, String srcId, String trgId) {
		super(pointList, type, id, srcId, trgId);
	}
}
