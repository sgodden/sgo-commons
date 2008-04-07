package org.sgodden.ui.mvc.impl;

import javax.swing.Action;

/**
 * Creates instances of {@link Action} from resolution names, in order
 * to provide the appropriate presentation elements, such as icon, label, 
 * keyboard shortcut and mnemonic, when those available resolutions
 * are displayed to the user.
 * 
 * @author goddens
 *
 */
public interface ActionFactory {
	
	/**
	 * Returns an {@link Action} instance for the passed
	 * resolution name, or <code>null</code> if the resolution
	 * name is not handled by this factory.
	 * @param resolutionName the resolution name (such as 'SAVE' or 'CANCEL').
	 * @return an appropriately configured {@link Action} instance, or <code>null</code>.
	 */
	public Action getAction(String resolutionName);

}
