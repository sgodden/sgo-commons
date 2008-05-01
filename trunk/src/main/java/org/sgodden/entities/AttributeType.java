package org.sgodden.entities;

public enum AttributeType {
	
	ADDRESS_LINE,
	BUILDING_NAME,
	BUILDING_NUMBER,
	/**
	 * A general purpose code to serve as a unique customer-visible identifier.
	 */
	CODE,
	DESCRIPTION,
	DESCRIPTION_OF_GOODS,
	GROSS_WEIGHT,
	LINE_ITEM_NUMBER,
	LOCALE, // FIXME - this may well be wrong, and locale may well be supposed to be a many-to-one aggregation
	LOCALITY,
	MARKS,
	NAME,
	ORDER_DATE,
	ORDER_NUMBER,
	PIECES,
	POSTAL_CODE,
	REFERENCE,
	STATE,
	TOWN

}
