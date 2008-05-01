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
import java.util.HashMap;
import java.util.Map;

import org.sgodden.entities.impl.EntityInstanceUtils;

public class SimpleInstanceAttributes 
	implements Serializable {
	
	private Map<String, SimpleInstanceAttribute> attributes = new HashMap<String, SimpleInstanceAttribute>();
	
	public Object getValue(String attributeName){
		return attributes.get(attributeName).getValue();
	}
	
	public DataType getDataType(String attributeName) {
		return attributes.get(attributeName).getMetadata().getDataType();
	}
	
	public SimpleInstanceAttribute get(String attributeName) {
		return attributes.get(attributeName);
	}
	
	public void setValue(
			String attributeName,
			String attributeLabel,
			Object value, 
			Class clazz,
			boolean unique,
			boolean uniqueInSet,
			EntityInstance entityInstance){
		
		DataType dataType = EntityInstanceUtils.getDataType(clazz);
		
		attributes.put(attributeName, 
				new SimpleInstanceAttribute(
						attributeLabel,
						attributeName,
						value,
						unique,
						uniqueInSet,
						entityInstance
						));
	}
	
	public void setValue(
			String attributeName,
			String attributeLabel,
			Object value, 
			boolean unique,
			boolean uniqueInSet,
			EntityInstance entityInstance){
		
		attributes.put(attributeName, 
				new SimpleInstanceAttribute(
						attributeLabel,
						attributeName,
						value,
						unique,
						uniqueInSet,
						entityInstance));
	}
	
	//FIXME - sort the attributes before returning them
	public SimpleInstanceAttribute[] getAllAttributes(){
		return (SimpleInstanceAttribute[]) attributes.values().toArray(new SimpleInstanceAttribute[attributes.values().size()]);
	}
	
	public SimpleInstanceAttributes makeClone() {
		SimpleInstanceAttributes ret = new SimpleInstanceAttributes();
		
		Map<String, SimpleInstanceAttribute> newAttributes = new HashMap<String, SimpleInstanceAttribute>();
		ret.attributes = newAttributes;
		
		for (String key : attributes.keySet()) {
			newAttributes.put( key, attributes.get(key).makeClone() );
		}
		
		return ret;
	}

}
