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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.sgodden.entities.EntityInstance;
import org.sgodden.entities.EntityInstancePk;
import org.sgodden.entities.EntityReference;
import org.sgodden.entities.SimpleInstanceAttribute;
import org.sgodden.entities.ToManyAggregation;
import org.sgodden.entities.ToManyChildEntry;
import org.sgodden.entities.ToOneAggregation;
import org.sgodden.entities.ToOneChildEntry;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.thoughtworks.xstream.XStream;

/**
 * Takes an instance of {@link EntityInstance} and merges it back into the
 * corresponding persistent object instance, or creates one.
 * 
 * @author goddens
 * 
 */
public class EntityInstanceMerger {
	
	private static final transient Log log = LogFactory.getLog(EntityInstanceMerger.class);

	SessionFactory sessionFactory;
	
	/**
	 * Sets the hibernate session factory.
	 * @param sessionFactory the hibernate session factory.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void merge(EntityInstance instance) {
		new XStream().toXML(instance);
		Object ret = createObject(instance);
		sessionFactory.getCurrentSession().merge(ret);
	}

	private Object createObject(EntityInstance instance) {
		Object ret = null;
		
		log.debug("Processing an instance of " + instance.getEntityClassName() + " with id '" + instance.getId() + "'");
		
		try {
			ret = getClass().getClassLoader().loadClass(
					instance.getEntityClassName()).newInstance();
			
			if (instance.getId() != null) {
				PropertyUtils.setProperty(ret, "id", new Long(instance.getId()));
			}

			{
				for (SimpleInstanceAttribute attr : instance.getSimpleInstanceAttributes()) {
					Object value = attr.getValue();
					if (value instanceof XMLGregorianCalendarImpl){
						value = ((XMLGregorianCalendarImpl)value).toGregorianCalendar();
					}
					PropertyUtils.setProperty(ret, attr.getAttributeName(),
							value);
				}
			}

			{
				for (ToOneChildEntry entry : instance.getToOneChildren()) {
					PropertyUtils.setProperty(ret, entry.getRelationName(),
							createObject(entry.getInstance()));
				}
			}

			{
				for (ToManyChildEntry entry : instance.getToManyChildren()) {
					Set s = new HashSet();
					for (EntityInstance childInstance : entry.getInstances()) {
						s.add(createObject(childInstance));
					}
					PropertyUtils.setProperty(ret, entry.getRelationName(), s);
					
					// delete any objects which are no longer in the child set
					deleteRemovedObjects(entry);
				}
			}

			{
				for (ToOneAggregation agg : instance.getToOneAggregations()) {
					if (agg.getReference().getId() != null) {
						Object o = sessionFactory.getCurrentSession().load(
								agg.getReference().getEntityClassName(),
								new Long(agg.getReference().getId()));
						PropertyUtils
								.setProperty(ret, agg.getAttributeName(), o);
					}
				}
			}

			{
				for (ToManyAggregation agg : instance.getToManyAggregations()) {
					Set s = new HashSet();
					for (EntityReference ref : agg.getReferences()) {
						if (ref.getId() != null) {
							s.add(sessionFactory.getCurrentSession().load(
									ref.getEntityClassName(), new Long(ref.getId())));
						}
					}
					PropertyUtils.setProperty(ret, agg.getRelationName(), s);
				}
			}

		} catch (Exception e) {
			throw new Error(e);
		}
		return ret;
	}
	
	private void deleteRemovedObjects(ToManyChildEntry entry) {
		for (EntityInstance removedChild : entry.getRemovedInstances()) {
			EntityInstancePk instancePk = new EntityInstancePk( removedChild.getEntityClassName(), removedChild.getId() );
			EntityInstanceUtils.deleteEntityInstance(instancePk, sessionFactory);
		}
	}

}
