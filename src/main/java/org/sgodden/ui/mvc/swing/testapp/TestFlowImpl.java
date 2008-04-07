package org.sgodden.ui.mvc.swing.testapp;

import org.sgodden.ui.mvc.impl.FlowImpl;

public class TestFlowImpl 
		extends FlowImpl {
	
	public TestFlowImpl(){
		super();
		
		addViewConfiguration("listView");
		addViewConfiguration("editView");
		
		addControllerConfiguration("saveController", "maintenanceController");
		
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
