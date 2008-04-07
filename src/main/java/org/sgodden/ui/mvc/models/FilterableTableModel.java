package org.sgodden.ui.mvc.models;

import java.util.Map;

import org.sgodden.ui.mvc.Model;
import org.sgodden.ui.mvc.messages.MessageModel;

/**
 * A table model that may be filtered.
 * 
 * @author simon
 *
 */
public interface FilterableTableModel 
		extends Model {
	
	/**
	 * Refreshes the model based on the passed filter model.
	 * <p/>
	 * FIXME - a simple map paradigm is insufficient for all but the simplest cases.
	 * 
	 * @param filterMap
	 */
	public void refresh(Map<String, Object> filterModel);
	
	/**
	 * Refreshes the model based on the passed filter model, and emits
	 * results messages into the passed message model.
	 * 
	 * @param filterModel a map of attribute path to filter values.
	 * @param messageModel a message model to which to add results messages.
	 */
	public void refresh(Map<String, Object> filterModel, MessageModel messageModel);

}
