package org.sgodden.ui.mvc.impl;

import org.sgodden.ui.mvc.FlowOutcome;
import org.sgodden.ui.mvc.NamedObjectResolver;

/**
 * A dummy implementation of FlowResolution for use
 * when calling sub-flows.
 * <p/>
 * This wraps the previous flow resolution for this factory, so
 * that when the sub-flow completes, control will be returned 
 * to the last controller that was active for this flow.
 * 
 * @author goddens
 *
 */
class SubFlowFlowOutcomeImpl 
		implements FlowOutcome {

	private FlowOutcome previousFlowResolution; 
	
	SubFlowFlowOutcomeImpl(FlowOutcome previousFlowResolution) {
		this.previousFlowResolution = previousFlowResolution;
	}
	
	public Object getController() {
		throw new IllegalStateException("This method should not be called"); // FIXME - indicates broken API
	}

	public FlowOutcome getFlowOutcome(
			String controllerResolution) {
		return previousFlowResolution;
	}

	public NamedObjectResolver getNamedObjectResolver() {
		return null; // FIXME - this is clearly crap
	}

    public String getFlowStepDescription() {
        return null; // FIXME - this is clearly crap
    }
	
}