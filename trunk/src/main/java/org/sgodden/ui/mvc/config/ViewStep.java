package org.sgodden.ui.mvc.config;

import java.io.Serializable;

import org.sgodden.ui.mvc.View;

/**
 * Defines a view step within a particular flow.
 * <p/>
 * Note that the step name must match the name of
 * one named object in the flow, and that object
 * must implement {@link View}.
 * 
 * @author sgodden
 *
 */
public class ViewStep
		extends FlowStep
		implements Serializable {
	
	private static final long serialVersionUID = 1L;
}
