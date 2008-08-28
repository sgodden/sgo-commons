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

import java.util.Map;

/**
 * Interface for an object which handles a set of view
 * and controller linkages to form a view 'flow'.
 * <p/>
 * As the user takes actions in views, named resolutions
 * are fired, and this object decides where to go next
 * based on those resolution names, and potentially also
 * on the state of the model at that time.
 * <p/>
 * In principle, this is very similar to the basic concepts
 * of struts or other web MVC implementations.  However, there
 * seemed to be no similar implementation for OO view frameworks
 * such as Swing, Echo, Millstone etc.  This is a simple
 * implementation to fill that gap.
 * <p/>
 * <b>Note -</b>The API is designed in a stateless paradigm, but flows
 * are not stateless, so this will probably be re-factored, to remove the need
 * to hold a reference to the previous flow outcome in order to ask for the
 * next one.     
 * 
 * @author sgodden
 *
 */
public interface Flow 
		extends NamedObjectResolver {
	
	/**
	 * Returns the next flow resolution.
	 * 
	 * @param resolutionName the name of the resolution that occurred in the current view or controller.
	 * @param previousFlowResolution the previous flow resolution.
	 * @param resolutionHandler ???
	 * @return
	 */
	public FlowOutcome getFlowOutcome(
			String resolutionName,
			FlowOutcome previousFlowResolution);

    /**
     * Returns the value of the specified flow variable, or <code>null</code>
     * if no variable by that name is set.
     * @param name the name of the variable.
     * @return the value, or <code>null</code> if no value is set.
     */
    public Object getVariable(String name);

    /**
     * Sets or unsets a variable on the flow.
     * @param name the name of the variable.
     * @param value the value, or <code>null</code> to unset the variable.
     */
    public void setVariable(String name, Object value);
 
    /**
     * Returns an unmodifiable map of flow variables.
     * @return an unmodifiable map of flow variables.
     */
    public Map < String, Object > getVariables();

}