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

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.entities.EntityInstance;
import org.sgodden.entities.EntityReference;
import org.sgodden.entities.InstanceAttribute;
import org.sgodden.entities.SimpleInstanceAttribute;
import org.sgodden.entities.SimpleInstanceAttributes;
import org.sgodden.entities.ToManyAggregation;
import org.sgodden.entities.ToManyAggregations;
import org.sgodden.entities.ToManyChildEntry;
import org.sgodden.entities.ToManyChildren;
import org.sgodden.entities.ToOneAggregation;
import org.sgodden.entities.ToOneAggregations;
import org.sgodden.entities.ToOneChildEntry;
import org.sgodden.entities.ToOneChildren;
import org.sgodden.entities.metadata.EntityMetadata;
import org.sgodden.entities.metadata.PropertyMetadata;
import org.sgodden.entities.metadata.SimpleInstanceAttributeMetadata;
import org.sgodden.entities.metadata.ToManyAggregationMetadata;
import org.sgodden.entities.metadata.ToManyCompositionMetadata;
import org.sgodden.entities.metadata.ToOneAggregationMetadata;
import org.sgodden.entities.metadata.ToOneCompositionMetadata;
import org.sgodden.locale.util.LocaleUtils;

import com.thoughtworks.xstream.XStream;

/**
 * FIXME - provide consistent initialisation for all relationship types (creating new objects still does not work).
 * <p/>
 * FIXME - we need a distinction between simple get and get-or-create paradigms when retrieving compound attributes.
 * <p/>
 * FIXME - the public interface is a mess, leading to issues with knowing when the objects are dirty.
 * @author simon
 *
 */
public class DefaultEntityInstanceImpl 
	implements EntityInstance {
	
	private static final transient Log log = LogFactory.getLog(DefaultEntityInstanceImpl.class);
	
	private ToOneChildren toOneChildren = new ToOneChildren();
	private ToManyChildren toManyChildren;
	private ToManyAggregations toManyAggregations = new ToManyAggregations();
	private ToOneAggregations toOneAggregations = new ToOneAggregations();
	private SimpleInstanceAttributes simpleInstanceAttributes = new SimpleInstanceAttributes();
	private String id;
	private String path;
	private String entityClassName;
	private String label;
	private boolean isDirty = false;
	
	/**
	 * A map of all the metadatas that can be referred to in this
	 * object tree.
	 */
	private Map<String, EntityMetadata> allMetadatas;
	
	/**
	 * Constructs a new instance.
	 * 
	 * @param entityClassName the fully-qualified name of the entity class.
	 * @param allMetadatas the map of metadatas containing metadata for this class and all of its compositions.
	 */
	public DefaultEntityInstanceImpl(
			String entityClassName, 
			Map<String, EntityMetadata> allMetadatas
			) {
		this.entityClassName = entityClassName;
		this.allMetadatas = allMetadatas;
	}

	public void setEntityClassName(String entityClassName) {
		this.entityClassName = entityClassName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSimpleInstanceAttributes(SimpleInstanceAttributes instanceAttributes) {
		this.simpleInstanceAttributes = instanceAttributes;
	}

	public void setToManyAggregations(ToManyAggregations toManyAggregations) {
		this.toManyAggregations = toManyAggregations;
	}

	public void setToManyChildren(ToManyChildren toManyChildren) {
		this.toManyChildren = toManyChildren;
	}

	public void setToOneAggregations(ToOneAggregations toOneAggregations) {
		this.toOneAggregations = toOneAggregations;
	}

	public void setToOneChildren(ToOneChildren toOneChildren) {
		this.toOneChildren = toOneChildren;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

	public String getId() {
		return id;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

	public SimpleInstanceAttribute[] getSimpleInstanceAttributes() {
		initialiseSimpleInstanceAttributes();
		return simpleInstanceAttributes.getAllAttributes();
	}
	
	public SimpleInstanceAttribute getSimpleInstanceAttribute(String attributeName) {
		return getSimpleInstanceAttribute(attributeName, true);
	}
	
	private SimpleInstanceAttribute getSimpleInstanceAttribute(String attributeName, boolean initialise) {
		SimpleInstanceAttribute ret = null;
		
		if (initialise) {
			ret = (SimpleInstanceAttribute) getInstanceAttribute(attributeName, null); // FIXME - not typesafe
		} 
		else if (simpleInstanceAttributes != null) {
			ret = simpleInstanceAttributes.get(attributeName);
		}
		
		return ret;
	}

	public ToManyAggregation[] getToManyAggregations() {
		return toManyAggregations.getAll();
	}
	
	public ToManyAggregation getToManyAggregation(String attributeName) {
		ToManyAggregation ret = toManyAggregations.get(attributeName); // FIXME - need to instantiate it if it does not exist but is valid according to metadata
		
		if ( ret == null && getMetadata().getToManyAggregrationMetadata(attributeName) != null ) {
			ret = initialiseToManyAggregation(attributeName);
		}
		
		return ret;
	}
	
	private ToManyAggregation initialiseToManyAggregation(String attributeName) {
		ToManyAggregation ret = null;
		
		ToManyAggregationMetadata md = getMetadata().getToManyAggregrationMetadata(attributeName);
		if (md == null) {
			throw new IllegalArgumentException(attributeName + " is not a valid to-many aggregation for entity " + getEntityClassName());
		}
		
		toManyAggregations.setEntityReferences(attributeName, null, allMetadatas.get(attributeName));
		
		return ret;
	}

	public ToManyChildEntry[] getToManyChildren() {
		if (toManyChildren != null) {
			return toManyChildren.getAllChildren();
		}
		else {
			return new ToManyChildEntry[]{};
		}
	}
	
	/**
	 * Reads all the to many child metadatas and initialises each one.
	 */
	private void initialiseToManyChildren() {
		if (toManyChildren == null) {
			toManyChildren = new ToManyChildren(allMetadatas, path);
		}
		for ( ToManyCompositionMetadata tmcm : getMetadata().getToManyCompositionMetadatas() ) {
			getToManyChildEntry( tmcm.getAttributeName() );
		}
	}
	
	public ToManyChildEntry getToManyChildEntry(String attributeName) {
		if (toManyChildren == null) {
			toManyChildren = new ToManyChildren(allMetadatas, path);
		}
		
		ToManyChildEntry ret = toManyChildren.get(attributeName);
		
		if ( ret == null && getMetadata().getToManyCompositionMetadata(attributeName) != null ) {
			ret = initialiseToManyChildEntry(attributeName);
		}
		
		return ret;
	}
	
	private ToManyChildEntry initialiseToManyChildEntry(String attributeName) {
		ToManyChildEntry ret = null;
		
		ToManyCompositionMetadata childMd = getMetadata().getToManyCompositionMetadata(attributeName);
		if (childMd == null) {
			throw new IllegalArgumentException(attributeName + " is not a valid to-many composition for entity " + getEntityClassName());
		}
		
		String label = childMd.getLabel();
		EntityMetadata childEntityMetadata = allMetadatas.get(childMd.getTargetClassName());

		/*
		 * If the child is locale-dependent, then ensure that the default locale entry is initialised
		 */
		EntityInstance[] childEntries = null;
		if ( childEntityMetadata.isLocaleDependent() ) {
			DefaultEntityInstanceImpl instance = new DefaultEntityInstanceImpl(childMd.getTargetClassName(), allMetadatas);
			childEntries = new EntityInstance[]{instance};
			instance.setPath(getPath() + "/" + attributeName + "[0]");
		}
		
		toManyChildren.setInstances(attributeName, label, childEntries, childEntityMetadata);
		ret = toManyChildren.get(attributeName);
		
		return ret;
	}

	public ToOneAggregation[] getToOneAggregations() {
		return toOneAggregations.getAll();
	}
	
	public ToOneAggregation getToOneAggregation(String attributeName) {
		return getToOneAggregation(attributeName, true);
	}
	
	private ToOneAggregation getToOneAggregation(String attributeName, boolean initialise) {
		ToOneAggregation ret = null;

		/*
		 * FIXME - not typesafe
		 */
		if (initialise) {
			ret = (ToOneAggregation) getInstanceAttribute(attributeName, null); 
		} 
		else if (toOneAggregations != null) {
			ret = toOneAggregations.get(attributeName);
		}
		
		return ret;
	}

	public ToOneChildEntry[] getToOneChildren() {
		return toOneChildren.getAllChildren();
	}
	
	public ToOneChildEntry getToOneChildEntry(String attributeName) {
		ToOneChildEntry ret = toOneChildren.get(attributeName);

		if (ret == null && getMetadata().getToOneCompositionMetadata(attributeName) != null) {
			ret = initialiseToOneComposition(attributeName);
		}
		
		return ret;
	}
	
	private ToOneChildEntry initialiseToOneComposition(String attributeName){
		ToOneCompositionMetadata md = getMetadata().getToOneCompositionMetadata(attributeName);
		
		if (md == null) {
			throw new IllegalArgumentException(attributeName + " is not a recognised to-one child composition");
		}
		
		DefaultEntityInstanceImpl childInstance = new DefaultEntityInstanceImpl(
				md.getTargetClassName(),
				allMetadatas);
		childInstance.setEntityClassName(md.getTargetClassName());
		childInstance.setAllMetadatas(allMetadatas);
		childInstance.setPath(PathUtils.appendTrailingDot(path) + md.getAttributeName());
		
		toOneChildren.setEntityInstance(attributeName, md.getLabel(), childInstance);
		
		return toOneChildren.get(attributeName);
	}
	
	private void initialiseSimpleInstanceAttributes() {
		if ( isNew() ) {
			for ( PropertyMetadata pm : getMetadata().getSimpleInstanceAttributeMetadatas()) {
				getInstanceAttribute(pm.getAttributeName(), null);
			}
		}
	}
	
	private void initialiseToOneAggregations() {
		for ( PropertyMetadata pm : getMetadata().getToOneAggregationMetadatas()) {
			getInstanceAttribute(pm.getAttributeName(), null);
		}
	}

	public InstanceAttribute[] getInstanceAttributes(Locale locale) {
		
		/* 
		 * Force initialisation.  This is currently necessary due to the way that
		 * new instances are implemented.
		 */
		initialiseSimpleInstanceAttributes();
		
		initialiseToManyChildren();
		initialiseToOneAggregations();
		
		/*
		 * Retrieve any locale-dependent attributes.
		 */
		InstanceAttribute[] localeAttributes = null;
		
		// FIXME - this is broken if there are no locale data entries
		for ( ToManyChildEntry tcm : toManyChildren.getAllChildren() ) {
			if ( tcm.getEntityMetadata().isLocaleDependent() ) {
				
				EntityInstance localeInstance = getLocaleDependentEntityInstance(tcm, locale);
				if (localeInstance != null) {
					localeAttributes = localeInstance.getInstanceAttributes(locale);
				}
				
				if ( localeAttributes != null) {
					break;
				}
				
			}
		}
		
		if ( localeAttributes == null ) {
			localeAttributes = new SimpleInstanceAttribute[]{};
		}
		
		/*
		 * Loop through the to-many child entries, and find the locale dependent ones,
		 * and add in their attributes into the returned array.
		 */
		
		int simpleInstanceAttributesLength = getSimpleInstanceAttributes().length;
		if (getMetadata().isLocaleDependent()) {
			simpleInstanceAttributesLength--;
		}
		
		InstanceAttribute[] ret = new InstanceAttribute[
		                                                simpleInstanceAttributesLength +
		                                                getToOneAggregations().length + 
		                                                localeAttributes.length];
		int i = 0;
		
		for (SimpleInstanceAttribute attr : getSimpleInstanceAttributes()){
			if ( !(getMetadata().isLocaleDependent() && attr.getAttributeName().equals("locale")) ) { // FIXME - need a safe way of determining the locale attribute
				ret[i++] = attr;				
			}
		}
		
		for (ToOneAggregation agg : getToOneAggregations()){
			ret[i++] = agg;
		}
		
		for (InstanceAttribute attr : localeAttributes) {
			ret[i++] = attr;
		}
		
		Arrays.sort(ret);
		
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.sgodden.entities.EntityInstance#getInstanceAttribute(java.lang.String)
	 */
	public InstanceAttribute getInstanceAttribute(String attributePath, Locale locale) {
		
		InstanceAttribute ret = null;
		
		if ( PathUtils.isCompoundPath(attributePath) ) {
			
			String relationPath = attributePath.substring(0, attributePath.lastIndexOf('.'));
			String relationAttributeName = attributePath.substring(attributePath.lastIndexOf('.')+1, attributePath.length());
			
			// FIXME - go through the metadata so that we initialise null parts of the path
			
			// get the association
			Object relation = EntityInstanceUtils.getAttribute(this, relationPath);
			
			// if it's to-one compo then it will be an entity instance
			if (relation instanceof EntityInstance) {
				ret = ((EntityInstance)relation).getInstanceAttribute(relationAttributeName, locale);
			}
			// if it's locale-dependent to-many compo
			else if (
					relation instanceof ToManyChildEntry
					&& ((ToManyChildEntry)relation).getEntityMetadata().isLocaleDependent() ) {
				EntityInstance localeInstance = getLocaleDependentEntityInstance((ToManyChildEntry)relation, locale);
				if (localeInstance != null) {
					ret = localeInstance.getInstanceAttribute(relationAttributeName, locale);
				}
			}
			// else illegal
			else {
				throw new IllegalArgumentException("The associated object in a compound path must be a to-one composition or a locale-dependent to-many child entry");
			}
		}
		else {
			
			ret = getSimpleInstanceAttribute(attributePath, false); // prevent recursion
			
			if ( ret == null ) {
				ret = getToOneAggregation(attributePath, false);
			}
			
			/*
			 * If the return value is null, and the attribute name is valid
			 * according to our metadata, then instantiate it with a null value
			 * and return it.
			 */
			if ( ret == null ) {

				PropertyMetadata pmetadata = getMetadata().getInstanceAttributeMetadata(attributePath);
				
				if ( pmetadata != null ) {
					
					if (pmetadata instanceof SimpleInstanceAttributeMetadata) {
						SimpleInstanceAttributeMetadata simplemd = (SimpleInstanceAttributeMetadata) pmetadata;
						simpleInstanceAttributes.setValue(
								attributePath, 
								simplemd.getLabel(), 
								null, 
								simplemd.getUnique(), 
								simplemd.getUniqueInSet(), 
								this);
						ret = getInstanceAttribute(attributePath, locale); // DANGER - recursion
					} 
					else if (pmetadata instanceof ToOneAggregationMetadata) {
						ToOneAggregationMetadata toam = (ToOneAggregationMetadata) pmetadata;
						EntityReference reference = new EntityReference(toam.getTargetClassName(), null);
						toOneAggregations.setEntityReference(
								toam.getAttributeName(), 
								toam.getLabel(), 
								reference, 
								null,
								toam.getUnique(), 
								toam.getUniqueInSet(), 
								this);
						ret = getInstanceAttribute(attributePath, locale); // DANGER - recursion
					}
				
				}
			}
		}
		return ret;
	}
	
	/**
	 * Given a locale-dependent to-many child entry and a locale, returns the child instance
	 * whose locale most closely matches the supplied locale.
	 * @param tcm the to-many child entry, which must be locale-dependent.
	 * @param locale the requested locale.
	 * @return the most closely matching entry, or <code>null</code> if one cannot be found.
	 */
	private EntityInstance getLocaleDependentEntityInstance(ToManyChildEntry tcm, Locale locale) {
		
		EntityInstance ret = null;
		
		if ( !(tcm.getEntityMetadata().isLocaleDependent()) ) {
			throw new IllegalArgumentException("The to-many child entry must be locale-depdendent");
		}
	
		for ( Locale thisLocale : LocaleUtils.getLocaleHierarchy(locale) ) {
			for ( EntityInstance localeEntry : tcm.getInstances() ) {
				SimpleInstanceAttribute localeAttribute = localeEntry.getSimpleInstanceAttribute("locale");
				if ( localeAttribute == null ) {
					throw new IllegalArgumentException("An entity marked as locale dependent must have an attribute named 'locale'");
				}
				if ( thisLocale == null && localeAttribute.getValue() == null 
						|| ( thisLocale != null 
								&& localeAttribute.getValue() != null 
								&& thisLocale.toString().equals(localeAttribute.getValue().toString())
							) ) {
					log.debug("Returning instance for locale '" + thisLocale + "'.  User's locale was '" + locale + "'");
					ret = localeEntry;
					break;
				}
			}
			
			if ( ret != null ) {
				break;
			}
		}
		
		return ret;

	}
	
	@Override
	public String toString(){
		return new XStream().toXML(this);
	}

	public String provideLabel(Locale locale) {
		return getLabel();
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}

	public EntityMetadata getMetadata() {
		EntityMetadata ret = allMetadatas.get(getEntityClassName()); 
		return ret;
	}

	public boolean isNew() {
		return (getId() == null);
	}
	
	public SimpleInstanceAttributes getSimpleInstanceAttributesHolder(){
		return simpleInstanceAttributes;
	}
	
	public ToOneAggregations getToOneAggregationsHolder(){
		return toOneAggregations;
	}
	
	public ToOneChildren getToOneChildrenHolder() {
		return toOneChildren;
	}
	
	public ToManyChildren getToManyChildrenHolder() {
		return toManyChildren;
	}
	
	public ToManyAggregations getToManyAggregationsHolder() {
		return toManyAggregations;
	}

	/**
	 * Sets a map of metatadatas of all classes which appear in this
	 * object tree.  This allows the implementation to create new objects
	 * on the fly and set the correct metadata on them.
	 * 
	 * @param allEntityMetadatas
	 */
	public void setAllMetadatas(Map<String, EntityMetadata> allEntityMetadatas) {
		this.allMetadatas = allEntityMetadatas;
		
		toManyChildren = new ToManyChildren(allMetadatas, path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.entities.EntityInstance#makeClone()
	 */
	public EntityInstance makeClone() {
		DefaultEntityInstanceImpl ret = new DefaultEntityInstanceImpl(entityClassName, allMetadatas);
		
		ret.id = this.id;
		ret.label = this.label;
		ret.path = this.path;
		
		ret.simpleInstanceAttributes = simpleInstanceAttributes.makeClone();
		ret.toOneChildren = toOneChildren.makeClone();
		if (toManyChildren != null) {
			ret.toManyChildren = toManyChildren.makeClone();
		}
		ret.toOneAggregations = toOneAggregations.makeClone();
		ret.toManyAggregations = toManyAggregations.makeClone();
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.entities.EntityInstance#isDirty()
	 */
	public boolean isDirty() {
		return isNew() || isDirty; // new records are always dirty
	}
	
	/**
	 * Sets whether the instance is dirty.
	 * <p/>
	 * FIXME - this should not be public.
	 * 
	 * @param isDirty
	 */
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
	
}
