package org.sgodden.ui.mvc.impl;

/**
 * An arbitrary guard which can be placed upon a destination mapping
 * to qualify whether that destination should be chosen.
 * @author sgodden
 *
 */
public interface Guard {
	
	public boolean approve();

}
