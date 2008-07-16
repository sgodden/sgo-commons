/**
 * 
 */
package org.sgodden.ui.mvc.impl;

import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.sgodden.ui.mvc.Flow;
import org.sgodden.ui.mvc.FlowOutcome;
import org.sgodden.ui.mvc.View;
import org.sgodden.ui.mvc.ViewFlowOutcome;
import org.sgodden.ui.mvc.config.FlowStep;

/**
 * A flow outcome indicating that a certain view
 * should be displayed.
 *
 * @author goddens
 *
 */
class ViewFlowOutcomeImpl
	    extends FlowOutcomeImpl
        implements ViewFlowOutcome {

	private View view;
	private FlowStep viewFlowStep;

    ViewFlowOutcomeImpl(
			FlowImpl factory,
			View view,
			Flow nextFlowResolutionFactory,
			FlowOutcome previousFlowResolution,
            FlowStep viewFlowStep) {
		super(
                factory,
                nextFlowResolutionFactory,
                previousFlowResolution,
                viewFlowStep.getName(),
                viewFlowStep.getDescription());
		this.view = view;
		this.viewFlowStep = viewFlowStep;
	}

	public View getView() {
		return view;
	}

	public Set<String> getAvailableResolutions() {
		return viewFlowStep.getAvailableResolutions();
	}

}