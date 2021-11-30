package com.obdasystems.sparqling.parsers.graphol.impl.v2;

public class GraphOLDescription {
	
	private String status;
	private String descriptionFormattedText;
	
	public GraphOLDescription() {
		status = "";
		descriptionFormattedText = "";
	}
	
	public GraphOLDescription(String status, String descr) {
		this.status = status;
		this.descriptionFormattedText = descr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescriptionFormattedText() {
		return descriptionFormattedText;
	}

	public void setDescriptionFormattedText(String descriptionFormattedText) {
		this.descriptionFormattedText = descriptionFormattedText;
	}
	
	
}
