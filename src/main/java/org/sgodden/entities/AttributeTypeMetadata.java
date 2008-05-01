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
package org.sgodden.entities;

import java.io.Serializable;

/**
 * A dictionary of metadata for attribute definitions, for instance 'order number',
 * or 'customer reference'.
 * <p/>
 * Contains constraints for the attribute such as maximum length.
 * <p/>
 * TODO - would probably good to have typical max length as well as absolute
 * max length.  Typical max length would influence size of field on screen.
 * 
 * @author goddens
 *
 */
public class AttributeTypeMetadata 
	implements Serializable {
	
	private int maxLength;
	private int precision;
	
	/**
	 * Default constructor for attribute types such as dates which
	 * currently have no metadata.
	 */
	private AttributeTypeMetadata(){}
	
	private AttributeTypeMetadata(int maxLength){
		this.maxLength = maxLength;
	}
	
	private AttributeTypeMetadata(int maxLength, int precision){
		this.maxLength = maxLength;
		this.precision = precision;
	}

	/**
	 * Returns the maximum length of this attribute - in the case of 
	 * numeric values, functions as the scale.
	 * 
	 * @return the maximum length.
	 */
	public int getMaxLength() {
		return maxLength;
	}
	
	/**
	 * Returns the precision for a numeric value.
	 * 
	 * @return the numeric precision.
	 */
	public int getPrecision() {
		return precision;
	}

}
