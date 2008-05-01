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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the attribute type that defines the metadata (maximum length
 * etc) for an attribute on a particular entity.
 * <p>
 * This allows for central definitions of lengths of particular attribute types
 * according to the customer's preference - for example, a certain customer might
 * want order numbers to be 35, whilst another might want them to be only 10.
 * Specifying this data on each entity is unnecessary and difficult to maintain.
 * 
 * @author goddens
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Attribute {
	/**
	 * The attribute type.
	 * @return
	 */
	String attributeTypeName();
	/**
	 * Whether the value of this attribute needs to be unique across
	 * all instances of the type.
	 * <p/>
	 * This might be used to infer indexes to be added to the database.
	 * <p/>
	 * N.B. - this might need enhancing, to allow identification of
	 * the primary unique constraint visible to the user, and perhaps
	 * also to allow composite unique constraints by supplying a named
	 * constraint set.
	 * @return
	 */
	boolean unique() default false;
	
	/**
	 * Whether the value of this attribute needs to be unique across
	 * instances in a particular composed set.
	 * <p/>
	 * For instance, the order line number needs to be unique within
	 * the set composed into an order.
	 * 
	 * @return
	 */
	boolean uniqueInComposedSet() default false;
}
