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

public class ToOneChildren 
		implements Serializable {

	private Map<String, ToOneChildEntry> _children = new HashMap<String, ToOneChildEntry>();
	
	public ToOneChildEntry get(String relationName) {
		return _children.get(relationName);
	}
	
	public ToOneChildEntry[] getAllChildren(){
		return (ToOneChildEntry[]) _children.values().toArray(new ToOneChildEntry[_children.values().size()]);
	}
	
	public ToOneChildEntry getChild(String relationName){
		// TODO - create it if it is a valid child according to the parent's metadata
		return _children.get(relationName);
	}
	
	public EntityInstance getEntityInstance(String relationName){
		return _children.get(relationName).getInstance();
	}
	
	public void setEntityInstance(String relationName, String label, EntityInstance instance){
		_children.put(relationName, new ToOneChildEntry(relationName, instance, label));
	}
	
	public ToOneChildren makeClone() {
		ToOneChildren ret = new ToOneChildren();
		
		for (String s : _children.keySet()) {
			ret._children.put(s, _children.get(s).makeClone());
		}
		
		return ret;
	}
	
}
