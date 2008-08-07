package org.sgodden.ui.mvc.impl;

import org.sgodden.ui.mvc.Context;

/**
 * An arbitrary guard which can be placed upon a destination mapping
 * to qualify whether that destination should be chosen.
 * @author sgodden
 *
 */
public interface Guard {
	
    /**
     * Returns whether this mapping should be selected.
     * @param ctx the flow context, to allow the guard to evaluate
     * state in the flow.
     * @return true to approve transition to the destination, false
     * to reject it.
     */
	public boolean approve(Context ctx);

}
