package org.sgodden.ui.mvc.swing.testapp;

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
