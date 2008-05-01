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
package org.sgodden.entities.impl;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.sgodden.entities.DataType;
import org.sgodden.entities.EntityInstance;
import org.sgodden.entities.EntityInstancePk;
import org.sgodden.entities.SimpleInstanceAttribute;
import org.sgodden.entities.ToManyChildEntry;

public class EntityInstanceUtils {

	private static final transient Log log = LogFactory
			.getLog(EntityInstanceUtils.class);

	/**
	 * Turns a method name (for example 'getName') into an attribute name (for
	 * example 'name').
	 * 
	 * @param methodName
	 * @return
	 */
	public static String toAttributeName(String methodName) {
		String temp = methodName.substring(3, methodName.length());
		return temp.substring(0, 1).toLowerCase()
				+ temp.substring(1, temp.length());
	}

	/**
	 * Returns whether the passed array of cascade types indicates an
	 * aggregation relationship.
	 * 
	 * @param cascadeTypes
	 * @return true if the relationship is an aggregation, false if it is a
	 *         composition.
	 */
	public static boolean isAggregation(Class clazz, String attributeName,
			CascadeType[] cascadeTypes) {
		boolean ret = false;

		if (cascadeTypes.length == 1 && cascadeTypes[0] == CascadeType.ALL) {
			ret = false;
		} else if (cascadeTypes.length == 0) {
			ret = true;
		} else {
			throw new Error("Cannot determine whether attribute "
					+ attributeName + " for class " + clazz
					+ " is an aggregation or composition");
		}

		return ret;
	}

	public static Object getAttribute(EntityInstance instance, String path) {

		log.debug("Retrieving attribute for path: " + path + ", Length "
				+ path.length());

		Object ret = null;

		// get the object represented by the first part of the path, and
		// re-invoke with that object
		// and the remaining path

		if (path.length() == 0) { // the root
			return instance;
		}

		String thisPart;

		if (path.indexOf(".") > -1) {
			thisPart = path.substring(0, path.indexOf("."));
		} else {
			thisPart = path;
		}

		// is there an index in this part (for going deeper)?
		int index = -1;
		if (thisPart.indexOf('[') > -1) {
			int leftBracket = thisPart.indexOf('[');
			int rightBracket = thisPart.indexOf(']');
			String intString = thisPart.substring(
					leftBracket + 1,
					rightBracket);
			log.debug("Index string is: " + intString);
			index = new Integer(intString).intValue();

			// and now chop off the index from the name
			thisPart = thisPart.substring(0, thisPart.indexOf('['));
		}

		String remainingPart;

		if (path.indexOf(".") > -1) {
			remainingPart = path.substring(path.indexOf(".") + 1, path
					.length());
		} else {
			remainingPart = "";
		}

		log.debug("Looking for attribute with name " + thisPart);
		if (index > -1) {
			log.debug("Index is: " + index);
		}
		log.debug("Remaining part is " + remainingPart);

		// is it an instance attribute? if so, we'll just return it
		for (SimpleInstanceAttribute attr : instance.getSimpleInstanceAttributes()) {
			if (attr.getAttributeName().equals(thisPart)) {
				ret = attr;
			}
		}

		if (ret == null) {
			// is it a child set?
			ToManyChildEntry entry = instance.getToManyChildEntry(thisPart);
			if (entry != null) {

				if (index < 0 && remainingPart.length() > 0) {
					throw new IllegalArgumentException(
							"Index must be provided to navigate through a to-many child entry");
				}

				if (index > -1) {
					ret = entry.getInstances()[index];
				} else {
					ret = entry;
				}

				// If there is more path to go, then re-invoke this method
				// with the instance
				// and the remaining part.
				if (remainingPart.length() > 0) {
					ret = getAttribute((EntityInstance) ret, remainingPart);
				}

			}
		}

		if (ret == null) {
			throw new IllegalArgumentException(
					"Could not find element for partial path '" + thisPart
							+ "', full path '" + path + "'");
		}

		log.debug("Returning " + ret.getClass());

		return ret;
	}
	
	
	public static boolean isRoot(String editPath) {
		
		if (editPath.length() == 0){
			return true;
		} else {
			return false;
		}
		
	}
	
	
	private static String getAttributeLabel(Method m, ResourceBundle rb, String attributeName){
		String ret = null;
		
		if (m.getAnnotation(Column.class) != null
				|| m.getAnnotation(OneToOne.class) != null
				|| m.getAnnotation(ManyToOne.class) != null
				) {
			ret = rb.getString(attributeName);
		} else {
			// must be plural
			ret = rb.getString(attributeName + ".plural");
		}
		
		return ret;
	}
	
	public static String getAttributeLabel(Method m, ResourceBundle rb) {
		return getAttributeLabel(m, rb, EntityInstanceUtils.toAttributeName(m.getName()));
	}

	public static DataType getDataType(Class clazz) {
		
		DataType ret;
		
		if (clazz == String.class){
			ret = DataType.STRING;			
		} else if (clazz == Integer.class){
			ret = DataType.INTEGER;			
		} else if (Calendar.class.isAssignableFrom(clazz)){
			ret = DataType.TIMESTAMP;			
		} else if (clazz == Long.class){
			ret = DataType.LONG;			
		} else if (clazz == Boolean.class){
			ret = DataType.BOOLEAN;			
		} else {
			throw new IllegalArgumentException("Unknown class for data type mapping: " + clazz);
		}
		
		return ret;

	}
	
	public static ToManyChildEntry getToManyChildEntry(EntityInstance instance, String path) {
		ToManyChildEntry ret = null;
		
		// strip any index suffix from the path
		path = PathUtils.stripIndexSuffix(path);
		
		Object o = getAttribute(instance, path);
		
		if (!(o instanceof ToManyChildEntry)) {
			throw new IllegalArgumentException("The path does not point to a to-many child entry: " + path);
		} else {
			ret = (ToManyChildEntry) o;
		}
		
		return ret;
	}

	public static void deleteEntityInstance(EntityInstancePk instancePk, SessionFactory sessionFactory) {
		Object obj = getPersistentObject(instancePk, sessionFactory);
		if (obj == null){
			throw new IllegalArgumentException("The passed entity instance does not represent a persistent object");
		} else {
			sessionFactory.getCurrentSession().delete(obj);
		}
	}
	
	public static Object getPersistentObject(EntityInstancePk pk, SessionFactory sessionFactory) {
		Object ret = null;
		
		Class clazz = getEntityClass(pk);
		
		sessionFactory.getCurrentSession().load( clazz, new Long( pk.getId() ) );
		
		return ret;
	}
	
	public static Class getEntityClass(EntityInstancePk pk) {
		
		Class clazz;

		try {
			clazz = EntityInstanceUtils.class.getClassLoader().loadClass(pk.getClassname());
		} catch (ClassNotFoundException e) {
			throw new Error("Error retrieving entity instance", e);
		}
		
		return clazz;
		
	}

}
