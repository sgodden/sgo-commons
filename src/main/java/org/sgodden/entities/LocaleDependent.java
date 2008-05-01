package org.sgodden.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the attributes on the specified class are locale-dependent.
 * <p/>
 * This would normally mean that the class exists on the many side of one or more one-to-many
 * composition relationships, in order to provide multiple values for attributes whose value
 * needs to vary by locale, for instance description fields.
 * 
 * @author simon
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LocaleDependent {
}
