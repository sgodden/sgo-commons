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
import java.util.HashSet;
import java.util.Set;

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
	private Context context;
	
	private Set < Integer > initialisedViews = new HashSet < Integer >();
    private TitledContainer titledContainer;

	/**
	 * Creates a new front controller to manage the specified container
	 * according to the specified flow.
	 * @param container the container which will display the views from the flow. 
	 * @param flow the flow.
	 */
	public FrontController(Container container, Flow flow) {
		this.container = container;

        if (container instanceof TitledContainer) {
            this.titledContainer = (TitledContainer) container;
        }
		
		context = new Context();
		context.setControllerResolutionHandler(this);
		context.setFlow(flow);
		
		processFlowResolution(
				flow.getFlowOutcome(null, null), 
				null);
	}
	
	private void processFlowResolution(FlowOutcome flowOutcome, String resolutionName) {

        context.setFlow(flowOutcome.getFlow());
	    
		previousFlowOutcome = flowOutcome;

        log.debug("Processing flow resolution: " + flowOutcome);

        // FIXME - allow a controller to be the first step in a flow
        if (resolutionName == null && !(flowOutcome instanceof ViewFlowOutcome)) {
            throw new IllegalArgumentException("Only a view can be invoked when the resolution name is null");
        }

        if (flowOutcome instanceof ControllerFlowOutcome) {
        	
        	context.setAvailableResolutions(null);

            Object controller = ((ControllerFlowOutcome) flowOutcome).getController();
            
            if (controller instanceof ContextAware) {
                ((ContextAware)controller).setContext(context);
            }
            
            String methodName = ((ControllerFlowOutcome) flowOutcome).getMethodName();
            if (methodName == null) {
            	methodName = resolutionName.toLowerCase();
            }

            /*
             * Controller methods must have the same name as resolution name (lower-cased), and no arguments.
             * They must also return a string.
             * TODO - allow controllers to specify which methods handle which resolutions by annotations
             */
            Method m;
            try {
                m = controller.getClass().getMethod(methodName, (Class[])null);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Unable to find method: resolution name '" + resolutionName + "'; controller class " + controller.getClass(), e);
            }

            if (!(m.getReturnType().equals(String.class))) {
                throw new IllegalStateException("Method " + m + " must return type java.lang.String");
            }

            String newResolutionName = null;

            try {
                newResolutionName = (String) m.invoke(controller, (Object[])null);
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
    	
    	context.setAvailableResolutions(vfr.getAvailableResolutions());
    	
        View view = vfr.getView();
        
        if (view instanceof DialogView) {
        	container.displayModalDialog(view);
        }
        else {
        	container.display(view);
        }

        if (titledContainer != null) {
            titledContainer.setTitle(vfr.getFlowStepDescription());
        }
        
        if (!(initialisedViews.contains(view.hashCode()))) {
            view.initialise(context);
            initialisedViews.add(view.hashCode());
        }
        
        view.activate();
		
	}

	public void handleResolution(String resolutionName) {
		FlowOutcome flowResolution = previousFlowOutcome.getFlowOutcome(resolutionName);
		if (flowResolution != null) {
			processFlowResolution(flowResolution, resolutionName);
		}
	}

    /**
     * Sets the container which will show the title of the currently
     * executing view step.
     * @param titledContainer the container which will show the title.
     */
    public void setTitledContainer(TitledContainer titledContainer) {
        this.titledContainer = titledContainer;
        if (previousViewFlowOutcome != null) {
            titledContainer.setTitle(
                    previousViewFlowOutcome.getFlowStepDescription());
        }
    }

}