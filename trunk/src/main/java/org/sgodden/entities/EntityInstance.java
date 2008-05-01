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

import org.sgodden.entities.metadata.EntityMetadata;

/**
 * Represents an instance of an entity.
 * 
 * @author goddens
 *
 */
public interface EntityInstance 
		extends Serializable, LabelProvider {

	public String getId();
	
	public String getPath();
	
	public String getEntityClassName();
	
	public ToOneChildEntry[] getToOneChildren();
	
	public ToOneChildEntry getToOneChildEntry(String attributeName);
	
	public ToManyChildEntry[] getToManyChildren();
	
	public ToManyChildEntry getToManyChildEntry(String attributeName);
	
	public ToOneAggregation[] getToOneAggregations();
	
	public ToOneAggregation getToOneAggregation(String attributeName);
	
	public ToManyAggregation[] getToManyAggregations();
	
	public ToManyAggregation getToManyAggregation(String attributeName);

	public SimpleInstanceAttribute[] getSimpleInstanceAttributes();
	
	public SimpleInstanceAttribute getSimpleInstanceAttribute(String attributeName);
	
	/**
	 * Returns a sorted array of all instance attributes, that is, simple instance
	 * attributes, many-to-one aggregations, and any locale-dependent attributes.
	 * <p/>
	 * The required behaviour is that an instance of {@link InstanceAttribute} is returned
	 * for every simple and to-one aggregated attribute, even if the value is null.
	 * 
	 * @return
	 */
	public InstanceAttribute[] getInstanceAttributes(Locale l);
	
	/**
	 * Returns the instance attribute represented by the supplied path.
	 * <p/>
	 * The path may represent one of the following:
	 * <ul>
	 * <li>An attribute from this entity instance (i.e. not a compound path).</li>
	 * <li>A compound attribute from a to-one composition.</li>
	 * <li>A compound attribute from a locale-dependent to-many composition.</li>
	 * </ul>
	 * 
	 * @param attributePath the path of the attribute.
	 * @param locale the locale for selection of locale-dependent attributes.
	 * @return the attribute pointed to by the supplied path.
	 * @throws IllegalArgumentException if the supplied path does not satisfy the constraints listed above.
	 */
	public InstanceAttribute getInstanceAttribute(String attributePath, Locale locale); 
	
	/**
	 * Returns the metadata for this entity instance.
	 * @return the metadata.
	 */
	public EntityMetadata getMetadata();
	
	/**
	 * Returns whether this object is new (that is, does not represent a previously
	 * persisted object). 
	 * @return true if the object is new, false if not.
	 */
	public boolean isNew();
	
	/**
	 * Returns a deep clone of this entity instance.
	 * 
	 * @return the newly created clone.
	 */
	public EntityInstance makeClone();
	
	/**
	 * Returns whether changes have been made which have not been persisted.
	 * @return
	 */
	public boolean isDirty();
	
}
