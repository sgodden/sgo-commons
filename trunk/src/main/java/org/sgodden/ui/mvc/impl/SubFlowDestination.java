package org.sgodden.ui.mvc.impl;

import java.util.Map;

public class SubFlowDestination 
	implements Destination {
	
	String destinationFlowName;
    Map<String, String> parameterMappings;

	public SubFlowDestination(
            String destinationFlowName,
            Map<String, String> parameterMappings) {
		super();
		this.destinationFlowName = destinationFlowName;
        this.parameterMappings = parameterMappings;
	}

}
