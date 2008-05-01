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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.entities.impl.DefaultEntityInstanceImpl;
import org.sgodden.entities.impl.PathUtils;
import org.sgodden.entities.metadata.EntityMetadata;

public class ToManyChildEntry 
		implements Serializable, LabelProvider {
	
	private static final transient Log log = LogFactory.getLog(ToManyChildEntry.class);

	private String relationName;
	private String label;
	private String parentPath;
	
	private EntityMetadata entityMetadata;
	private Map<String, EntityMetadata> allMetadatas;
	
	private List<EntityInstance> instances = new ArrayList<EntityInstance>();
	private Set<EntityInstance> removedInstances = new HashSet<EntityInstance>();

	/**
	 * Private constructor for the purpose of making clones.
	 */
	private ToManyChildEntry() { }
	
	public ToManyChildEntry(
			String relationName, 
			String label, 
			EntityInstance[] instances, 
			EntityMetadata entityMetadata,
			Map<String, EntityMetadata> allMetadatas,
			String parentPath) {
		super();
		this.relationName = relationName;
		this.label = label;
		this.entityMetadata = entityMetadata;
		this.allMetadatas = allMetadatas;
		this.parentPath = parentPath;
		
		if (instances != null) {
			this.instances.addAll(Arrays.asList(instances));
		}
	}

	public EntityInstance[] getInstances() {
		return (EntityInstance[]) instances.toArray(new EntityInstance[instances.size()]);
	}
	
	public void replaceInstance(EntityInstance oldInstance, EntityInstance newInstance) {
		for (int i = 0; i < instances.size(); i++) {
			EntityInstance current = instances.get(i);
			if (current.equals(oldInstance)) {
				log.debug("Found the old instance");
				instances.set(i, newInstance);
				break;
			}
		}
	}

	public void setInstances(EntityInstance[] instances) {
		this.instances = new ArrayList<EntityInstance>();
		this.instances.addAll(Arrays.asList(instances));
	}
	
	public void removeInstance(EntityInstance instance) {
		instances.remove(instance);
		
		/*
		 * If the instances has an id, then it was persistent,
		 * so we need to record that it was removed to 
		 * to ensure it is also removed from persistent storage,
		 * rather than leaving an orphan in the database. 
		 */
		if (instance.getId() != null) {
			removedInstances.add(instance);
		}
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public String getLabel() {
		return label;
	}

	public String provideLabel(Locale locale) {
		return getLabel();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public EntityMetadata getEntityMetadata() {
		return entityMetadata;
	}

	public Set<EntityInstance> getRemovedInstances() {
		return removedInstances;
	}
	
	public int size() {
		return instances.size();
	}
	
	/**
	 * Adds a new child instance to the end of the set.
	 */
	public void addNew(){
		DefaultEntityInstanceImpl newChild = new DefaultEntityInstanceImpl(
				entityMetadata.getClassName(),
				allMetadatas);
		
		newChild.setPath(
				PathUtils.appendTrailingDot(parentPath) 
				+ relationName 
				+ "[" 
				+ instances.size() 
				+ "]");
		
		instances.add(newChild);
	}
	
	public ToManyChildEntry makeClone() {
		
		ToManyChildEntry ret = new ToManyChildEntry();
		
		ret.allMetadatas = this.allMetadatas;
		ret.entityMetadata = this.entityMetadata;
		ret.label = this.label;
		ret.relationName = this.relationName;
		
		for (EntityInstance instance : this.instances) {
			ret.instances.add(instance.makeClone());
		}
		
		return ret;
	}
	
}
