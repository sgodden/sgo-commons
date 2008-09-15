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

}