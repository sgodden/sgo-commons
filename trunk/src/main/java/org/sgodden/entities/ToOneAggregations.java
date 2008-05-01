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

public class ToOneAggregations 
	implements Serializable {

	Map<String, ToOneAggregation> _aggs = new HashMap<String, ToOneAggregation>();
	
	public ToOneAggregation get(String relationName) {
		return _aggs.get(relationName);
	}
	
	public ToOneAggregation[] getAll(){
		return (ToOneAggregation[]) _aggs.values().toArray(new ToOneAggregation[_aggs.values().size()]);
	}
	
	public EntityReference getEntityReference(String relationName){
		return _aggs.get(relationName).getReference();
	}
	
	public void setEntityReference(
			String relationName, 
			String relationLabel, 
			EntityReference reference,
			String code,
			boolean unique,
			boolean uniqueInSet,
			EntityInstance entityInstance){
		_aggs.put(relationName, new ToOneAggregation(
				relationName, 
				relationLabel, 
				reference,
				code,
				unique,
				uniqueInSet,
				entityInstance));
	}
	
	public ToOneAggregations makeClone() {
		ToOneAggregations ret = new ToOneAggregations();
		
		for(String key : _aggs.keySet()) {
			ret._aggs.put(key, _aggs.get(key).makeClone());
		}
		
		return ret;
	}
}
