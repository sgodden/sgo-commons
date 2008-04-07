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


/**
 * Encapsulates the outcome of a step in the flow.
 * <p/>
 * It contains the relevant controller or view, configured and ready to be executed.
 * <p/>
 * In order to the progress to the next flow stop, the 
 * {@link #getFlowOutcome(String, ResolutionHandler)}
 * method on this same object is called.
 * <p/>
 * TODO - do we need this, or would it suffice to return the flow step. That would mean making SFRF the api, rather than just one implementation.
 * 
 * @author goddens
 *
 */
public interface FlowOutcome {
	
	/**
	 * Returns a new FlowResolution object encapsulating the outcome of the flow
	 * in response to the passed controller resolution.
	 * 
	 * @param resolutionName the name of the resolution from the previous step.
	 * @param resolutionHandler the handler for any resolutions that come directly from views.
	 * @return the new FlowResolution, containing the destination controller or view.
	 */
	public FlowOutcome getFlowOutcome(
			String resolutionName);
	
	/**
	 * Returns a named object resolver for the flow resolution factory
	 * which created this resolution.
	 * <p/>
	 * This will be used to resolve objects for the conversation context.
	 * @return
	 */
	public NamedObjectResolver getNamedObjectResolver();

}
