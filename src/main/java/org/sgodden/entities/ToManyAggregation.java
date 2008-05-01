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

import org.sgodden.entities.metadata.EntityMetadata;

public class ToManyAggregation 
		implements Serializable {
	
	private String relationName;
	private EntityReference[] references;
	private EntityMetadata entityMetadata;
	
	public ToManyAggregation(String relationName, EntityReference[] references, EntityMetadata entityMetadata) {
		super();
		this.relationName = relationName;
		this.references = references;
		this.entityMetadata = entityMetadata;
	}
	public EntityReference[] getReferences() {
		return references;
	}
	public void setReferences(EntityReference[] references) {
		this.references = references;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public EntityMetadata getEntityMetadata() {
		return entityMetadata;
	}
	public void setEntityMetadata(EntityMetadata entityMetadata) {
		this.entityMetadata = entityMetadata;
	}
	
	public ToManyAggregation makeClone() {
		EntityReference[] cloneReferences = new EntityReference[references.length];
		
		int i = 0;
		
		for (EntityReference reference : references) {
			cloneReferences[i] = references[i].makeClone();
			i++;
		}
		
		return new ToManyAggregation(relationName, cloneReferences, entityMetadata);
		
	}
	

}
