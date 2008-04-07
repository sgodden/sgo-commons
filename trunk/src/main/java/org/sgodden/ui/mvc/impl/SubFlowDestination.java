package org.sgodden.ui.mvc.impl;

import org.sgodden.ui.mvc.Flow;

public class SubFlowDestination 
	implements Destination {
	
	Class<? extends Flow> destinationFlowFactoryClass;

	public SubFlowDestination(Class<? extends Flow> destinationFlowFactoryClass) {
		super();
		this.destinationFlowFactoryClass = destinationFlowFactoryClass;
	}

}
