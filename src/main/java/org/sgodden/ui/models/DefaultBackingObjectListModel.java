package org.sgodden.ui.models;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Extends {@link DefaultListModel} to allow a backing object to be
 * maintained per item in the list.
 * @author sgodden
 */
public class DefaultBackingObjectListModel 
            extends DefaultListModel implements BackingObjectDataModel {
    
    private List<Object> backingObjects = new ArrayList<Object>();
    
    /**
     * Adds the specified element to the list, with the corresponding
     * backing object.
     * @param item the item to be rendered in the list.
     * @param backingObject the backing object for the item.
     */
    public void addElement(Object item, Object backingObject) {
        super.addElement(item);
        backingObjects.add(backingObject);
    }

    /**
     * Returns the backing object for the specified row.
     * @param rowIndex the index of the row.
     * @return the backing object.
     */
    public Object getBackingObjectForRow(int rowIndex) {
        return backingObjects.get(rowIndex);
    }
    
    /**
     * Sets the backing objects for the rows.
     * @param backingObjects the backing objects.
     */
    public void setBackingObjects(Object[] backingObjects) {
        this.backingObjects.clear();
        for (Object object : backingObjects) {
            this.backingObjects.add(object);
        }
    }


}