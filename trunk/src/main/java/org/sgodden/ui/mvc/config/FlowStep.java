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
	
	private String name;
    private String description;
	
	public FlowStep(){}

    /**
     * Returns the step description.
     * @return the step description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the step description.
     * @param stepDescription the step description.
     */
    public void setDescription(String stepDescription) {
        this.description = stepDescription;
    }



	/**
	 * Returns the step name, which must
	 * be unique within the flow.
	 * @return the step name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the step name, which must be
	 * unique within the flow.
	 * @param stepName the step name.
	 */
	public void setName(String stepName) {
		this.name = stepName;
	}	

}
