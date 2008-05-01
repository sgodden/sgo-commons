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

import org.sgodden.entities.impl.DefaultEntityInstanceImpl;

public class ToOneChildEntry 
		implements Serializable {
	
	private String relationName;
	private EntityInstance instance;
	private String label;
	
	public ToOneChildEntry(String relationName, EntityInstance instance, String label) {
		super();
		this.relationName = relationName;
		this.instance = instance;
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public EntityInstance getInstance() {
		return instance;
	}
	public void setInstance(EntityInstance instance) {
		this.instance = instance;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	
	public ToOneChildEntry makeClone() {
		return new ToOneChildEntry(relationName, instance.makeClone(), label);
	}

}
