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

import org.sgodden.entities.impl.DefaultEntityInstanceImpl;
import org.sgodden.entities.metadata.SimpleInstanceAttributeMetadata;

public class SimpleInstanceAttribute 
		extends InstanceAttribute
		implements Cloneable {

	private Object value;
	
	public SimpleInstanceAttribute(
			String label, 
			String attributeName, 
			Object value, 
			boolean unique,
			boolean uniqueInSet,
			EntityInstance entityInstance
			) {
		super(
				label, 
				attributeName, 
				unique, 
				uniqueInSet, 
				entityInstance);
		this.value = value;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value){
		
		if (	
				( getValue() == null && value != null )
				|| ( getValue() != null && !(getValue().equals(value)) )
				) {
			((DefaultEntityInstanceImpl)getEntityInstance()).setDirty(true); // FIXME - shouldn't need the cast
		}
		
		this.value = value;
	}
	
	@Override
	public SimpleInstanceAttributeMetadata getMetadata(){
		return getEntityInstance().getMetadata().getSimpleInstanceAttributeMetadata(getAttributeName());
	}
	
	@Override
	public String toString(){
		return super.toString() + ", [value=" + value + ", dataType=" + getMetadata().getDataType() + "]";
	}
	
	public static SimpleInstanceAttribute create(
			SimpleInstanceAttributeMetadata metadata,
			EntityInstance entityInstance){
		
		SimpleInstanceAttribute ret = new SimpleInstanceAttribute(
				metadata.getLabel(),
				metadata.getAttributeName(),
				null,
				metadata.getUnique(),
				metadata.getUniqueInSet(),
				entityInstance
				);
		return ret;
	}
	
	public SimpleInstanceAttribute makeClone() {
		SimpleInstanceAttribute ret = null;
		try {
			ret = (SimpleInstanceAttribute) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return ret;
	}

}
