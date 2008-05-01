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

public class ToManyAggregations 
	implements Serializable{
	
	private Map<String, ToManyAggregation> _aggs = new HashMap<String, ToManyAggregation>();
	
	public void setAll(ToManyAggregation[] aggs){
		_aggs = new HashMap<String, ToManyAggregation>();
		for (ToManyAggregation agg : aggs){
			_aggs.put(agg.getRelationName(), agg);
		}
	}
	
	public ToManyAggregation[] getAll(){
		ToManyAggregation[] ret = (ToManyAggregation[]) _aggs.values().toArray(new ToManyAggregation[_aggs.size()]); 
		
		return ret;
	}
	
	public ToManyAggregation get(String relationName) {
		return _aggs.get(relationName);
	}
	
	public EntityReference[] getEntityReferences(String relationName){
		return _aggs.get(relationName).getReferences();
	}
	
	public void setEntityReferences(String relationName, EntityReference[] references, EntityMetadata metadata){
		ToManyAggregation agg = new ToManyAggregation(relationName, references, metadata);
		_aggs.put(relationName, agg);
	}
	
	public ToManyAggregations makeClone() {
		ToManyAggregations ret = new ToManyAggregations();
		
		for (String key : _aggs.keySet()) {
			ret._aggs.put(key, _aggs.get(key).makeClone());
		}
		
		return ret;
	}

}
