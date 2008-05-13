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

/**
 * A simulated implementation of a maintenance controller.
 * @author sgodden
 */
public class MaintenanceController {
	
	private boolean fail = false;

	/**
	 * Sets whether the next call to {@link #save()} should simulate
	 * validation failure.
	 * 
	 * @param fail whether to fail the next save call.
	 */
	public void setFail(boolean fail) {
		this.fail = fail;
	}
	
	public String validate() {
		debug("VALIDATE");
		return "SUCCESS";
	}
	
	public String save() {
		debug("SAVE");
		String ret = "SUCCESS";
		
		if (fail) {
			debug("Simulated fail - returning null to redisplay the same view");
			ret = null;
		}
		
		return ret;
	}
	
	private static void debug(String msg) {
		System.out.println(MaintenanceController.class + ": " + msg);
	}

}
