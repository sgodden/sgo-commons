package org.sgodden.entities.metadata;

public class AggregationMetadata {
	private String className;
	private int displaySequence; // FIXME - this is confusion of concerns
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getDisplaySequence() {
		return displaySequence;
	}
	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
	}
}