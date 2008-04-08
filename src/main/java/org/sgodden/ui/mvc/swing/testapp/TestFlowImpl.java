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

import org.sgodden.ui.mvc.impl.FlowImpl;

/**
 * A test flow configuration.
 * @author sgodden
 */
public class TestFlowImpl 
		extends FlowImpl {
	
	public TestFlowImpl(){
		super();
		
		addViewStep("listView");
		addViewStep("editView");
		
		addControllerStep("saveController", "maintenanceController");
		
		addResolutionMapping("listView", "EDIT", null, "editView");
		
		addResolutionMapping("editView", "CANCEL", null, "listView");
		addResolutionMapping("editView", "SAVE", null, "saveController");
		
		addResolutionMapping("saveController", "SUCCESS", null, "listView");
	}

	@Override
	protected String getInitialViewName() {
		return "listView";
	}

}
