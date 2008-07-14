/**
 * 
 */
package org.sgodden.ui.mvc.impl;

import org.sgodden.ui.mvc.impl.FlowImpl.FlowStep;

/**
 * A destination pointing to another step within the same flow.
 * @author sgodden
 *
 */
public class FlowStepDestinationImpl
		implements Destination {
	
	FlowStep destination;

	protected FlowStepDestinationImpl(
			FlowStep destination) {
		super();
		this.destination = destination;
	}
	
}