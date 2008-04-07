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

import java.util.Set;

import javax.swing.Action;


/**
 * A flow resolution pointing to a view as the next invocation step.
 */
public interface ViewFlowOutcome
    extends FlowOutcome {

    /**
     * Returns the view which should be invoked next.
     *
     * @return the view.
     */
    public View getView();
    
	/**
	 * Returns a map of all available resolutions from this flow step, 
	 * where the key is the name of the resolution, 
	 * and the value is a corresponding {@link Action}
	 * to provide the necessary presentation information, such as icon, label etc.
	 * @return the map of available resolutions from this flow step.
	 */
	public Set<String> getAvailableResolutions();


}
