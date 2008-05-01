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

import org.sgodden.entities.metadata.EntityMetadata;

public class ToManyChildren 
		implements Serializable {
	
	Map<String, ToManyChildEntry> children = new HashMap<String, ToManyChildEntry>();
	
	Map<String, EntityMetadata> allMetadatas;
	
	private String parentPath;
	
	/**
	 * Private constructor for the purpose of making clones.
	 */
	private ToManyChildren() {	}
	
	public ToManyChildren(
			Map<String, EntityMetadata> allMetadatas,
			String parentPath) {
		this.allMetadatas = allMetadatas;
		this.parentPath = parentPath;
	}
	
	public ToManyChildEntry get(String relationName) {
		return children.get(relationName);
	}
	
	public ToManyChildEntry[] getAllChildren(){
		return (ToManyChildEntry[])children.values().toArray(new ToManyChildEntry[children.values().size()]);
	}
	
	public EntityInstance[] getInstances(String relationName){
		return children.get(relationName).getInstances();
	}
	
	public void setInstances(String relationName, String label, EntityInstance[] instances, EntityMetadata metadata){
		ToManyChildEntry entry = new ToManyChildEntry(
				relationName, 
				label, 
				instances, 
				metadata, 
				allMetadatas,
				parentPath);
		children.put(relationName, entry);
	}
	
	public ToManyChildren makeClone() {
		ToManyChildren ret = new ToManyChildren();
		
		for (String s : children.keySet()) {
			ret.children.put(s, children.get(s).makeClone());
		}
		
		return ret;
	}

}
