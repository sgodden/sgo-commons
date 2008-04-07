package org.sgodden.ui.mvc.impl;

/**
 * A destination which indicates that the current flow should terminate,
 * returning to any previous flow which might have triggered it.
 * 
 * @author sgodden
 *
 */
public class FlowTerminationDestinationImpl
		implements Destination {
	
	String resolutionName;

	public FlowTerminationDestinationImpl() {
		super();
	}
	
	public FlowTerminationDestinationImpl(String resolutionName) {
		this();
		this.resolutionName = resolutionName;
	}
	
}