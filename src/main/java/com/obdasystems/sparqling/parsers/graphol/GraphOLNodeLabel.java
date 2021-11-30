package com.obdasystems.sparqling.parsers.graphol;

public class GraphOLNodeLabel {
	private int x;
	private int y;
	private int height;
	private int width;
	private String label;
	
	public GraphOLNodeLabel(String label, int x,int y, int height, int width) {
		this.label = label;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
	}
	
	public String getLabel() {
		return label;
	}
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
