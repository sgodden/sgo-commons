package org.sgodden.ui.mvc.models;

import java.io.Serializable;


/**
 * Simple holder for sort data.
 * @author sgodden
 */
public class SortData implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 20080528L;

    /**
     * The column index on which to sort, 1-indexed.
     */
    private int columnIndex;

    /**
     * The sort order.
     */
    private SortOrder sortOrder;

    /**
     * Constructs a new sort data.
     * @param columnIndex the column index.
     * @param sortOrder the sort order.
     */
    public SortData(int columnIndex, SortOrder sortOrder) {
        this.columnIndex = columnIndex;
        this.sortOrder = sortOrder;
    }

    /**
     * Returns the sort column index, one-indexed.
     * @return the sort column inded, one-indexed.
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Returns the sort order.
     * @returnthe sort order.
     */
    public SortOrder getSortOrder() {
        return sortOrder;
    }

}