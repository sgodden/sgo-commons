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

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.sgodden.ui.mvc.FrontController;
import org.sgodden.ui.mvc.config.ControllerStep;
import org.sgodden.ui.mvc.config.ResolutionMapping;
import org.sgodden.ui.mvc.config.ViewStep;
import org.sgodden.ui.mvc.impl.FlowImpl;
import org.sgodden.ui.mvc.swing.ContainerImpl;

/**
 * Provides a simple swing test app for the MVC framework.
 * 
 * @author sgodden
 *
 */
public class Main {
	
	public static void main(String[] args) {
		
		FlowImpl flow = new FlowImpl();
		
		/*
		 * Create the named objects (controllers and views).
		 * In real life, everything would normally be configured via
		 * your dependency-injection provider.
		 */
		Map<String, Object> namedObjects = new HashMap<String, Object>();
		
		MaintenanceController maintenanceController = new MaintenanceController();
		namedObjects.put("maintenanceController", maintenanceController);
		
		namedObjects.put("listView", new ListPanel());
		
		EditPanel editPanel = new EditPanel();
		editPanel.setMaintenanceController(maintenanceController);
		namedObjects.put("editView", editPanel);
		
		flow.setNamedObjects(namedObjects);
		
		/*
		 * Set the controller steps.
		 */
		ControllerStep cstep = new ControllerStep();
		cstep.setStepName("saveController");
		cstep.setObjectName("maintenanceController");
		flow.setControllerSteps(new ControllerStep[]{cstep});
		
		/*
		 * Set the view steps.
		 */
		ViewStep vstep = new ViewStep();
		vstep.setStepName("listView");
		
		ViewStep vstep2 = new ViewStep();
		vstep2.setStepName("editView");
		
		flow.setViewSteps(new ViewStep[]{vstep, vstep2});
		flow.setInitialViewName("listView");
		
		/*
		 * Set the resolution mappings.
		 */
		flow.setResolutionMappings(new ResolutionMapping[]{
				createResolutionMapping("listView", "EDIT", "editView", null),
				createResolutionMapping("editView", "CANCEL", "listView", null),
				createResolutionMapping("editView", "SAVE", "saveController", "save"),
				createResolutionMapping("saveController", "SUCCESS", "listView", null),
		});
		
		ContainerImpl container = new ContainerImpl();
		FrontController front = new FrontController(container, flow);
		
		JFrame frame = new JFrame("Simple MVC test app");
		frame.getContentPane().add(container);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, 300);
		//frame.pack();
		frame.setVisible(true);
		
	}
	
	private static ResolutionMapping createResolutionMapping(String source, String resolution, String destination, String methodName) {
		ResolutionMapping ret = new ResolutionMapping();
		ret.setSourceStepName(source);
		ret.setResolutionName(resolution);
		ret.setDestinationStepName(destination);
		ret.setControllerMethodName(methodName);
		
		return ret;
	}

}
