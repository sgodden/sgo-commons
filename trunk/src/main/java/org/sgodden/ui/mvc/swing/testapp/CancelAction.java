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
package org.sgodden.ui.mvc.swing.testapp;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.sgodden.ui.mvc.Context;

/**
 * A cancel action.
 * @author sgodden
 *
 */
public class CancelAction 
		extends AbstractAction {
	
	private Context context;
	
	public CancelAction(){
		super("Cancel"); // FIXME - i18n
		context = Context.getCurrentContext();
	}

	/**
	 * Default implementation of actionPerformed which simply
	 * fires a resolution named CANCEL.
	 */
	public void actionPerformed(ActionEvent e) {
		context.handleResolution("CANCEL");
	}

}
