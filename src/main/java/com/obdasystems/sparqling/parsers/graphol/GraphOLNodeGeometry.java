package com.obdasystems.sparqling.parsers.graphol;

public class GraphOLNodeGeometry {
	
	private int x;
	private int y;
	private int height;
	private int width;
	
	public GraphOLNodeGeometry(int x,int y, int height, int width) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
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
