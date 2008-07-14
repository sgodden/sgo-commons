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
package org.sgodden.ui.mvc.swing.testapp.flow1

import org.sgodden.ui.mvc.impl.FlowImpl
import org.sgodden.ui.mvc.config.ViewStep
import org.sgodden.ui.mvc.config.ResolutionMapping
import org.sgodden.ui.mvc.config.ControllerStep

/**
 * A second flow in the test app.
 * @author sgodden
 */
class Flow1 extends FlowImpl {

    Flow1(){
        namedObjects = [
            maintenanceController: new MaintenanceController(),
            listView: new ListPanel(),
            editView: new EditPanel()
        ]

        controllerSteps = [
            new ControllerStep(
                name: "saveController",
                objectName: "maintenanceController",
                methodName: "save"
            ),
            new ControllerStep(
                name: "validateController",
                objectName: "maintenanceController",
                methodName: "validate"
            )
        ]

        viewSteps = [
            new ViewStep(
                name: "listView",
                description: "Flow 1 list view"
            ),
            new ViewStep(
                name: "editView",
                description: "Flow 1 edit view"
            )
        ]

        initialViewName = "listView"

        resolutionMappings = [
            new ResolutionMapping(
                sourceStepName: "listView",
                resolutionName: "EDIT",
                destinationStepName: "editView"
            ),
            new ResolutionMapping(
                sourceStepName: "editView",
                resolutionName: "CANCEL",
                destinationStepName: "listView"
            ),
            new ResolutionMapping(
                sourceStepName: "editView",
                resolutionName: "SAVE",
                destinationStepName: "validateController"
            ),
            new ResolutionMapping(
                sourceStepName: "editView",
                resolutionName: "SUBFLOW",
                subFlowName: "flow2",
                subFlowParameters: [
                    "outputFlow1" : "inputFlow2"
                ]
            ),
            new ResolutionMapping(
                sourceStepName: "validateController",
                resolutionName: "SUCCESS",
                destinationStepName: "saveController"
            ),
            new ResolutionMapping(
                sourceStepName: "saveController",
                resolutionName: "SUCCESS",
                destinationStepName: "listView"
            )
        ]
    }
}