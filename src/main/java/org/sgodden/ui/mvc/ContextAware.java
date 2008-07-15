package org.sgodden.ui.mvc;

/**
 * Interface for objects (usually controllers) that wish to
 * have access to the current flow context.
 * @author sgodden
 *
 */
public interface ContextAware {
    
    /**
     * Sets the context of the current flow.
     * @param context the context of the current flow.
     */
    public void setContext(Context context);

}