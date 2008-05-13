package org.sgodden.ui.mvc.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.ui.mvc.ControllerFlowOutcome;
import org.sgodden.ui.mvc.Flow;
import org.sgodden.ui.mvc.FlowOutcome;
import org.sgodden.ui.mvc.View;
import org.sgodden.ui.mvc.ViewFlowOutcome;
import org.sgodden.ui.mvc.config.ControllerStep;
import org.sgodden.ui.mvc.config.ResolutionMapping;
import org.sgodden.ui.mvc.config.ViewStep;

/**
 * Default implementation of the {@link Flow} interface. 
 * 
 * @author goddens
 */
public class FlowImpl
        implements Flow {

    private static final transient Log log = LogFactory.getLog(FlowImpl.class);
    /**
     * A simple internal variable to tell us whether we are being invoked
     * for the first time.
     */
    private boolean firstInvocation = true;
    private Map<String, Object> namedObjects;
    private Map<String, FlowStep> flowStepConfigurations = new HashMap<String, FlowStep>();
    private Map<String, Set<QualifiedResolutionMapping>> globalResolutionMappings = new HashMap<String, Set<QualifiedResolutionMapping>>();
    
    /**
     * If we have been daisy-chained from a previous flow resolution factory, then this is the outcome in the
     * previous factory that caused us to be invoked.  We will reinvoke this resolution once this flow terminates.
     * In this respect, flow resolution factories operate as a simple linked list. 
     */
    private FlowOutcome precedingFactoryFlowResolution;
    
    /**
     * The name of the initial view to be displayed.
     */
    private String initialViewName;
    
    /**
     * The controller steps of the flow.
     */
    private ControllerStep[] controllerSteps;
    
    /**
     * The view steps of the flow.
     */
    private ViewStep[] viewSteps;
    
    /**
     * The resolution mappings of the flow.
     */
    private ResolutionMapping[] resolutionMappings;

    /**
     * Sets the map of named objects that will be used to provide view
     * and controller implementations.
     * 
     * @param namedObjects the set of managed objects.
     */
    public void setNamedObjects(Map<String, Object> namedObjects) {
        this.namedObjects = namedObjects;
    }
    
    /**
     * Sets the controller steps of the flow.
     * @param controllerSteps the controller steps.
     */
    public void setControllerSteps(ControllerStep[] controllerSteps) {
    	this.controllerSteps = controllerSteps;
    }
    
    /**
     * Sets the view steps of the flow.
     * @param viewSteps the view steps.
     */
    public void setViewSteps(ViewStep[] viewSteps) {
    	this.viewSteps = viewSteps;
    }
    
    /**
     * Sets the resolution mappings of the flow.
     * @param resolutionMappings the resolution mappings.
     */
    public void setResolutionMappings(ResolutionMapping[] resolutionMappings) {
    	this.resolutionMappings = resolutionMappings;
    }
    
    /**
     * Sets the name of the initial view to be displayed.
     * @param viewName the initial view to be displayed.
     */
    public void setInitialViewName(String viewName) {
    	this.initialViewName = viewName;
    }

    /**
     * Adds a view step, where the name of the view matches
     * a key in the named objects provided in <code>setNamedObjects(Map)</code>.
     * <p/>
     * Since views are stateful, a single managed object cannot be used to
     * provide more than one view.
     * 
     * @param viewName the name of the view, which must be unique.
     */
    protected final void addViewStep(
            String viewName) {

        if (flowStepConfigurations.containsKey(viewName)) {
            throw new IllegalArgumentException("A view configuration already exists with name " + viewName);
        }

        flowStepConfigurations.put(
                viewName,
                new ViewFlowStep(viewName));

    }

    private void checkViewConfigurationExists(String viewName) {
        if (!flowStepConfigurations.containsKey(viewName)) {
            throw new IllegalArgumentException("No view configuration exists with name " + viewName);
        }
    }

    private void checkControllerConfigurationExists(String controllerName) {
        if (!flowStepConfigurations.containsKey(controllerName)) {
            throw new IllegalArgumentException("No controller configuration exists with name " + controllerName);
        }
    }

    /**
     * Adds the basic details of a controller step. Addition of resolution mappings
     * is done as a secondary step, to cope with circular dependencies.
     * 
     * @param controllerName the name of the controller, which must be unique.
     * @param managedObjectName the name of the managed object which provides the controller function.
     * @param configurationProperties a set of properties to configure on controllers created from this configuration.
     */
    protected final void addControllerStep(
            String controllerName,
            String managedObjectName) {

        if (flowStepConfigurations.containsKey(controllerName)) {
            throw new IllegalArgumentException("A flow step already exists with name '" + controllerName);
        }

        flowStepConfigurations.put(
                controllerName,
                new ControllerFlowStep(
                controllerName,
                managedObjectName
                ));
    }
    
    /**
     * A convenience version of {@link #addResolutionMapping(String, String, Guard, Destination)}
     * where the destination is the named step.
     * 
     * @param sourceStepName the name of the source flow step.
     * @param resolutionName the name of the resolution event.
     * @param destinationStepName the name of the destination flow step to which control should be transferred, or 
     * <code>null</code> in order to cause termination of this flow and transfer of control to the previous flow.
     */
    protected final void addResolutionMapping(
            String sourceStepName,
            String resolutionName,
            String destinationStepName) {
        addResolutionMapping(sourceStepName, resolutionName, null, destinationStepName, null);
    }

    /**
     * A convenience version of {@link #addResolutionMapping(String, String, Guard, Destination)}
     * where the destination is the named step.
     * 
     * @param sourceStepName the name of the source flow step.
     * @param resolutionName the name of the resolution event.
     * @param guard a guard condition which must evaluate to true.
     * @param destinationStepName the name of the destination flow step to which control should be transferred, or 
     * <code>null</code> in order to cause termination of this flow and transfer of control to the previous flow.
     */
    protected final void addResolutionMapping(
            String sourceStepName,
            String resolutionName,
            Guard guard,
            String destinationStepName,
            String controllerMethodName) {

        FlowStep source = null;
        FlowStep destination = null;

        if (!(flowStepConfigurations.containsKey(sourceStepName))) {
            throw new IllegalArgumentException("No flow step exists with name '" + sourceStepName);
        } else {
            source = flowStepConfigurations.get(sourceStepName);
        }

        if (destinationStepName == null) {
            throw new IllegalArgumentException("the destination flow step name may not be null");
        }

        if (destinationStepName != null && !(flowStepConfigurations.containsKey(destinationStepName))) {
            throw new IllegalArgumentException("No flow step exists with name '" + destinationStepName);
        } else if (destinationStepName != null) {
            destination = flowStepConfigurations.get(destinationStepName);
        }

        source.addResolutionMapping(resolutionName, guard, new FlowStepDestinationImpl(destination, controllerMethodName));
    }

    /**
     * Configures the destination routing for a particular source step and resolution name.
     * 
     * @param sourceStepName the name of the source flow step.
     * @param resolutionName the name of the resolution event.
     * @param guard A guard condition which can perform arbitrary tests to see if this destination is valid, for instance based
     * on model values.  This allows multiple destinations to be set up for the same resolution name dependent on arbitrary conditions.
     * For instance, if the order value was above the permitted maximum for the customer, a different path could be taken.
     * @param destination The destination to transfer control to, or  
     * <code>null</code> in order to cause termination of this flow and transfer of control to the previous flow.
     */
    protected final void addResolutionMapping(
            String sourceStepName,
            String resolutionName,
            Guard guard,
            Destination destination,
            String controllerMethodName) {

        FlowStep source = flowStepConfigurations.get(sourceStepName);
        if (source == null) {
            throw new IllegalArgumentException("No flow step exists with name '" + sourceStepName);
        }
        source.addResolutionMapping(resolutionName, guard, destination);

    }

    /**
     * A convenience version of {@link #addGlobalResolutionMapping(String, Guard, Destination)}
     * where the destination represents a particular controller within this flow.
     * 
     * @param resolutionName the name of the resolution event.
     * @param guard A guard condition which can perform arbitrary tests to see if this destination is valid, for instance based
     * on model values.  This allows multiple destinations to be set up for the same resolution name dependent on arbitrary conditions.
     * For instance, if the order value was above the permitted maximum for the customer, a different path could be taken.
     * @param destinationStepName The name of the destination step to transfer control to, or  
     * <code>null</code> in order to cause termination of this flow and transfer of control to the previous flow.
     */
    protected void addGlobalResolutionMapping(
            String resolutionName,
            Guard guard,
            String destinationStepName,
            String controllerMethodName) {

        FlowStep destination = flowStepConfigurations.get(destinationStepName);

        if (destination == null) {
            throw new IllegalArgumentException("Cannot find flow step with name: " + destinationStepName);
        }

        Set<QualifiedResolutionMapping> mappings = globalResolutionMappings.get(resolutionName);

        if (mappings == null) {
            mappings = new HashSet<QualifiedResolutionMapping>();
            globalResolutionMappings.put(resolutionName, mappings);
        }

        mappings.add(new QualifiedResolutionMapping(guard, new FlowStepDestinationImpl(destination, controllerMethodName)));
    }

    /**
     * Adds a global mapping of a particular named controller resolution, to a
     * destination.
     * 
     * @param resolutionName the name of the resolution event.
     * @param guard an optional guard which must evaluate to true for this flow resolution to be valid.
     * @param destination a destination, for instance a controller or a flow termination.
     */
    protected void addGlobalResolutionMapping(
            String resolutionName,
            Guard guard,
            Destination destination,
            String controllerMethodName) {

        if (resolutionName == null) {
            throw new IllegalArgumentException("resolution name may not be null");
        }

        if (destination == null) {
            throw new IllegalArgumentException("destination may not be null");
        }

        Set<QualifiedResolutionMapping> mappings = globalResolutionMappings.get(resolutionName);

        if (mappings == null) {
            mappings = new HashSet<QualifiedResolutionMapping>();
            globalResolutionMappings.put(resolutionName, mappings);
        }

        mappings.add(new QualifiedResolutionMapping(guard, destination));
    }

    /**
     * Returns the named view flow step.
     * @param viewName the name of the view, which must be registered.
     * @return the view flow step.
     */
    protected ViewFlowStep getViewConfiguration(String viewName) {
        checkViewConfigurationExists(viewName);
        return (ViewFlowStep) flowStepConfigurations.get(viewName);
    }

    /**
     * Returns the named controller flow step.
     * @param controllerName the name of the controller, which must have registered.
     * @return the controller flow step.
     */
    protected ControllerFlowStep getControllerConfiguration(String controllerName) {
        checkControllerConfigurationExists(controllerName);
        return (ControllerFlowStep) flowStepConfigurations.get(controllerName);
    }

    /*
     * (non-Javadoc)
     * @see org.sgodden.ui.mvc.Flow#getFlowOutcome(java.lang.String, org.sgodden.ui.mvc.FlowOutcome)
     */
    public FlowOutcome getFlowOutcome(
            String controllerResolution,
            FlowOutcome previousFlowResolution) {

        FlowOutcome ret = null;

        /*
         * If this is the first time we have been invoked, then we need to
         * store the previous flow resolution so that we can re-invoke it
         * once this flow terminates.
         */
        if (firstInvocation) {
            firstInvocation = false;
            precedingFactoryFlowResolution = previousFlowResolution;
            
            /*
             * If we have been configured using the config classes, initialise
             * from them now.
             */
            if (controllerSteps != null) {
            	for (ControllerStep step : controllerSteps) {
            		addControllerStep(step.getStepName(), step.getObjectName());
            	}
            }
            if (viewSteps != null) {
            	for (ViewStep step : viewSteps) {
            		addViewStep(step.getStepName());
            	}
            }
            if (resolutionMappings != null) {
            	for (ResolutionMapping mapping : resolutionMappings) {
            		addResolutionMapping(
            				mapping.getSourceStepName(), 
            				mapping.getResolutionName(), 
            				mapping.getGuard(), 
            				mapping.getDestinationStepName(),
            				mapping.getControllerMethodName());
            	}
            }
            
            ret = configureFlowResolutionFromViewFlowStep(getInitialViewFlowStep(), previousFlowResolution);
        } else {
            ret = handleResolution(controllerResolution, (FlowOutcomeImpl) previousFlowResolution);
        }

        log.debug("Returning flow resolution: " + ret);

        return ret;
    }

    private FlowOutcome handleResolution(
            String resolutionName,
            FlowOutcomeImpl previousFlowResolution) {

        FlowOutcome ret = null;

        log.debug("Processing controller resolution: flow step=" + previousFlowResolution.getFlowStepName() + ", resolutionName=" + resolutionName);

        // store the memento
        // mementos.put(sfr.getControllerName(), resolutionName.getMemento()); TODO - consider mementos

        FlowStep source;

        if (previousFlowResolution instanceof ViewFlowOutcome) {
            if (!flowStepConfigurations.containsKey(previousFlowResolution.getFlowStepName())) {
                throw new IllegalArgumentException("Unknown view name: " + previousFlowResolution.getFlowStepName());
            }
            source = flowStepConfigurations.get(previousFlowResolution.getFlowStepName());
        } else if (previousFlowResolution instanceof ControllerFlowOutcome) {
            if (!(flowStepConfigurations.containsKey(previousFlowResolution.getFlowStepName()))) {
                throw new IllegalArgumentException("Unknown controller name: " + previousFlowResolution.getFlowStepName());
            }
            source = flowStepConfigurations.get(previousFlowResolution.getFlowStepName());
        } else {
            throw new IllegalArgumentException("Unknown flow resolution type: " + previousFlowResolution.getClass());
        }

        Destination destination = getDestination(resolutionName, source);

        log.debug("Found destination: " + destination);

        /*
         * Destination is termination of this flow.
         */
        if (destination instanceof FlowTerminationDestinationImpl) {
            if (precedingFactoryFlowResolution != null) {
                ret = precedingFactoryFlowResolution.getFlowOutcome(resolutionName);
            }
        // if the previous resolution was null, nothing will happen (the action will just be consumed)
        } /*
         * Destination is a step from this flow. 
         */ else if (destination instanceof FlowStepDestinationImpl) {
            FlowStepDestinationImpl ccd = (FlowStepDestinationImpl) destination;
            ret = configureFlowOutcomeFromFlowStep(ccd, previousFlowResolution);
        } /*
         * Destination is a sub-flow.
         */ else if (destination instanceof SubFlowDestination) {
            SubFlowDestination subFlowDestination = (SubFlowDestination) destination;

            try {
                Flow nextFactory = subFlowDestination.destinationFlowFactoryClass.newInstance();
                ret = nextFactory.getFlowOutcome(
                        resolutionName,
                        new SubFlowFlowOutcomeImpl(previousFlowResolution));
            } catch (Exception e) {
                throw new Error("Error trying to instantiate the next flow resolution factory: " + subFlowDestination.destinationFlowFactoryClass.getName(), e);
            }
        } /*
         * Destination is an instruction to return to the previous step.
         */ else if (destination instanceof GoBackDestinationImpl) {
            log.debug("Previous flow resolution is: " + previousFlowResolution);
            ret = previousFlowResolution.getPreviousFlowResolution();
        } /*
         * WTF?!
         */ else {
            throw new IllegalArgumentException("Destination of type " + destination.getClass().getName() + " is not supported");
        }

        return ret;
    }

    /**
     * Determines the destination for a particular resolution
     * from a previous flow step.
     * 
     * @param resolutionName
     * @param source
     * @return
     */
    private Destination getDestination(
            String resolutionName,
            FlowStep source) {

        Destination destination = null;

        // find the first resolution which is either unqualified, or whose guard is satisfied by the model
        Set<QualifiedResolutionMapping> mappings = source.resolutionMappings.get(resolutionName);
        if (mappings != null) {
            for (QualifiedResolutionMapping mapping : mappings) {
                if (mapping.guard == null || mapping.guard.approve()) {
                    destination = mapping.destination;
                    break;
                }
            }
        }

        // if the destination is null, then go for the global mappings
        if (destination == null) {
            mappings = globalResolutionMappings.get(resolutionName);
            if (mappings != null) {
                for (QualifiedResolutionMapping mapping : mappings) {
                    if (mapping.guard == null || mapping.guard.approve()) {
                        destination = mapping.destination;
                        break;
                    }
                }
            }
        }

        // if we haven't found any mappings for this resolution name, then throw an error
        if (mappings == null) {
            throw new IllegalArgumentException("Neither a specific controller mapping nor a global mapping could be found for resolution name: " + resolutionName + "; Flow resolution factory class name: " + this.getClass().getName());
        }

        return destination;

    }

    /**
     * Returns the initial view flow step.
     * 
     * @return the initial view flow step.
     */
    private ViewFlowStep getInitialViewFlowStep() {
        ViewFlowStep ret = null;
        
        if (initialViewName == null) {
        	throw new IllegalStateException("The initial view name has not been set");
        }

        if (!(flowStepConfigurations.containsKey(initialViewName))) {
            throw new IllegalArgumentException("No view configuration exists for name: " + initialViewName);
        }

        ret = (ViewFlowStep) flowStepConfigurations.get(initialViewName);

        return ret;
    }

    /**
     * Configures a flow outcome from a destination flow step.
     * 
     * @param destination the destination flow step.
     * @param previousFlowOutcome the previous flow outcome.
     * @return the new flow outcome.
     */
    private FlowOutcome configureFlowOutcomeFromFlowStep(
            FlowStepDestinationImpl destination,
            FlowOutcome previousFlowOutcome) {

        FlowOutcome ret;
        
        FlowStep target = destination.destination;

        if (target instanceof ControllerFlowStep) {
            ret = configureFlowResolutionFromControllerFlowStep(
                    (ControllerFlowStep) target,
                    previousFlowOutcome,
                    destination.controllerMethodName);
        } else {
            ret = configureFlowResolutionFromViewFlowStep(
                    (ViewFlowStep) target,
                    previousFlowOutcome);
        }

        return ret;

    }

    /**
     * Configures a controller flow outcome from the destination
     * controller flow step.
     * @param destination the destination controller flow step.
     * @param previousFlowOutcome the previous flow outcome.
     * @return the new flow outcome.
     */
    private FlowOutcome configureFlowResolutionFromControllerFlowStep(
            ControllerFlowStep destination,
            FlowOutcome previousFlowOutcome,
            String controllerMethodName) {

        Object controller = destination.getController();

        // and set the memento that it may have stored earlier
        //Serializable memento = mementos.get(destination.controllerName); TODO - consider mementos
        //controller.setMemento(memento);


        return new ControllerFlowOutcomeImpl(
                this,
                controller,
                controllerMethodName,
                this,
                destination.getFlowStepName(),
                previousFlowOutcome);

    }

    /**
     * Configures a view flow outcome from the destination
     * view flow step.
     * @param destination the destination view flow step.
     * @param previousFlowOutcome the previous flow outcome.
     * @return
     */
    private FlowOutcome configureFlowResolutionFromViewFlowStep(
            ViewFlowStep destination,
            FlowOutcome previousFlowOutcome) {

        View controller = destination.getView();

        return new ViewFlowOutcomeImpl(
                this,
                controller,
                this,
                previousFlowOutcome,
                destination);

    }

    /*
     * (non-Javadoc)
     * @see org.sgodden.ui.mvc.NamedObjectResolver#getNamedObject(java.lang.String)
     */
    public Object getNamedObject(String objectName) {
        if (namedObjects.containsKey(objectName)) {
            return namedObjects.get(objectName);
        } else {
            throw new IllegalArgumentException("Unknown named object: " + objectName);
        }
    }

    /**
     * Abstract base class for flow steps.
     * 
     * @author sgodden
     *
     */
    protected abstract class FlowStep {

        private String flowStepName;
        private Map<String, Set<QualifiedResolutionMapping>> resolutionMappings = new HashMap<String, Set<QualifiedResolutionMapping>>();

        /**
         * Creates a new flow step.
         * 
         * @param flowStepName the name of the flow step, which must be unique.
         * @param configurationProperties an optional map of configuration properties for this step.
         */
        protected FlowStep(
                String flowStepName) {
            super();
            this.flowStepName = flowStepName;
        }

        /**
         * Adds a resolution mapping to the flow step.
         * @param resolutionName the name of the resolution.
         * @param guard an optional guard which, if passed, must evaluate to true in order for this mapping to be selected at runtime.
         * @param destination the destination.
         */
        public void addResolutionMapping(
                String resolutionName,
                Guard guard,
                Destination destination) {

            Set<QualifiedResolutionMapping> mappings = resolutionMappings.get(resolutionName);

            if (mappings == null) {
                mappings = new HashSet<QualifiedResolutionMapping>();
                resolutionMappings.put(resolutionName, mappings);
            }

            mappings.add(new QualifiedResolutionMapping(guard, destination));

        }

        /**
         * Returns a map of resolution names available from this flow step, and actions which provide the view elements (title, icon) for the resolution.
         * @return
         */
        public Set<String> getAvailableResolutions() {
            return resolutionMappings.keySet();
        }

        /**
         * Returns the flow step name.
         * @return
         */
        public String getFlowStepName() {
            return flowStepName;
        }
    }

    /**
     * A flow step which delegates execution to a controller.
     * 
     * @author sgodden
     *
     */
    protected class ControllerFlowStep
            extends FlowStep {

        private String managedObjectName;

        /**
         * Creates a new controller flow step.
         * @param flowStepName the name of the flow step, which must be unique.
         * @param managedObjectName the name of the managed object which will provide the controller function.
         * @param configurationProperties an optional set of configuration properties for this step.
         */
        protected ControllerFlowStep(
                String flowStepName,
                String managedObjectName) {
            super(flowStepName);
            this.managedObjectName = managedObjectName;
        }

        /**
         * Returns the controller instance.
         * @return the controller instance.
         */
        public Object getController() {
            return getNamedObject(managedObjectName);
        }
    }

    /**
     * A flow step which displays a view.
     * @author sgodden
     *
     */
    protected class ViewFlowStep
            extends FlowStep {

        private View view;

        /**
         * Creates a new view flow step.
         * @param flowStepName the name of the flow step, which must be unique.
         * @param viewClass the view class.
         */
        private ViewFlowStep(String flowStepName) {
            super(flowStepName);
        }

        /**
         * Creates the view instance.
         * @return
         */
        private View getView() {
            if (view == null) {
                view = (View) getNamedObject(getFlowStepName());
            }

            return view;
        }
    }

    /**
     * A qualified resolution mapping.  The resolution mapping is only selected
     * if either a) it has a null guard object, or b) the guard evaluates to true
     * at runtime.
     * 
     * @author sgodden
     *
     */
    private static class QualifiedResolutionMapping {

        private Guard guard;
        private Destination destination;
        private String controllerMethodName;

        public QualifiedResolutionMapping(
        		Guard guard, 
        		Destination destination) {
            super();
            this.guard = guard;
            this.destination = destination;
        }
    }
}
