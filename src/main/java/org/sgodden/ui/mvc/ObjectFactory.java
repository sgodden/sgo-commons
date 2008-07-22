package org.sgodden.ui.mvc;

/**
 * An factory which creates participant objects for flows.
 * <p/>
 * A common usage will be to retrieve objects from the
 * dependency injection framework being used.
 * @author sgodden
 */
public interface ObjectFactory {
    
    /**
     * Returns a new object of the given name.
     * @param objectName the name of the object.
     * @return the named object.
     */
    public Object makeObject(String objectName);

}
