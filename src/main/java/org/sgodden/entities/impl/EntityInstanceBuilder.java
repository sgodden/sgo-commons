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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.beanutils.PropertyUtils;
import org.sgodden.entities.Aggregation;
import org.sgodden.entities.Attribute;
import org.sgodden.entities.AttributeTypeMetadata;
import org.sgodden.entities.EntityInstance;
import org.sgodden.entities.EntityReference;
import org.sgodden.entities.IllegalEntityConfigurationException;
import org.sgodden.entities.LabelProvider;
import org.sgodden.entities.metadata.EntityMetadata;
import org.sgodden.resources.ResourceUtils;

public class EntityInstanceBuilder {
	
	//private static final transient Log log = LogFactory.getLog(EntityInstanceBuilder.class);
	
	public EntityInstance build(
			Class clazz,
			Object obj,
			Locale locale,
			Map<String, EntityMetadata> metadatas) {
		
		return build(
				null,
				clazz,
				obj,
				locale,
				metadatas);
		
	}
	
	/*
	 * FIXME - this method is too large - would a visitor pattern help to simplify this?
	 */
	public EntityInstance build(
			String path,
			Class clazz, 
			Object obj, 
			Locale locale,
			Map<String, EntityMetadata> metadatas){
		
		if (obj == null){
			return null;
		}
		
		DefaultEntityInstanceImpl instance = new DefaultEntityInstanceImpl(
				clazz.getName(),
				metadatas);
		
		instance.setPath(path);
		
		try {
		
		instance.setId(PropertyUtils.getProperty(obj, "id").toString());
		
		if ( !(metadatas.containsKey(clazz.getName())) ) {
			throw new IllegalStateException("Metadata for " + clazz.getName() + " was not found");
		}

		instance.setAllMetadatas(metadatas);
		
		ResourceBundle rb = ResourceUtils.getDefaultLiteralsResourceBundle(clazz.getName(), locale);
		
		// FIXME - all this is rubbish - use the logical primary key as the label
		if (obj instanceof LabelProvider){
			instance.setLabel(((LabelProvider)obj).provideLabel(locale));
		} else if (PropertyUtils.getPropertyDescriptor(obj, "code") != null){
			// if the object has a code, use that as the label
			instance.setLabel( (String) PropertyUtils.getProperty(obj, "code"));
		}
		
		/*
		 * Get all the get methods
		 */
		Method[] methods = clazz.getMethods();
		for (Method m : methods){
			if (m.getName().startsWith("get")
					&& !(m.getName().equals("getId"))
					&& !(m.getName().equals("getClass"))){
				
				String attributeName = EntityInstanceUtils.toAttributeName(m.getName());
				Object attributeValue = PropertyUtils.getProperty(obj, attributeName);
				Class attributeClazz = null;
				String attributeLabel = EntityInstanceUtils.getAttributeLabel(m, rb);
				
				if (m.getAnnotation(Column.class) != null
						|| m.getAnnotation(ManyToOne.class) != null
						|| m.getAnnotation(OneToOne.class) != null){
					attributeClazz = m.getReturnType();
					
				} else {
					if (m.getAnnotation(ManyToMany.class) != null){
						ManyToMany ann = m.getAnnotation(ManyToMany.class);
						attributeClazz = ann.targetEntity();
					} else if (m.getAnnotation(OneToMany.class) != null){
						OneToMany ann = m.getAnnotation(OneToMany.class);
						attributeClazz = ann.targetEntity();
					}
					if (attributeClazz == null || attributeClazz == void.class){
						throw new IllegalEntityConfigurationException("Target class must be specified for a to-many child relationship: attributeName=" + attributeName);
					}
				}
				
				if (m.getAnnotation(Column.class) != null){

					// it's a plain column = attribute
					// if it has an Attribute annotation, then get the metadata
					AttributeTypeMetadata md = null;
					boolean unique = false;
					boolean uniqueInSet = false;
					
					if (m.getAnnotation(Attribute.class) != null) {
						Attribute attr = m.getAnnotation(Attribute.class);
						md = AttributeTypeMetadata.get(attr.attributeType());
						unique = attr.unique();
						uniqueInSet = attr.uniqueInComposedSet();
					}
					
					instance.getSimpleInstanceAttributesHolder().setValue(
							attributeName, 
							attributeLabel,
							attributeValue,
							m.getReturnType(),
							unique,
							uniqueInSet,
							instance);
				} 
				else if (m.getAnnotation(ManyToOne.class) != null){
					// TODO - handling many to one reverse mapped compositions
					// assume it's an aggregation for now
					boolean unique = false;
					boolean uniqueInSet = false;
					
					if (m.getAnnotation(Aggregation.class) != null){
						Aggregation agg = m.getAnnotation(Aggregation.class);
						unique = agg.unique();
						uniqueInSet = agg.uniqueInComposedSet();
					}
					
					instance.getToOneAggregationsHolder().setEntityReference(
							attributeName, 
							attributeLabel,
							makeEntityReference(attributeClazz, attributeValue),
							getCodeFromObject(attributeValue),
							unique,
							uniqueInSet,
							instance);
					
				} else if (m.getAnnotation(ManyToMany.class) != null){
					// must be an aggregation
					setToManyAggregations(
							attributeClazz, 
							instance, 
							attributeName, 
							attributeValue, 
							metadatas.get(attributeName));
				} 
				else if (m.getAnnotation(OneToMany.class) != null){ // one to many
					
					OneToMany ann = m.getAnnotation(OneToMany.class);
					
					if (EntityInstanceUtils.isAggregation(obj.getClass(), attributeName, ann.cascade())){
						setToManyAggregations(
								attributeClazz, 
								instance, 
								attributeName, 
								attributeValue,
								metadatas.get(attributeName));
					} 
					else {
						// it's a composition
						Set childObjects = (Set)attributeValue;
						
						if (childObjects != null) {
							
							String newPath = getAttributePath(path, attributeName);
							
							Set<EntityInstance> children = new HashSet<EntityInstance>();
							
							Iterator it = childObjects.iterator();
							int index = 0;
							
							while(it.hasNext()){
								children.add(
										build(
												newPath + "[" + index++ + "]", 
												attributeClazz, 
												it.next(), 
												locale, 
												metadatas));
							}
							
							instance.getToManyChildrenHolder().setInstances(
									attributeName, 
									attributeLabel, 
									(EntityInstance[])children.toArray(new EntityInstance[children.size()]),
									metadatas.get(attributeClazz.getName())
									);
							
						}
					}
					
				} else if (m.getAnnotation(OneToOne.class) != null){
					
					OneToOne ann = m.getAnnotation(OneToOne.class);
					
					boolean unique = false;
					boolean uniqueInSet = false;
					
					if (m.getAnnotation(Aggregation.class) != null){
						Aggregation agg = m.getAnnotation(Aggregation.class);
						unique = agg.unique();
						uniqueInSet = agg.uniqueInComposedSet();
					}
					
					if (EntityInstanceUtils.isAggregation(obj.getClass(), attributeName, ann.cascade())){
						instance.getToOneAggregationsHolder().setEntityReference(
								attributeName, 
								attributeLabel, 
								makeEntityReference(attributeClazz, attributeValue),
								getCodeFromObject(attributeValue),
								unique,
								uniqueInSet,
								instance);
					} else {
						String newPath = getAttributePath(path, attributeName);
						
						instance.getToOneChildrenHolder().setEntityInstance(
								attributeName, 
								attributeLabel, build(newPath, attributeClazz, attributeValue, locale, metadatas)
								);
					}
				}
			}
		}
		
		
		} catch(Exception e){
			throw new Error("Error constructing entity instance",e);
		}
		
		return instance;
	}

	/**
	 * Returns a new attribute path based on the path for the existing
	 * object, plus the specified attribute.
	 * 
	 * @param currentPath the path of the current object.
	 * @param attributeName the name of the attribute to append to the path.
	 * @return the new fully qualified path for the attribute.
	 */
	private String getAttributePath(String currentObjectPath, String attributeName) {

		StringBuffer sb = new StringBuffer();
		if (currentObjectPath != null) {
			sb.append(currentObjectPath);
		}
		
		if (sb.length() > 0) {
			sb.append('.');
		}
		
		sb.append(attributeName);
		
		return sb.toString();
		
	}
	
	private void setToManyAggregations(
			Class referencedClazz, 
			DefaultEntityInstanceImpl instance, 
			String attributeName, 
			Object attributeValue,
			EntityMetadata metadata) throws Exception {
		
		Set<EntityReference> refs = new HashSet<EntityReference>();
		Set s = (Set)attributeValue;
		
		if (s != null) {
		
			Iterator it = s.iterator();
			while (it.hasNext()){
				refs.add(makeEntityReference(referencedClazz, it.next()));
			}
			instance.getToManyAggregationsHolder().setEntityReferences(
					attributeName, 
					(EntityReference[])refs.toArray(new EntityReference[refs.size()]),
					metadata
					);
		
		}
	}
	
	private EntityReference makeEntityReference(Class referencedClazz, Object referenced) throws Exception {
		EntityReference ret = new EntityReference();
		ret.setEntityClassName(referencedClazz.getName());
		if (referenced != null){
			ret.setId( ((Serializable) PropertyUtils.getProperty(referenced, "id")).toString() );			
		}
		return ret;
	}
	
	private String getCodeFromObject(Object object) {
		String ret = null;
		if (object != null) {
			try {
				ret = (String) PropertyUtils.getProperty(object, "code");
			} catch (Exception e) {
				throw new Error(e);
			} 
		}
		return ret;
	}

}
