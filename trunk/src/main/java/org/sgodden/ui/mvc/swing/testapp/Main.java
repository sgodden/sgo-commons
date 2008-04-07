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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFrame;

import org.sgodden.ui.mvc.FrontController;
import org.sgodden.ui.mvc.impl.ActionFactory;
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
		
		FlowImpl flow = new TestFlowImpl();
		
		/*
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
		
		List<ActionFactory> actionFactories = new ArrayList<ActionFactory>();
		actionFactories.add(new ActionFactoryImpl());
		
		ContainerImpl container = new ContainerImpl();
		FrontController front = new FrontController(container, flow);
		
		JFrame frame = new JFrame("Simple MVC test app");
		frame.getContentPane().add(container);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, 300);
		//frame.pack();
		frame.setVisible(true);
		
	}
	
	public static class ActionFactoryImpl
			implements ActionFactory {

		public Action getAction(String resolutionName) {
			if ("CANCEL".equals(resolutionName)) {
				return new CancelAction();
			}
			else if ("EDIT".equals(resolutionName)) {
				return new EditAction();
			} 
			else if ("SAVE".equals(resolutionName)) {
				return new SaveAction();
			} 
			else {
				throw new IllegalArgumentException(resolutionName);
			}
		}
		
	}

}
