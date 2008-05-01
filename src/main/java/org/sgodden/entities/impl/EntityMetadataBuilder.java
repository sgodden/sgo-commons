package org.sgodden.entities.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.entities.Aggregation;
import org.sgodden.entities.Attribute;
import org.sgodden.entities.AttributeTypeMetadataProvider;
import org.sgodden.entities.LocaleDependent;
import org.sgodden.entities.metadata.EntityMetadata;
import org.sgodden.entities.metadata.PropertyMetadata;
import org.sgodden.entities.metadata.SimpleInstanceAttributeMetadata;
import org.sgodden.entities.metadata.ToManyAggregationMetadata;
import org.sgodden.entities.metadata.ToManyCompositionMetadata;
import org.sgodden.entities.metadata.ToOneAggregationMetadata;
import org.sgodden.entities.metadata.ToOneCompositionMetadata;
import org.sgodden.resources.ResourceUtils;

/**
 * Builds metadata related to entity classes.
 * <p/>
 * This class is not thread-safe.
 * <p/>
 * FIXME - handle one-to-one aggregations and one-to-many aggregations (yes they can exist).
 * <p/>
 * FIXME - this is broken due to infinite recursion possibilities.
 * <p/>
 * TODO - this is inefficient as it does not cache the metadatas it has created already, but is it a problem?
 * 
 * @author goddens
 *
 */
public class EntityMetadataBuilder {
	
	private static final transient Log log = LogFactory.getLog(EntityMetadataBuilder.class);
	
	private Map<String, EntityMetadata> metadatas = new HashMap<String, EntityMetadata>();
	
	private AttributeTypeMetadataProvider attributeTypeMetadataProvider;
	
	public Map<String, EntityMetadata> createMetadatas(String className, Locale locale) {
		createMetadata(className, locale);
		return metadatas;
	}
	
	private void createMetadata(String className, Locale locale){
		
		EntityMetadata ret;
		
		if (metadatas.containsKey(className)) {
			ret = metadatas.get(className);
		} else {
			
			log.debug("Creating metadata for class '" + className + "'");
			
			ret = new EntityMetadata();
			metadatas.put(className, ret);
			try {
				Class clazz = getClass().getClassLoader().loadClass(className);
				ret.setClassName(className);
				
				if ( clazz.getAnnotation(LocaleDependent.class) != null ) {
					ret.setLocaleDependent(true);
				}
				
				Method[] methods = clazz.getMethods();
				addSimpleProperties(ret, methods, locale);
				addManyToOneAggregations(ret, methods, locale);
				addManyToManyAggregations(ret, methods, locale);
				addToOneCompositions(ret, methods, locale);
				addToManyCompositions(ret, methods, locale);
				
			}
			catch(Exception e) {
				throw new Error("Error constructing entity metadata for class " + className, e);
			}
			
		}
		
	}
	
	private void addSimpleProperties(EntityMetadata md, Method[] methods, Locale locale) {
		
		Set<PropertyMetadata> pmds = new HashSet<PropertyMetadata>();
		
		for (Method m : methods) {
			
			Attribute attr = m.getAnnotation(Attribute.class);
			
			if (attr != null){
				
				SimpleInstanceAttributeMetadata siam = new SimpleInstanceAttributeMetadata(
						EntityInstanceUtils.getAttributeLabel(
								m, 
								ResourceUtils.getDefaultLiteralsResourceBundle(md.getClassName(), locale)),  
						EntityInstanceUtils.toAttributeName(m.getName()), 
						attr.unique(), 
						attr.uniqueInComposedSet(), 
						EntityInstanceUtils.getDataType(m.getReturnType()),
						attributeTypeMetadataProvider.getMetadata(attr.attributeTypeName()));
				
				pmds.add(siam);
				
			}
		}
		
		md.setSimpleInstanceAttributeMetadatas((SimpleInstanceAttributeMetadata[]) pmds.toArray(new SimpleInstanceAttributeMetadata[pmds.size()]));
	}

	private void addManyToOneAggregations(EntityMetadata md, Method[] methods, Locale locale) {
		
		Set<ToOneAggregationMetadata> toams = new HashSet<ToOneAggregationMetadata>();
		
		for (Method m : methods) {
			
			ManyToOne mto = m.getAnnotation(ManyToOne.class);
			
			boolean unique = false;
			boolean uniqueInSet = false;
			
			Aggregation agg = m.getAnnotation(Aggregation.class);
			
			if (agg != null) {
				unique = agg.unique();
				uniqueInSet = agg.uniqueInComposedSet();
			}
			
			if ( mto != null ) {
				
				String aggregatedClassName = m.getReturnType().getName();
				
				ToOneAggregationMetadata toam = new ToOneAggregationMetadata(
						EntityInstanceUtils.toAttributeName(m.getName()), 
						EntityInstanceUtils.getAttributeLabel(
								m, 
								ResourceUtils.getDefaultLiteralsResourceBundle(md.getClassName(), locale)),  
						aggregatedClassName, 
						unique, 
						uniqueInSet);
				
				createMetadata(aggregatedClassName, locale);
				
				toams.add(toam);
			}
			
		}
		
		md.setToOneAggregationMetadatas( (ToOneAggregationMetadata[]) toams.toArray(new ToOneAggregationMetadata[toams.size()]) );
		
	}
	
	private void addManyToManyAggregations(EntityMetadata md, Method[] methods, Locale locale) {
		
		Set<ToManyAggregationMetadata> tmams = new HashSet<ToManyAggregationMetadata>();
		
		for (Method m : methods) {
			
			ManyToMany manyToMany = m.getAnnotation(ManyToMany.class);
			
			if ( manyToMany != null ) {
				
				if (manyToMany.targetEntity() == null) {
					throw new IllegalArgumentException("The target entity must be specified on ManyToMany annotations");
				}

				ToManyAggregationMetadata tmam = new ToManyAggregationMetadata(
						EntityInstanceUtils.getAttributeLabel(
								m, 
								ResourceUtils.getDefaultLiteralsResourceBundle(md.getClassName(), locale)),  
								EntityInstanceUtils.toAttributeName(m.getName()), 
						manyToMany.targetEntity().getName() 
						);
				
				createMetadata(manyToMany.targetEntity().getName(), locale);
				
				tmams.add(tmam);
			}
			
		}
		
		md.setToManyAggregationMetadatas( (ToManyAggregationMetadata[]) tmams.toArray(new ToManyAggregationMetadata[tmams.size()])  );
		
	}
	
	private void addToOneCompositions(EntityMetadata md, Method[] methods, Locale locale) {
		
		Set<ToOneCompositionMetadata> tocms = new HashSet<ToOneCompositionMetadata>();
		
		for (Method m : methods) {
			
			OneToOne oneToOne = m.getAnnotation(OneToOne.class);
			
			if (oneToOne != null 
					&& oneToOne.cascade().length == 1
					&& oneToOne.cascade()[0] == CascadeType.ALL) {
				
				createMetadata(m.getReturnType().getName(), locale);
				
				ToOneCompositionMetadata tocm = new ToOneCompositionMetadata(
						EntityInstanceUtils.getAttributeLabel(
								m, 
								ResourceUtils.getDefaultLiteralsResourceBundle(md.getClassName(), locale)),  
						EntityInstanceUtils.toAttributeName(m.getName()), 
						m.getReturnType().getName(),
						metadatas.get(m.getReturnType().getName())
						);
				
				createMetadata(m.getReturnType().getName(), locale);
				
				tocms.add(tocm);
			}
		}
		
		md.setToOneCompositionMetadatas( (ToOneCompositionMetadata[]) tocms.toArray(new ToOneCompositionMetadata[tocms.size()]) );
	}
	
	private void addToManyCompositions(EntityMetadata md, Method[] methods, Locale locale) {
		
		Set<ToManyCompositionMetadata> tmcms = new HashSet<ToManyCompositionMetadata>();
		
		for (Method m : methods) {
			
			OneToMany oneToMany = m.getAnnotation(OneToMany.class);
			
			if (oneToMany != null 
					&& oneToMany.cascade().length == 1
					&& oneToMany.cascade()[0] == CascadeType.ALL) {
				
				if (oneToMany.targetEntity() == null) {
					throw new IllegalArgumentException("The target entity must be specified on OneToMany annotations");
				}
				
				ToManyCompositionMetadata tmcm = new ToManyCompositionMetadata(
						EntityInstanceUtils.getAttributeLabel(
								m, 
								ResourceUtils.getDefaultLiteralsResourceBundle(md.getClassName(), locale)),  
						EntityInstanceUtils.toAttributeName(m.getName()), 
						oneToMany.targetEntity().getName()
						);
				
				createMetadata(oneToMany.targetEntity().getName(), locale);
				
				tmcms.add(tmcm);
				
			}
			
			md.setToManyCompositionMetadatas( (ToManyCompositionMetadata[]) tmcms.toArray(new ToManyCompositionMetadata[tmcms.size()]) );
			
		}
		
	}
	
	/**
	 * Sets the provider that will return attribute type metadata.
	 * @param attributeTypeMetadataProvider the provider of attribute type metadata.
	 */
	public void setAttributeTypeMetadataProvider(
			AttributeTypeMetadataProvider attributeTypeMetadataProvider) {
		this.attributeTypeMetadataProvider = attributeTypeMetadataProvider;
	}
	
}
