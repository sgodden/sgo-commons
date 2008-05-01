package org.sgodden.entities.service;

import java.util.Locale;

import org.sgodden.entities.EntityInstance;
import org.sgodden.entities.EntityInstancePk;

/**
 * A service which maintains entity instances.
 * 
 * @author sgodden
 *
 */
public interface ObjectMaintainerService {
	
	/**
	 * Returns the entity instance for the specified primary key.
	 * <p/>
	 * TODO - determine behaviour when no instance exists for the PK.
	 * 
	 * @param primaryKey the primary key of the object.
	 * @param locale the locale which should be used to create metadata descriptions (field labels etc.).
	 * @return the entity instance.
	 */
	public EntityInstance getEntityInstance(EntityInstancePk primaryKey, Locale locale);
	
	/**
	 * Returns a new entity instance for the specified class name.
	 * @param className the class for which a new entity instance should be created.
	 * @param locale the locale which should be used to create metadata descriptions (field labels etc.).
	 * @return the newly created entity instance.
	 */
	public EntityInstance createEntityInstance(String className, Locale locale);
	
	/**
	 * Saves or updates the passed entity instance to persistent storage.
	 * @param instance the instance to store.
	 */
	public void maintainEntityInstance(EntityInstance instance);
	
	/**
	 * Deletes the object having the passed primary key from persistent storage.
	 * @param primaryKey the primary key of the object to delete.
	 */
	public void deleteEntityInstance(EntityInstancePk primaryKey);

}
