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


public class ToOneAggregation 
		extends InstanceAttribute {
	
	private EntityReference reference;
	private String code;
	
	public ToOneAggregation(
			String relationName, 
			String label, 
			EntityReference reference,
			String code,
			boolean unique,
			boolean uniqueInSet,
			EntityInstance entityInstance) {
		super(
				label, 
				relationName, 
				unique, 
				uniqueInSet, 
				entityInstance);
		this.reference = reference;
	}
	
	public EntityReference getReference() {
		return reference;
	}
	public void setReference(EntityReference reference) {
		this.reference = reference;
	}
	
	public String getCode() {
		return code;
	}
	
	public ToOneAggregation makeClone() {
		return new ToOneAggregation(
				getAttributeName(),
				provideLabel(null),
				reference.makeClone(),
				code,
				getUnique(),
				getUniqueInSet(),
				getEntityInstance()
				);
	}
}
