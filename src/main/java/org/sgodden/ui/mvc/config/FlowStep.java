package org.sgodden.ui.mvc.config;

import java.io.Serializable;

/**
 * Defines a step within a particular flow.  Each step within the flow
 * must have a unique name.
 * 
 * @author sgodden
 *
 */
public abstract class FlowStep 
		implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String stepName;
	
	public FlowStep(){}

	/**
	 * Returns the step name, which must
	 * be unique within the flow.
	 * @return the step name.
	 */
	public String getStepName() {
		return stepName;
	}

	/**
	 * Sets the step name, which must be
	 * unique within the flow.
	 * @param stepName the step name.
	 */
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}	

}
