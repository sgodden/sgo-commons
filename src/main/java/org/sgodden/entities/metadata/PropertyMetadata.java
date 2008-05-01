package org.sgodden.entities.metadata;

import java.util.Locale;

import org.sgodden.entities.LabelProvider;

/**
 * An abstract base class for metadata for all properties of a class.
 * 
 * @author goddens
 *
 */
public abstract class PropertyMetadata 
		implements LabelProvider {
	
	private String label;
	private String attributeName;
	
	public PropertyMetadata(String label, String attributeName) {
		super();
		this.label = label;
		this.attributeName = attributeName;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String provideLabel(Locale locale){ // TODO - I don't like this interface
		return getLabel();
	}
	
}