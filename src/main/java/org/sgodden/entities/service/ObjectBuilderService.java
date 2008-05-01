package org.sgodden.entities.service;

import java.util.Locale;

import org.sgodden.entities.EntityInstance;
import org.sgodden.entities.EntityInstancePk;

public interface ObjectBuilderService {
	
	public EntityInstance getEntityInstance(EntityInstancePk primaryKey, Locale locale);
	
	public EntityInstance createEntityInstance(String className, Locale locale);
	
	public void maintainEntityInstance(EntityInstance instance);
	
	public void deleteEntityInstance(EntityInstancePk primaryKey);

}
