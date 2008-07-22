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

import org.sgodden.ui.mvc.config.FlowStep
import org.sgodden.ui.mvc.impl.FlowImpl

/**
 * A second flow in the test app.
 * @author sgodden
 */
class OrderFlow extends FlowImpl {

    OrderFlow(){
        namedObjects = [
            controller: new MaintenanceController(),
            listView: new ListPanel(),
            editView: new EditPanel()
        ]

        initialStepName = "listView"
        
        steps = [
              [
                 name: "listView",
                 viewName: "listView",
                 description: "Flow 1 list view",
                 transitions: [ 
                     [on: "EDIT", to: "editView"]
                 ]
             ],
             [
                 name: "editView",
                 viewName: "editView",
                 description: "Flow 1 edit view",
                 transitions: [
                     [on: "CANCEL", to: "listView"],
                     [on: "SAVE", to: "validateAndSave"],
                     [on: "SUBFLOW", to: "orderLine"]
                 ]
             ],
             [
                 name: "validateAndSave",
                 entryControllerMethods: ["controller#exportForValidate"],
                 subFlowName: "ValidateAndSaveOrder",
                 subFlowInputParameters: [
                     "order" : '${asdasd}',
                     "messageModel" : '${messageModel}',
                     "fail" : '${controller.fail}'
                 ],
                 transitions: [
                     [on: "FAIL", to: "*LAST-VIEW",
                         controllerMethods: ["controller#setValidationFailed"]],
                     [on: "SUCCESS", to: "orderLine",
                         controllerMethods: ["controller#setValidationSucceeded"]]
                 ]
             ],
             [
                 name: "orderLine",
                 subFlowName: "OrderLineFlow",
                 subFlowInputParameters: [
                     "orderLineId" : "orderLineId"
                 ],
                 subFlowOutputParameters: [
                     "orderLine" : "addedOrderLine"
                 ],
                 transitions: [
                     [on: "SUCCESS", to: "editView"]
                 ],
                 exitControllerMethods: ["controller#addOrderLine","controller#reload"]
             ]
        ]
    }
}