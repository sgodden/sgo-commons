package org.sgodden.ui.mvc.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.sgodden.ui.mvc.Flow;
import org.sgodden.ui.mvc.FlowOutcome;
import org.sgodden.ui.mvc.NamedObjectResolver;

/**
 * Abstract base class for flow resolutions.
 * 
 * @author goddens
 *
 */
abstract class FlowOutcomeImpl
	implements FlowOutcome {
	
	/**
	 * The factory that created us.
	 */
	private final FlowImpl factory;
	private Flow nextFlowResolutionFactory;
	private FlowOutcome previousFlowResolution;
    private String flowStepName;
    private String flowStepDescription;

    FlowOutcomeImpl(
			FlowImpl factory, 
			Flow nextFlowResolutionFactory,
			FlowOutcome previousFlowResolution,
            String flowStepName,
            String flowStepDescription) {
		super();
		this.factory = factory;
		this.nextFlowResolutionFactory = nextFlowResolutionFactory;
		this.previousFlowResolution = previousFlowResolution;
        this.flowStepName = flowStepName;
        this.flowStepDescription = flowStepDescription;
    }

	public Flow getNextFlowResolutionFactory() {
		return nextFlowResolutionFactory;
	}

	public FlowOutcome getPreviousFlowResolution() {
		return previousFlowResolution;
	}

	public FlowOutcome getFlowOutcome(
			String controllerResolution) {
		return this.factory.getFlowOutcome(controllerResolution, this);
	}

    public String getFlowStepDescription() {
        return flowStepDescription;
    }

    public String getFlowStepName(){
        return flowStepName;
    }

    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("nextFlowResolutionFactory", nextFlowResolutionFactory)
			.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.ui.mvc.FlowResolution#getNamedObjectResolver()
	 */
	public NamedObjectResolver getNamedObjectResolver() {
		return factory;
	}
	
}