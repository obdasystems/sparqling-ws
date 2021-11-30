package com.obdasystems.sparqling.parsers.graphol.edge.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLEdgeI;
import com.obdasystems.sparqling.parsers.graphol.GraphOLEdgePoint;

import java.util.List;

public class GraphOLEdge implements GraphOLEdgeI {
	
	private List<GraphOLEdgePoint> pointList;
	private String type;
	private String id;
	private String sourceNodeId;
	private String targetNodeId;
	
	public GraphOLEdge() {
		
	}
	
	public GraphOLEdge(List<GraphOLEdgePoint> pointList, String type, String id, String srcId, String trgId) {
		this.pointList = pointList;
		this.type = type;
		this.id = id;
		this.sourceNodeId = srcId;
		this.targetNodeId = trgId;
	}
	
	@Override
	public List<GraphOLEdgePoint> getPoints() {
		return this.pointList;
	}

	@Override
	public String getEdgeType() {
		return this.type;
	}

	@Override
	public String getEdgeId() {
		return this.id;
	}

	@Override
	public String getSourceNodeId() {
		return this.sourceNodeId;
	}

	@Override
	public String getTargetNodeId() {
		return this.targetNodeId;
	}

	@Override
	public void setPoints(List<GraphOLEdgePoint> points) {
		this.pointList = points;
	}

	@Override
	public void setEdgeType(String type) {
		this.type = type;
	}

	@Override
	public void setEdgeId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if(this.getClass() != o.getClass()) {
			return false;
		}
		GraphOLEdge edge = (GraphOLEdge)o;
		if(this.id != edge.id || this.sourceNodeId != edge.sourceNodeId || this.targetNodeId != edge.targetNodeId) {
			return false;
		}
		
		return true;
	}

	@Override
	public void setSourceNodeId(String sourceId) {
		this.sourceNodeId = sourceId;
	}

	@Override
	public void setTargetNodeId(String targetId) {
		this.targetNodeId = targetId;
	}
	
	@Override
	public String toString() {
		String result = String.format("Edge[id='%s', type='%s', srcID='%s', tgtID='%s']", this.id, this.type, this.sourceNodeId, this.targetNodeId);
		return result;
	}
}
