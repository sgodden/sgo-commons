package org.sgodden.ui.mvc.config;

import java.io.Serializable;

import org.sgodden.ui.mvc.impl.Guard;

/**
 * Maps a resolution (outcome) in one step, to
 * a particular destination step.
 * <p/>
 * Note that it is possible to define more than one
 * mapping from the same source to the same destination,
 * qualified by a {@link org.sgodden.ui.mvc.impl.Guard} condition
 * which will selectively determine whether that mapping
 * will apply at runtime.
 * <p/>
 * This facilitates more complex situations where, for instance,
 * permission has to be sought to enter an order for a client with a poor
 * credit record.
 * <p/>
 * It is up to the flow designer to ensure that only one qualified
 * resolution mapping can succeed in such a scenario.  In practise,
 * the first mapping found which fulfills the transition will be used.
 * @author sgodden
 *
 */
public class ResolutionMapping 
		implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sourceStepName;
	private String resolutionName;
	private Guard guard;
	private String destinationStepName;
	private String controllerMethodName;
    private String terminationResolutionName;
	private String subFlowName;
	
	/**
	 * Returns the name of the source step which triggers
	 * the resolution.
	 * @return the name of the source step.
	 */
	public String getSourceStepName() {
		return sourceStepName;
	}
	
	/**
	 * (Mandatory) - sets the name of the source step which triggers
	 * the resolution.
	 * @param sourceStepName the name of the source step.
	 */
	public void setSourceStepName(String sourceStepName) {
		this.sourceStepName = sourceStepName;
	}
	
	/**
	 * Returns the name of the resolution fired by the source
	 * step.
	 * @return the name of the resolution.
	 */
	public String getResolutionName() {
		return resolutionName;
	}
	
	/**
	 * (Mandatory) - sets the name of the resolution fired by the source
	 * step.
	 * @param resolutionName the name of the resolution.
	 */
	public void setResolutionName(String resolutionName) {
		this.resolutionName = resolutionName;
	}
	
	/**
	 * Returns the guard condition which makes this mapping conditional.
	 * @return the guard condition.
	 */
	public Guard getGuard() {
		return guard;
	}
	
	/**
	 * (Optional) - sets the guard condition which makes this mapping
	 * conditional.
	 * @param guard the guard condition.
	 */
	public void setGuard(Guard guard) {
		this.guard = guard;
	}
	
	/**
	 * Returns the name of the destination step to which the flow should go.
	 * @return the name of the destination step.
	 */
	public String getDestinationStepName() {
		return destinationStepName;
	}
	
	/**
	 * Sets the name of the destination step to which the
	 * flow should go.  Only one of destination step name and
     * termination resolution name may be specified.
	 * @param destinationStepName the name of the destination step.
	 */
	public void setDestinationStepName(String destinationStepName) {
        checkState();
		this.destinationStepName = destinationStepName;
	}

	/**
	 * Returns the name of the method to invoke in the controller, in the case
	 * that the destination points to a controller.
	 * @return the name of the method to invoke in the controller.
	 */
	public String getControllerMethodName() {
		return controllerMethodName;
	}

	/**
	 * Returns the name of the method to invoke in the controller, in the case
	 * that the destination points to a controller.
	 * @param controllerMethodName the name of the method to invoke in the controller.
	 */
	public void setControllerMethodName(String controllerMethodName) {
		this.controllerMethodName = controllerMethodName;
	}

    /**
     * A non-null value indicates that the flow should be terminated with the
     * returned resolution name.
     * @return the resolution name with which to terminate the flow.
     */
    public String getTerminationResolutionName() {
        return terminationResolutionName;
    }

    /**
     * Sets the resolution name with which to terminate the flow.
     * Only one of termination resolution name and destination step name
     * may be specified.
     * @param terminationResolutionName the resolution name with which to
     * terminate the flow.
     */
    public void setTerminationResolutionName(String terminationResolutionName) {
        checkState();
        this.terminationResolutionName = terminationResolutionName;
    }

    /**
     * Returns the name of the sub-flow to invoke.
     * @return the name of the sub-flow to invoke.
     */
    public String getSubFlowName() {
        return subFlowName;
    }

    /**
     * Sets the name of the sub-flow to invoke.
     * @param subFlowName the name of the sub-flow to invoke.
     */
    public void setSubFlowName(String subFlowName) {
        checkState();
        this.subFlowName = subFlowName;
    }

    private void checkState() {
        if (
                (destinationStepName != null
                && (terminationResolutionName != null || subFlowName != null))
                || (terminationResolutionName != null
                && (destinationStepName != null || subFlowName != null))
                || (subFlowName != null
                && (terminationResolutionName != null || subFlowName != null))
                ) {
            throw new IllegalStateException("Only one of destinationStepName," +
                    " terminationResolutionName and subFlowName may be " +
                    "specified");
        }
    }

}