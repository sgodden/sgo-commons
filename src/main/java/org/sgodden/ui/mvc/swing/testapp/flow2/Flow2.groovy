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
package org.sgodden.ui.mvc.swing.testapp.flow2

import org.sgodden.ui.mvc.impl.FlowImpl
import org.sgodden.ui.mvc.config.ViewStep
import org.sgodden.ui.mvc.config.ResolutionMapping

/**
 * A second flow in the test app.
 * @author sgodden
 */
class Flow2 extends FlowImpl {

    Flow2(){
        namedObjects = [
            listView: new ListPanel()
        ]

        viewSteps = [
            new ViewStep(
                name: "listView",
                description: "Flow 2 list view"
            )
        ]

        initialViewName = "listView"

        resolutionMappings = [
            new ResolutionMapping(
                sourceStepName: "listView",
                resolutionName: "CANCEL",
                terminationResolutionName: "SUCCESS"
            )
        ]
    }
}