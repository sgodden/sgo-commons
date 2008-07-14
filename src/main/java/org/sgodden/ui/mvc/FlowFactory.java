package org.sgodden.ui.mvc;

/**
 * An factory which creates flows.
 * @author sgodden
 */
public interface FlowFactory {
    
    /**
     * Returns a new flow of the given name.
     * @param flowName the name of the flow.
     * @return the new flow.
     */
    public Flow makeFlow(String flowName);

}
