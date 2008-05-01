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
import java.util.Locale;

import org.sgodden.entities.metadata.InstanceAttributeMetadata;

/**
 * Base class for all instance attributes.
 * <p/>
 * FIXME - should be an interface.
 * @author goddens
 *
 */
public abstract class InstanceAttribute 
		implements Serializable, LabelProvider, Comparable<InstanceAttribute> {

	private String label;
	private String attributeName;
	private boolean unique;
	private boolean uniqueInSet;
	private int presentationSequence;
	private EntityInstance entityInstance;
	private String path;
	
	public InstanceAttribute(
			String label, 
			String attributeName,
			boolean unique,
			boolean uniqueInSet,
			EntityInstance entityInstance) {
		super();
		this.label = label;
		this.attributeName = attributeName;
		this.unique = unique;
		this.uniqueInSet = uniqueInSet;
		this.presentationSequence = presentationSequence;
		this.entityInstance = entityInstance;
		
		StringBuffer sb = new StringBuffer();
		if (entityInstance.getPath() != null) {
			sb.append(entityInstance.getPath());
			sb.append('.');
		}
		sb.append(attributeName);
		path = sb.toString();

	}
	public String getAttributeName() {
		return attributeName;
	}
	public String provideLabel(Locale locale) { 
		return label; 
	}
	
	/**
	 * Returns the fully qualified path of this attribute.
	 * @return
	 */
	public String getPath() {
		return path;
	}
	
	public int compareTo(InstanceAttribute other) {
		
		int otherPresentationSequence = other.getPresentationSequence();
		
		if (getPresentationSequence() < otherPresentationSequence){
			return -1;
		} else if (getPresentationSequence() > otherPresentationSequence){
			return 1;
		}
		
		return 0; // no difference
	}

	public int getPresentationSequence() {
		return presentationSequence;
	}
	public boolean getUnique() {
		return unique;
	}
	public boolean getUniqueInSet() {
		return uniqueInSet;
	}
	public EntityInstance getEntityInstance() {
		return entityInstance;
	}
	
	public InstanceAttributeMetadata getMetadata() {
		return entityInstance.getMetadata().getInstanceAttributeMetadata(attributeName);
	}
	
	@Override
	public String toString(){
		return super.toString() + "[label=" + label + ", attributeName=" + attributeName + ", unique=" + unique + ", uniqueInSet=" + uniqueInSet + ", presentationSequence=" + presentationSequence+"]";
	}

}
