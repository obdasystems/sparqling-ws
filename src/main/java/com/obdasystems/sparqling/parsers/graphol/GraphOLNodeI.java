package com.obdasystems.sparqling.parsers.graphol;

public interface GraphOLNodeI {
	
	public void setNodeLabel(GraphOLNodeLabel label);
	public void setNodeGeometry(GraphOLNodeGeometry geometry);
	public void setNodeType(String type);
	public void setNodeColor(String color);
	public void setNodeId(String id);
	
	public GraphOLNodeLabel getNodeLabel();
	public GraphOLNodeGeometry getNodeGeometry();
	public String getNodeType();
	public String getNodeColor();
	public String getNodeId();

}
