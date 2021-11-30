package com.obdasystems.sparqling.parsers.graphol.node.impl;

import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeGeometry;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeI;
import com.obdasystems.sparqling.parsers.graphol.GraphOLNodeLabel;

public class GraphOLNode implements GraphOLNodeI {
	
	protected GraphOLNodeLabel nodeLabel;
	protected GraphOLNodeGeometry nodeGeometry;
	protected String type;
	protected String color;
	protected String id;
	
	public GraphOLNode() {
		
	}
	
	public GraphOLNode(GraphOLNodeLabel nodeLabel, GraphOLNodeGeometry nodeGeometry, String type, String color, String id) {
		this.nodeLabel = nodeLabel;
		this.nodeGeometry = nodeGeometry;
		this.type = type;
		this.color = color;
		this.id = id;
	}
	
	@Override
	public GraphOLNodeLabel getNodeLabel() {
		return this.nodeLabel;
	}

	@Override
	public GraphOLNodeGeometry getNodeGeometry() {
		return this.nodeGeometry;
	}

	@Override
	public String getNodeType() {
		return this.type;
	}

	@Override
	public String getNodeColor() {
		return this.color;
	}

	@Override
	public String getNodeId() {
		return this.id;
	}

	@Override
	public void setNodeLabel(GraphOLNodeLabel label) {
		this.nodeLabel = label;
	}

	@Override
	public void setNodeGeometry(GraphOLNodeGeometry geometry) {
		this.nodeGeometry = geometry;
	}

	@Override
	public void setNodeType(String type) {
		this.type = type;
	}

	@Override
	public void setNodeColor(String color) {
		this.color = color;
	}

	@Override
	public void setNodeId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if(this.getClass() != o.getClass()) {
			return false;
		}
		GraphOLNode node = (GraphOLNode)o;
		
		if(this.id != node.id) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String result;
		if(nodeLabel!=null) {
			result = String.format("Node[id='%s', type='%s', label='%s']", this.id, this.type, this.nodeLabel.getLabel());
		}
		else {
			result = String.format("Node[id='%s', type='%s', label='']", this.id, this.type);
		}
		
		return result;
	}

}
