package org.sgodden.ui.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;

/**
 * Extends {@link DefaultListModel} to allow a backing object to be
 * maintained per item in the list.
 * @author sgodden
 */
public class DefaultBackingObjectListModel 
            extends DefaultListModel implements BackingObjectDataModel {
    
    private List < Object > backingObjects = new ArrayList < Object > ();
    private Map < Object, Object > backingObjectsToItems = new HashMap < Object, Object >();
    
    /**
     * Adds the specified element to the list, with the corresponding
     * backing object.
     * @param item the item to be rendered in the list.
     * @param backingObject the backing object for the item.
     */
    public void addElement(Object item, Object backingObject) {
        super.addElement(item);
        backingObjects.add(backingObject);
        backingObjectsToItems.put(backingObject, item);
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
     * See {@link BackingObjectDataModel#getValueForBackingObject(Object)}.
     * @param backingObject the backing object.
     * @return the model value.
     */
    public Object getValueForBackingObject(Object backingObject) {
        return backingObjectsToItems.get(backingObject);
    }

}