package org.sgodden.entities;

import java.util.Locale;

/**
 * Represents an object that can have
 * a label which is meaningful to a  user.
 * @author goddens
 *
 */
public interface LabelProvider {
	
	/**
	 * Returns the label for the object.
	 * <p/>
	 * This does not use the normal 'get' convention so that it is
	 * not confused with regular persistent properties on an entity.
	 * <p/>
	 * FIXME - not using the normal get naming convention is ugly.
	 * 
	 * @param locale
	 * @return
	 */
	public String provideLabel(Locale locale);

}
