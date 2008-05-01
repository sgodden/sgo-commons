package org.sgodden.entities.metadata;

import org.sgodden.entities.AttributeTypeMetadata;
import org.sgodden.entities.DataType;

public class SimpleInstanceAttributeMetadata extends InstanceAttributeMetadata {
	
	private DataType dataType;
	private AttributeTypeMetadata attributeTypeMetadata;

	public SimpleInstanceAttributeMetadata(
			String label, 
			String attributeName,
			boolean unique, 
			boolean uniqueInSet, 
			DataType dataType,
			AttributeTypeMetadata attributeTypeMetadata
			) {
		super(label, attributeName, unique, uniqueInSet);
		this.dataType = dataType;
		this.attributeTypeMetadata = attributeTypeMetadata;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public int getMaxLength() {
		return attributeTypeMetadata.getMaxLength();
	}

	public AttributeTypeMetadata getAttributeTypeMetadata() {
		return attributeTypeMetadata;
	}

	public void setAttributeTypeMetadata(AttributeTypeMetadata attributeTypeMetadata) {
		this.attributeTypeMetadata = attributeTypeMetadata;
	}

}
