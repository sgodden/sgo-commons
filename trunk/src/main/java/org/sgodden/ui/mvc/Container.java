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
 * A simple toolkit-independent interface for a container which can display a view
 * (a single component occupying the entire space), and modal dialogs over the top of it.
 * <p/>
 * FIXME - revisit the handling of dialogs / windows.
 * <p/>
 * FIXME - setting the title of the container.
 * 
 * @author sgodden
 *
 */
public interface Container {
	
	/**
	 * Fills the container with the specified view.
	 * 
	 * @param view
	 */
	public void display(View view);
	
	/**
	 * Displays the passed view as a modal dialog centred
	 * on the current container.
	 * <p/>
	 * FIXME - the container can work out for itself if the passed
	 * view is modal.  Maybe need a Window interface and change
	 * this to displayWindow(Window window).
	 * 
	 * @param view
	 */
	public void displayModalDialog(View view);
	
	/**
	 * Closes the open dialog.
	 * <p/>
	 * FIXME - the close method on the dialog view interface should be called instead, to allow for multiple open dialogs.
	 * @throws IllegalStateException if no dialog is open.
	 */
	public void closeDialog();

}
