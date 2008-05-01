package org.sgodden.entities.metadata;


/**
 * Provides metadata relating to a certain entity class.
 * 
 * @author goddens
 *
 */
public class EntityMetadata {
	
	private String className;
	private SimpleInstanceAttributeMetadata[] propertyMetadatas;
	private ToOneAggregationMetadata[] toOneAggregationMetadatas;
	private ToManyAggregationMetadata[] toManyAggregationMetadatas;
	private ToOneCompositionMetadata[] toOneCompositionMetadatas;
	private ToManyCompositionMetadata[] toManyCompositionMetadatas;
	private boolean localeDependent = false;
	
	public void setLocaleDependent(boolean localeDependent) {
		this.localeDependent = localeDependent;
	}

	public boolean isLocaleDependent() {
		return localeDependent;
	}

	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public SimpleInstanceAttributeMetadata[] getSimpleInstanceAttributeMetadatas() {
		return propertyMetadatas;
	}
	
	public void setSimpleInstanceAttributeMetadatas(SimpleInstanceAttributeMetadata[] propertyMetadatas) {
		this.propertyMetadatas = propertyMetadatas;
	}
	
	public SimpleInstanceAttributeMetadata getSimpleInstanceAttributeMetadata(String attributeName){
		SimpleInstanceAttributeMetadata ret = null;
		for (SimpleInstanceAttributeMetadata metadata : propertyMetadatas) {
			if (metadata.getAttributeName().equals(attributeName)) {
				ret = metadata;
			}
		}
		return ret;
	}
	
	public ToManyAggregationMetadata[] getToManyAggregationMetadatas() {
		return toManyAggregationMetadatas;
	}
	
	public void setToManyAggregationMetadatas(
			ToManyAggregationMetadata[] toManyAggregationMetadatas) {
		this.toManyAggregationMetadatas = toManyAggregationMetadatas;
	}
	
	public ToManyAggregationMetadata getToManyAggregrationMetadata(String attributeName) {
		ToManyAggregationMetadata ret = null;
		
		for (ToManyAggregationMetadata md : toManyAggregationMetadatas) {
			if (md.getAttributeName().equals(attributeName)) {
				ret = md;
			}
		}
		
		return ret;
	}
	
	public ToManyCompositionMetadata[] getToManyCompositionMetadatas() {
		return toManyCompositionMetadatas;
	}
	
	public void setToManyCompositionMetadatas(
			ToManyCompositionMetadata[] toManyCompositionMetadatas) {
		this.toManyCompositionMetadatas = toManyCompositionMetadatas;
	}
	
	public ToManyCompositionMetadata getToManyCompositionMetadata(String attributeName) {
		ToManyCompositionMetadata ret = null;
		
		for (ToManyCompositionMetadata md : toManyCompositionMetadatas) {
			if (md.getAttributeName().equals(attributeName)) {
				ret = md;
			}
		}
		
		return ret;
	}
	
	public ToOneAggregationMetadata[] getToOneAggregationMetadatas() {
		return toOneAggregationMetadatas;
	}
	
	public void setToOneAggregationMetadatas(
			ToOneAggregationMetadata[] toOneAggregationMetadatas) {
		this.toOneAggregationMetadatas = toOneAggregationMetadatas;
	}
	
	public ToOneAggregationMetadata getToOneAggregationMetadata(String attributeName) {
		ToOneAggregationMetadata ret = null;
		
		for (ToOneAggregationMetadata md : toOneAggregationMetadatas) {
			if (md.getAttributeName().equals(attributeName)) {
				ret = md;
			}
		}
		
		return ret;
	}
	
	public ToOneCompositionMetadata[] getToOneCompositionMetadatas() {
		return toOneCompositionMetadatas;
	}
	
	public void setToOneCompositionMetadatas(
			ToOneCompositionMetadata[] toOneCompositionMetadatas) {
		this.toOneCompositionMetadatas = toOneCompositionMetadatas;
	}
	
	public ToOneCompositionMetadata getToOneCompositionMetadata(String attributeName) {
		ToOneCompositionMetadata ret = null;
		
		for (ToOneCompositionMetadata md : toOneCompositionMetadatas) {
			if (md.getAttributeName().equals(attributeName)) {
				ret = md;
			}
		}
		
		return ret;
	}
	
	public InstanceAttributeMetadata getInstanceAttributeMetadata(String attributeName) {
		
		InstanceAttributeMetadata ret = getSimpleInstanceAttributeMetadata(attributeName);
		
		if ( ret == null ) {
			ret = getToOneAggregationMetadata(attributeName);
		}
		
		if ( ret == null ) {
			throw new IllegalArgumentException("Unknown attribute name: " + attributeName);
		}
		
		return ret;
	}

}
