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
package org.sgodden.entities.metadata;

public class ToOneCompositionMetadata 
		extends PropertyMetadata {
	
	private String targetClassName;
	
	private EntityMetadata targetEntityMetadata;

	public ToOneCompositionMetadata(
			String label, 
			String attributeName, 
			String targetClassName, 
			EntityMetadata targetEntityMetadata) {
		super(label, attributeName);
		this.targetClassName = targetClassName;
		this.targetEntityMetadata = targetEntityMetadata;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	public EntityMetadata getTargetEntityMetadata() {
		return targetEntityMetadata;
	}

	public void setTargetEntityMetadata(EntityMetadata targetEntityMetadata) {
		this.targetEntityMetadata = targetEntityMetadata;
	}
}
