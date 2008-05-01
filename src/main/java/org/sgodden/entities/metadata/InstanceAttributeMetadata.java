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
package org.sgodden.entities.metadata;

import java.io.Serializable;

/**
 * An abstract base class for all singular attributes of a class
 * (primitive properties and to-one aggregations).
 * @author goddens
 *
 */
public abstract class InstanceAttributeMetadata
		extends PropertyMetadata 
		implements Serializable {

	private boolean unique;
	private boolean uniqueInSet;
	
	public InstanceAttributeMetadata(
			String label, 
			String attributeName,
			boolean unique,
			boolean uniqueInSet) {
		super(label, attributeName);
		this.unique = unique;
		this.uniqueInSet = uniqueInSet;
	}
	
	public boolean getUnique() {
		return unique;
	}
	public boolean getUniqueInSet() {
		return uniqueInSet;
	}

}
