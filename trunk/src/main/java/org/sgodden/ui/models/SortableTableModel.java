package org.sgodden.ui.models;

/**
 * A table model which can be sorted by a particular column index.
 * <p>
 * This is to cater for the common paradigm where a user may click
 * on a particular column header to indicate that they wish to sort
 * by it.  The table model may have its own natural sort sequence
 * already.  It is up to the implementation as to how it interprets
 * the semantics of a user sorting by a particular column in such
 * a case.  A common implementation would be to order by the new
 * column first, followed by the existing natural sort order.
 * </p>
 * @author sgodden
 *
 */
public interface SortableTableModel {
    
    /**
     * Sorts the table model by the specified (one-indexed)
     * column, and in the specified sort sequence.
     * @param columnIndex the column index to sort by, one-indexed.
     * @param ascending whether to sort ascending (true), or descending
     * (false).
     */
    public void sort(int columnIndex, boolean ascending);

}
