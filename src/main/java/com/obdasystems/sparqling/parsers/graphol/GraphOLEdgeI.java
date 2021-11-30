package com.obdasystems.sparqling.parsers.graphol;

import java.util.List;

public interface GraphOLEdgeI {
	
	public List<GraphOLEdgePoint> getPoints();
	public String getEdgeType();
	public String getEdgeId();
	public String getSourceNodeId();
	public String getTargetNodeId();
	
	public void setPoints(List<GraphOLEdgePoint> points);
	public void setEdgeType(String type);
	public void setEdgeId(String id);
	public void setSourceNodeId(String sourceId);
	public void setTargetNodeId(String targetId);
}
