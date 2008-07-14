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
	private String methodName;

    ControllerFlowOutcomeImpl(
			FlowImpl factory,
			Object controller,
			String methodName,
			Flow nextFlowResolutionFactory,
			String flowStepName,
            String flowStepDescription,
			FlowOutcome previousFlowResolution) {
		super(
                factory,
                nextFlowResolutionFactory,
                previousFlowResolution,
                flowStepName,
                flowStepDescription);
		this.controller = controller;
		this.methodName = methodName;
	}

	public Object getController() {
		return controller;
	}
	
	public String getMethodName() {
		return methodName;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("controller", controller)
			.toString();
	}

}