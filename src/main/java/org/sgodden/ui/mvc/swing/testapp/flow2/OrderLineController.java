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
package org.sgodden.ui.mvc.swing.testapp.flow2;

import org.apache.log4j.Logger;
import org.sgodden.ui.mvc.Context;
import org.sgodden.ui.mvc.ContextAware;

/**
 * A simulated implementation of a controller for the order lines.
 * @author sgodden
 */
public class OrderLineController implements ContextAware {

    private static final Logger log = Logger.getLogger(
            OrderLineController.class);

    private Context context;
	
	public String save() {
        log.debug("Save");
		String ret = "SUCCESS";
		return ret;
	}

    public void exportOrderLine() {
        log.debug("Exporting order line");
        context.setFlowVariable("orderLine", "I AM AN ORDER LINE" +
                " CREATED BY THE ORDER LINE FLOW");
    }

    public void setContext(Context context) {
        log.debug("Setting context");
        this.context = context;
    }

}
