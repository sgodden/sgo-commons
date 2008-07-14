package org.sgodden.ui.mvc.config;

import java.io.Serializable;

/**
 * Defines a controller step within a particular flow.
 * <p/>
 * Since one controller can execute processing for more
 * than one step in the flow, the name of the object to execute
 * the step can be specified separately.  If it is not specified,
 * then it is assumed to be the same as the step name.
 * <p/>
 * For example, say that we have an object named 'orderController'
 * within our flow.  We decide that we want to separate out the
 * validation and save steps of the flow, in order for us to more
 * easily tailor that process for different implementations of our
 * software.
 * <p/>
 * So the 'save' resolution from the edit panel would go to a 'validate'
 * step performed by 'orderController'.  'success' from 'validate'
 * would go to a 'save' step, also performed by 'orderController'.
 * 
 * @author sgodden
 *
 */
public class ControllerStep 
		extends FlowStep
		implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String objectName;
    private String methodName;
	
	public ControllerStep(){}

	/**
	 * Returns the name of the managed object
	 * which will execute this controller step.
	 * @return the name of the managed object.
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * (Optional) - sets the name of the object
	 * which will execute this controller step.
	 * <p/>
	 * If not specified, then the object name is the same
	 * as the step name.
	 * @param objectName the name of the object.
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

    /**
     * Returns the name of the method which should be invoked
     * on the controller.
     * @return the name of the controller method to invoke.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the name of the method which should be invoked
     * on the controller.
     * @param methodName the name of the controller method to invoke.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }



}
