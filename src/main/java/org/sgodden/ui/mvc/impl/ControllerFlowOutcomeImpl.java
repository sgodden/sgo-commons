/**
 * 
 */
package org.sgodden.ui.mvc.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.sgodden.ui.mvc.*;

/**
 * An outcome indicating that a certain controller
 * should be invoked.
 * 
 * @author goddens
 *
 */
class ControllerFlowOutcomeImpl
	    extends FlowOutcomeImpl
        implements ControllerFlowOutcome {

	private Object controller;

    ControllerFlowOutcomeImpl(
			FlowImpl factory,
			Object controller,
			Flow nextFlowResolutionFactory,
			String flowStepName,
			FlowOutcome previousFlowResolution) {
		super(
                factory,
                nextFlowResolutionFactory,
                previousFlowResolution,
                flowStepName);
		this.controller = controller;
	}

	public Object getController() {
		return controller;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("controller", controller)
			.toString();
	}

}