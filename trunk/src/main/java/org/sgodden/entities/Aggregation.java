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
 * Specifies information about an aggregation, such as its default
 * presentation sequence.
 * 
 * FIXME - uniqueness attributes are relevant only to to-one aggregations
 * 
 * @author goddens
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Aggregation {
	/**
	 * Whether the value of this aggregation needs to be unique across
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
	 * Whether the value of this aggregation needs to be unique across
	 * instances in a particular composed set.
	 * 
	 * @return
	 */
	boolean uniqueInComposedSet() default false;
}
