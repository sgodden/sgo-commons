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
package org.sgodden.entities.service;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.sgodden.entities.EntityInstance;
import org.sgodden.entities.EntityInstancePk;
import org.sgodden.entities.impl.DefaultEntityInstanceImpl;
import org.sgodden.entities.impl.EntityInstanceBuilder;
import org.sgodden.entities.impl.EntityInstanceMerger;
import org.sgodden.entities.impl.EntityInstanceUtils;
import org.sgodden.entities.impl.EntityMetadataBuilder;
import org.sgodden.entities.metadata.EntityMetadata;

/**
 * Default object maintainer service implementation, which uses Hibernate as the persistence
 * mechanism.
 * @author sgodden
 *
 */
public class ObjectMaintainerServiceImpl implements ObjectMaintainerService {
	
	private static final transient Log log = LogFactory.getLog(ObjectMaintainerServiceImpl.class);
	
	private SessionFactory sessionFactory;

	/**
	 * Sets the hibernate session factory.
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.entities.service.ObjectBuilderService#getEntityInstance(org.sgodden.entities.EntityInstancePk, java.util.Locale)
	 */
	public EntityInstance getEntityInstance(EntityInstancePk pk, Locale locale) {
		log.debug("Retrieving entity instance for key " + pk + ", locale " + locale);
		
		EntityInstance ret = null;

		Class clazz = EntityInstanceUtils.getEntityClass(pk);
		
		Object obj = EntityInstanceUtils.getPersistentObject(pk, sessionFactory);

		Map<String, EntityMetadata> metadatas = new EntityMetadataBuilder().createMetadatas(pk.getClassname(), locale);
		
		ret = new EntityInstanceBuilder().build( clazz, obj, locale, metadatas );
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.entities.service.ObjectBuilderService#maintainEntityInstance(org.sgodden.entities.EntityInstance)
	 */
	public void maintainEntityInstance(EntityInstance instance) {
		new EntityInstanceMerger().merge(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.entities.service.ObjectBuilderService#createEntityInstance(java.lang.String, java.util.Locale)
	 */
	public EntityInstance createEntityInstance(String className, Locale locale) {
		log.debug("Creating entity instance for class " + className);

		Class clazz;
		try {
			clazz = getClass().getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new Error("Error creating entity instance", e);
		}

		Map<String, EntityMetadata> metadatas = new EntityMetadataBuilder().createMetadatas(clazz.getName(), locale);
		
		DefaultEntityInstanceImpl ret = new DefaultEntityInstanceImpl(
				className,
				metadatas);
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.entities.service.ObjectBuilderService#deleteEntityInstance(org.sgodden.entities.EntityInstancePk)
	 */
	public void deleteEntityInstance(EntityInstancePk instancePk) {
		EntityInstanceUtils.deleteEntityInstance(instancePk, sessionFactory);
	}

}
