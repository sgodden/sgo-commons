/* =================================================================
 # This library is free software; you can redistribute it and/or
 # modify it under the terms of the GNU Lesser General Public
 # License as published by the Free Software Foundation; either
 # version 2.1 of the License, or (at your option) any later version.
 #
 # This library is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 # Lesser General Public License for more details.
 #
 # You should have received a copy of the GNU Lesser General Public
 # License along with this library; if not, write to the Free Software
 # Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 #
 # ================================================================= */
package org.sgodden.ui.mvc;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages the flow within a particular visual container.
 * 
 * @author sgodden
 *
 */
public class FrontController 
		implements ResolutionHandler {

    private static final transient Log log = LogFactory.getLog(FrontController.class);

    private Container container;
	private FlowOutcome previousFlowOutcome;
	private ViewFlowOutcome previousViewFlowOutcome;

	/**
	 * Creates a new front controller to manage the specified container
	 * according to the specified flow.
	 * @param container the container which will display the views from the flow. 
	 * @param flow the flow.
	 */
	public FrontController(Container container, Flow flow) {
		this.container = container;
		
		processFlowResolution(
				flow.getFlowOutcome(null, null), 
				null);
	}
	
	private void processFlowResolution(FlowOutcome flowOutcome, String resolutionName) {
		previousFlowOutcome = flowOutcome;

        log.debug("Processing flow resolution: " + flowOutcome);

        // if the resolution name is null (i.e. it's the first flow step), then we can only invoke a view - FIXME - why so?
        if (resolutionName == null && !(flowOutcome instanceof ViewFlowOutcome)) {
            throw new IllegalArgumentException("Only a view can be invoked when the resolution name is null");
        }

        Context.getCurrentContext().setConversationNamedObjectResolver(flowOutcome.getNamedObjectResolver());
        Context.getCurrentContext().setControllerResolutionHandler(this);

        if (flowOutcome instanceof ControllerFlowOutcome) {
        	
        	Context.getCurrentContext().setAvailableResolutions(null);

            Object controller = ((ControllerFlowOutcome) flowOutcome).getController();

            /*
             * Controller methods must have the same name as resolution name (lower-cased), and no arguments.
             * They must also return a string.
             * TODO - allow controllers to specify which methods handle which resolutions by annotations
             */
            Method m;
            try {
                m = controller.getClass().getMethod(resolutionName.toLowerCase(), (Class[])null);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Unable to find method: resolution name '" + resolutionName + "'; controller class " + controller.getClass(), e);
            }

            if (!(m.getReturnType().equals(String.class))) {
                throw new IllegalStateException("Method " + m + " must return type java.lang.String");
            }

            String newResolutionName = null;

            try {
                newResolutionName = (String) m.invoke(controller, null);
            } catch (Exception e) {
                throw new Error("Error invoking controller", e);
            }

            if (newResolutionName != null) {
                handleResolution(newResolutionName);
            } else {
            	// null from the controller means redisplay the last view
            	previousFlowOutcome = previousViewFlowOutcome;
            	configureFromViewFlowOutcome(previousViewFlowOutcome);
            }

        }
        else if (flowOutcome instanceof ViewFlowOutcome) {
        	previousViewFlowOutcome = (ViewFlowOutcome) flowOutcome;
        	configureFromViewFlowOutcome(previousViewFlowOutcome);
        }
    }
	
	private void configureFromViewFlowOutcome(ViewFlowOutcome vfr) {
    	
    	Context.getCurrentContext().setAvailableResolutions(vfr.getAvailableResolutions());
    	
        View view = vfr.getView();
        
        // FIXME - the container can tell whether it is a dialog, no need for a separate method in the interface
        if (view instanceof DialogView) {
        	container.displayModalDialog(view);
        }
        else {
        	container.display(view);
        }
        
        view.activate();
		
	}

	public void handleResolution(String resolutionName) {
		FlowOutcome flowResolution = previousFlowOutcome.getFlowOutcome(resolutionName);
		if (flowResolution != null) {
			processFlowResolution(flowResolution, resolutionName);
		}
	}
}