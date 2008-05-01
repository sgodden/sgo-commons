package org.sgodden.resources;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides utility methods related to resources.
 * 
 * @author goddens
 *
 */
public class ResourceUtils {
	
	public static ResourceBundle getDefaultLiteralsResourceBundle(String entityClassName, Locale locale){
		ResourceBundle ret = null;
		
		ret = ResourceBundle.getBundle(getBasePackageNameForEntityClass(entityClassName) + ".Literals", locale);
		
		return ret;
	}
	
	public static String getBasePackageNameForEntityClass(String entityClassName){
		return entityClassName.substring(0, entityClassName.indexOf(".entities"));
	}
	
	public static String getSingularLabelForClass(Class clazz, Locale locale) {
		return getSingularLabelForClass( clazz.getName(), locale );
	}
	
	private static String getSingularLabelForClass(
			String fullyQualifiedClassName, 
			Locale locale) {
		ResourceBundle rb = getDefaultLiteralsResourceBundle(fullyQualifiedClassName, locale);
		return rb.getString(getUnqualifiedClassName(fullyQualifiedClassName));
	}
	
	public static String getPluralLabelForClass(Class clazz, Locale locale) {
		return getPluralLabelForClass( clazz.getName(), locale );
	}
	
	private static String getPluralLabelForClass(
			String fullyQualifiedClassName, 
			Locale locale) {
		ResourceBundle rb = getDefaultLiteralsResourceBundle(fullyQualifiedClassName, locale);
		return rb.getString(getUnqualifiedClassName(fullyQualifiedClassName) + ".plural");
	}
	
	private static String getUnqualifiedClassName(String fullyQualifiedClassName) {
		return fullyQualifiedClassName.substring(
				fullyQualifiedClassName.lastIndexOf('.'),
				fullyQualifiedClassName.length()
				);
	}

}
