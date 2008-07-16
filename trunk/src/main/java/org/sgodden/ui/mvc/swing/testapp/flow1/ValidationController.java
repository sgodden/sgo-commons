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
package org.sgodden.ui.mvc.swing.testapp.flow1;

import org.apache.log4j.Logger;
import org.sgodden.ui.mvc.Context;
import org.sgodden.ui.mvc.ContextAware;

/**
 * A simulated implementation of an order validation controller.
 * @author sgodden
 */
public class ValidationController implements ContextAware {
	
    private static final Logger log = Logger.getLogger
            (ValidationController.class);
    
    private Context context;

	public String validate() {
		String ret = "SUCCESS";

        String order = (String) context.getFlowVariable("order");
        String messageModel = (String) context.getFlowVariable("messageModel");
        Boolean fail = (Boolean) context.getFlowVariable("fail");
        if (fail == null) {
            fail = false;
        }

        log.debug("Validate");
        log.debug("Order = " + order);
        log.debug("Message model = " + messageModel);
        log.debug("Fail = " + fail);

        /*
         * Have we been asked to simulate a failed validation?
         */
        if (fail) {
            ret = "FAIL";
        }

        return ret;
	}

    public String save() {
        return "SUCCESS";
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
