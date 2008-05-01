package org.sgodden.entities;

/**
 * An object which returns attribute type metadata
 * for attribute type names.
 * @author sgodden
 */
public interface AttributeTypeMetadataProvider {
	
	/**
	 * Returns metadata for the named attribute type.
	 * @param attributeTypeName the attribute type name.
	 * @return the attribute type metadata.
	 */
	public AttributeTypeMetadata getMetadata(String attributeTypeName);

}
