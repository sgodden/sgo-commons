package org.sgodden.entities.metadata;

public class CompositionMetadata {
	
	private EntityMetadata entityMetadata;
	private int displaySequence; // FIXME - this is confusion of concerns
	
	public int getDisplaySequence() {
		return displaySequence;
	}
	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
	}
	public EntityMetadata getEntityMetadata() {
		return entityMetadata;
	}
	public void setEntityMetadata(EntityMetadata entityMetadata) {
		this.entityMetadata = entityMetadata;
	}
}