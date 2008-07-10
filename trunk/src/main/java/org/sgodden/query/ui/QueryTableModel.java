package org.sgodden.query.ui;

import org.sgodden.ui.models.FilterableTableModel;
import org.sgodden.ui.models.SortableTableModel;

/**
 * A table model built on a simple object query interface.
 * @author sgodden
 */
public interface QueryTableModel extends FilterableTableModel,
        SortableTableModel {

    /**
     * Returns the object identifier for the specified row.
     * @param row the row, zero-indexed.
     * @return the object identifier for the object at the specified row.
     */
    public String getIdForRow(int row);

    /**
     * Returns the class of the entity being queried.
     * @return the entity class.
     */
    @SuppressWarnings("unchecked")
    public Class getEntityClass();

}
