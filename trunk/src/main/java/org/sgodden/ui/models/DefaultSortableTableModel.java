package org.sgodden.ui.models;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import nextapp.echo.app.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An extension of
 * 
 * @author sgodden
 */
@SuppressWarnings("serial")
public class DefaultSortableTableModel extends DefaultTableModel implements
        SortableTableModel, BackingObjectDataModel {

    private static final transient Log LOG = LogFactory
            .getLog(DefaultSortableTableModel.class);

    /**
     * In the case that the user needs to track selections, this allows them to
     * track what was selected.
     */
    private Map<Integer, Object> rowsToBackingObjects;

    /**
     * Constructs a new sortable table model using the passed data. Note that
     * using this constructor, you may not be able to effectively track
     * selection unless the key element is contained in the data itself.
     * <p/>
     * This is because the data could get sorted, so the original positions of
     * data rows may change. If you attempt to use the selection indices of the
     * table to retrieve data from some original inputs to this data model, they
     * may be wrong.
     * 
     * @param data
     *            the table data.
     * @param columnNames
     *            the column names.
     */
    public DefaultSortableTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    /**
     * Constructs a new sortable table model using the passed data, and the
     * passed backing objects for each row.
     * <p>
     * See {@link BackingObjectDataModel} for further explanation.
     * 
     * @param data
     *            the model data.
     * @param columnNames
     *            the column names.
     * @param backingObjects
     *            an array of backing object per row.
     */
    public DefaultSortableTableModel(Object[][] data, Object[] columnNames,
            Object[] backingObjects) {
        super(data, columnNames);
        setBackingObjects(backingObjects);
    }

    /**
     * Returns a hashcode for the specified row.
     * <p>
     * FIXME - this will break if two rows have the same data in them.
     * </p>
     * 
     * @param rowIndex
     * @return
     */
    private int getHashCodeForRow(int rowIndex) {
        int ret = 0;
        Object[] row = new Object[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            ret += getValueAt(i, rowIndex).hashCode();
        }
        return ret;
    }

    /**
     * See {@link BackingObjectDataModel#getBackingObjectForRow(int)}.
     * 
     * @param rowIndex
     *            the row index.
     * @return the backing object for that row index.
     */
    public Object getBackingObjectForRow(int rowIndex) {
        if (rowsToBackingObjects == null) {
            throw new IllegalStateException("Cannot request backing"
                    + " objects when none were set at construction time");
        }
        int rowHash = getHashCodeForRow(rowIndex);
        LOG.info("Row " + rowIndex + " has hashCode " + rowHash);
        return rowsToBackingObjects.get(rowHash);
    }

    /**
     * See {@link BackingObjectDataModel#setBackingObjects(java.lang.Object[])}.
     * 
     * @param backingObjects
     *            the backing objects.
     */
    public void setBackingObjects(Object[] backingObjects) {
        rowsToBackingObjects = new HashMap<Integer, Object>();
        for (int i = 0; i < backingObjects.length; i++) {
            int rowHash = getHashCodeForRow(i);
            LOG.info("Row " + i + " has hashCode " + rowHash);
            rowsToBackingObjects.put(rowHash, backingObjects[i]);
        }
    }

    /**
     * See {@link SortableTableModel#sort(int, boolean)}.
     * 
     * @param columnIndex
     *            the column index.
     * @param ascending
     *            whether to sort ascending (true) or descending (false).
     */
    public void sort(int columnIndex, boolean ascending) {

        Object[] columnNames = new Object[getColumnCount()];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = getColumnName(i);
        }

        Object[][] rowData = new Object[getRowCount()][getColumnCount()];
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                rowData[row][col] = getValueAt(col, row);
            }
        }

        /*
         * Remember columnIndex has been passed to us one-indexed.
         */
        Arrays.sort(rowData, new ArrayColumnComparator(columnIndex, ascending));

        for (int i = 0; i < rowData.length; i++) {
            addRow(rowData[i]);
        }
    }

    /**
     * See {@link SortableTableModel#sort(int[], boolean[])}
     * 
     * @param columnIndices
     * @param ascending
     */
    public void sort(int[] columnIndices, boolean[] ascending) {
        // FIXME - this is a vary lazy way to do it
        for (int i = columnIndices.length - 1; i >= 0; i--) {
            sort(columnIndices[i], ascending[i]);
        }
    }

    /**
     * Compares the specified column of an array.
     * 
     * @author sgodden
     */
    private static class ArrayColumnComparator implements Comparator<Object[]> {

        /**
         * The column index to compare.
         */
        private int colIndex;
        /**
         * The order in which to perform the sort.
         */
        private boolean ascending;

        /**
         * Creates a new comparator, comparing the object arrays on the
         * specified index.
         * 
         * @param columnIndex
         *            the column index to compare.
         */
        private ArrayColumnComparator(int columnIndex, boolean ascending) {
            this.colIndex = columnIndex;
            this.ascending = ascending;
        }

        /**
         * See {@link Comparator#compare(Object, Object)}.
         * 
         * @param oa1
         *            the first object array.
         * @param oa2
         *            the second object array.
         */
        @SuppressWarnings("unchecked")
        public int compare(Object[] oa1, Object[] oa2) {
            Comparable c1 = (Comparable) oa1[colIndex];
            Comparable c2 = (Comparable) oa2[colIndex];
            if (ascending) {
                return c1.compareTo(c2);
            } else {
                return c2.compareTo(c1);
            }
        }

    }

    /**
     * Not implemented - See
     * {@link BackingObjectDataModel#getValueForBackingObject(Object)}.
     */
    public Object getValueForBackingObject(Object backingObject) {
        // FIXME - shows that the API is wrong
        throw new UnsupportedOperationException();
    }
}