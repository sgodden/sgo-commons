package org.sgodden.ui.mvc.impl;

public class SubFlowDestination 
	implements Destination {
	
	String destinationFlowName;

	public SubFlowDestination(String destinationFlowName) {
		super();
		this.destinationFlowName = destinationFlowName;
	}

}
