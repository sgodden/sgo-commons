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
import java.util.HashMap;
import java.util.Map;

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
	
	private static Map<AttributeType, AttributeTypeMetadata> dictionary = new HashMap<AttributeType, AttributeTypeMetadata>();
	
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
	
	/**
	 * Returns the metadata for the specified attribute type.
	 * @param attributeType the attribute type.
	 * @return the metadata.
	 */
	public static AttributeTypeMetadata get(AttributeType attributeType){
		AttributeTypeMetadata ret = dictionary.get(attributeType);
		if (ret == null){
			throw new IllegalArgumentException("Unknown attribute type: " + attributeType);
		}
		return ret;
	}

	// and now all the enumeration constants in alphabetical order please
	
	//public static final AttributeTypeMetadata CODE = new AttributeTypeMetadata(17);
	
	static {
		dictionary.put(AttributeType.ADDRESS_LINE, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.BUILDING_NAME, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.BUILDING_NUMBER, new AttributeTypeMetadata(7, 0));
		dictionary.put(AttributeType.CODE, new AttributeTypeMetadata(17));
		dictionary.put(AttributeType.DESCRIPTION_OF_GOODS, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.DESCRIPTION, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.GROSS_WEIGHT, new AttributeTypeMetadata(9, 2));
		dictionary.put(AttributeType.LOCALE, new AttributeTypeMetadata(10));
		dictionary.put(AttributeType.LOCALITY, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.LINE_ITEM_NUMBER, new AttributeTypeMetadata(3, 0));
		dictionary.put(AttributeType.MARKS, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.NAME, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.ORDER_NUMBER, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.ORDER_DATE, new AttributeTypeMetadata());
		dictionary.put(AttributeType.PIECES, new AttributeTypeMetadata(7, 0));
		dictionary.put(AttributeType.POSTAL_CODE, new AttributeTypeMetadata(17));
		dictionary.put(AttributeType.REFERENCE, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.STATE, new AttributeTypeMetadata(35));
		dictionary.put(AttributeType.TOWN, new AttributeTypeMetadata(35));
	}

}
