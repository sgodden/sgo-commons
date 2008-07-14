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
import org.sgodden.ui.mvc.impl.FlowImpl.ViewFlowStep;

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
	private ViewFlowStep viewFlowStep;

    ViewFlowOutcomeImpl(
			FlowImpl factory,
			View view,
			Flow nextFlowResolutionFactory,
			FlowOutcome previousFlowResolution,
            ViewFlowStep viewFlowStep) {
		super(
                factory,
                nextFlowResolutionFactory,
                previousFlowResolution,
                viewFlowStep.getFlowStepName(),
                viewFlowStep.getFlowStepDescription());
		this.view = view;
		this.viewFlowStep = viewFlowStep;
	}

	public View getView() {
		return view;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
            .append("view", view)
			.toString();
	}

	public Set<String> getAvailableResolutions() {
		return viewFlowStep.getAvailableResolutions();
	}

}