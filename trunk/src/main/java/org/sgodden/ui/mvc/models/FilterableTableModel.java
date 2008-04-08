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
