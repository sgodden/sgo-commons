package org.sgodden.ui.models;

/**
 * A model which can map a backing object to each row of data.
 * <p/>
 * This is useful for tracking selection when the key item of data
 * for the selected row(s) is not contained within the visible data
 * items.
 * <p/>
 * For instance, you may be showing a list of objects.  Each object is
 * keyed by an internal id which is not visible.  Therefore you could
 * not use selected row index to obtain something from the actual data
 * in order to know which user was selected.
 * <p/>
 * Using this constructor, you could pass that id, or the whole User
 * object as the backing object for that row, and then use the 
 * {@link #getBackingObject(int)} method to obtain that backing
 * object using the selected indices from the selection model.

 * @author sgodden
 */
public interface BackingObjectDataModel {
    
    /**
     * Returns the backing object for the row at the specified index.
     * @param rowIndex the row index.
     * @return the backing object for that row.
     */
    public Object getBackingObjectForRow(int rowIndex);
    
    /**
     * Returns the model value for the selected backing object.
     * @param backingObject the backing object.
     * @return the model value.
     */
    public Object getValueForBackingObject(Object backingObject);
    
}