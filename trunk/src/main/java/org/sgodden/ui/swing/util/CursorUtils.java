package org.sgodden.ui.swing.util;

import java.awt.Component;
import java.awt.Cursor;

public class CursorUtils {

	public static void setBusyCursor(Component c){
		c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	public static void setDefaultCursor(Component c){
		c.setCursor(Cursor.getDefaultCursor());
	}
	
}
