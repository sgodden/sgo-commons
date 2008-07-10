/*
 * ================================================================= # This
 * library is free software; you can redistribute it and/or # modify it under
 * the terms of the GNU Lesser General Public # License as published by the Free
 * Software Foundation; either # version 2.1 of the License, or (at your option)
 * any later version. # # This library is distributed in the hope that it will
 * be useful, # but WITHOUT ANY WARRANTY; without even the implied warranty of #
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU # Lesser
 * General Public License for more details. # # You should have received a copy
 * of the GNU Lesser General Public # License along with this library; if not,
 * write to the Free Software # Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301 USA # #
 * =================================================================
 */
package org.sgodden.query.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.sgodden.query.Operator;
import org.sgodden.query.Query;
import org.sgodden.query.ResultSet;
import org.sgodden.query.ResultSetRow;
import org.sgodden.query.service.QueryService;
import org.sgodden.ui.mvc.ModelListener;
import org.sgodden.ui.models.SortData;

/**
 * Abstract implementation of the {@link QueryTableModel} interface.
 * @author goddens
 */
public abstract class AbstractQueryTableModel extends AbstractTableModel
        implements QueryTableModel {

    /**
     * The query service which will actually execute the queries.
     */
    private QueryService service;
    
    /**
     * The filter criteria used on the last query refresh.
     */
    private Map<String, Object> filterCriteria;

    /**
     * Sets the query service to be used to run the queries.
     * @param queryService the query service.
     */
    public void setQueryService(QueryService queryService) {
        this.service = queryService;
    }

    /**
     * The list of listeners to be notified of a model change.
     */
    private List < ModelListener > listeners = new ArrayList < ModelListener >();

    /**
     * The result set returned from the query service.
     */
    private ResultSet rs;

    /**
     * Constructs a new abstract query table model.
     */
    public AbstractQueryTableModel() {
        super();
    }

    /**
     * Constructs a new abstract query table model, using the passed service to
     * execute the queries.
     * @param service the query service.
     */
    public AbstractQueryTableModel(QueryService service) {
        this();
        this.service = service;
    }

    /**
     * Adds a listener to be notified of model changes.
     * @param listener the listener to be added.
     */
    public void addModelListener(ModelListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified model listener.
     * @param listener the listener to be removed.
     */
    public void removeModelListener(ModelListener listener) {
        listeners.remove(listener);
    }

    /**
     * Internal method which actually performs model refresh.
     * @param query the query to (re)execute.
     */
    protected void doRefresh(Query query) {
        rs = service.executeQuery(query);
        fireTableDataChanged();
    }

    /**
     * Returns the array of column identifiers.
     * @return the array of column identifiers.
     */
    protected abstract Object[] getColumnIdentifiers();

    /**
     * Returns the column name for the specified index, zero-indexed.
     * @param column the column index, zero-indexed.
     */
    @Override
    public String getColumnName(int column) {
        return getColumnIdentifiers()[column].toString();
    }

    /**
     * Returns the result set.
     * @return the result set.
     */
    protected ResultSet getResultSet() {
        return rs;
    }

    /**
     * See {@link javax.swing.table.TableModel#getValueAt(int, int)}.
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int colIndex) {
        ResultSetRow row = rs.getRow(rowIndex);
        return row.getColumns()[colIndex].getValue();
    }

    /**
     * See {@link javax.swing.table.AbstractTableModel#isCellEditable(int, int)}.
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * See {@link org.sgodden.query.ui.QueryTableModel#getIdForRow(int)}.
     * @see org.sgodden.query.ui.QueryTableModel#getIdForRow(int)
     */
    public String getIdForRow(int row) {
        return rs.getRow(row).getId();
    }

    /**
     * See {@link javax.swing.table.TableModel#getRowCount()}
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (rs == null) {
            refresh(null);
        }
        if (!rs.getQueryBailedOut()) {
            return rs.getRowCount();
        }
        else {
            return 0;
        }
    }

    /**
     * See {@link javax.swing.table.TableModel#getColumnCount()}.
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return getColumnIdentifiers().length;
    }

    /**
     * Returns the total number of matches for the query.
     * @return the total number of matches for the query.
     */
    public int getQueryMatchCount() {
        return rs.getCachedRowCount();
    }

    /**
     * Returns whether the query bailed out and did not retrieve any rows, due
     * to their being too many matches.
     * @return whether the query bailed out.
     */
    public boolean getQueryBailedOut() {
        return rs.getQueryBailedOut();
    }

    /**
     * Refreshes the model based on the specified filter criteria, which may be
     * null to retrieve all rows.
     * <p>
     * This implementation just uses the LIKE operator for all string criteria
     * and EQUALS for all other types - override this method if you need to
     * change that.
     * </p>
     * <p>
     * XXX - changing filter criteria really means the model is changing,
     * so this method should be removed?
     * </p>
     * @param filterCriteria the filter criteria to put in the query, or
     *            <code>null</code> to perform no filtering.
     * @param sortData the primary sort data to use, or <code>null</code> to
     *            specify no primary sort.
     */
    private void refresh(Map < String, Object > filterCriteria,
            SortData sortData) {
        Query query = makeQuery();

        if (filterCriteria != null) {
            for (String s : filterCriteria.keySet()) {
                Object o = filterCriteria.get(s);
                if (o != null) {
                    if (o instanceof String) {
                        query.addFilterCriterion(s, Operator.LIKE,
                                (String) o + '%');
                    }
                    else {
                        query.addFilterCriterion(s, Operator.EQUALS, o);
                    }
                }
            }
        }
        
        if (sortData != null) {
            query.setSortData(sortData);
        }

        doRefresh(query);
    }

    /**
     * Refreshes the model based on the specified filter criteria, which may be
     * null to retrieve all rows.
     * <p>
     * This implementation just uses the LIKE operator for all string criteria
     * and EQUALS for all other types - override this method if you need to
     * change that.
     * </p>
     * @param filterCriteria the filter criteria to put in the query, or
     *            <code>null</code> to perform no filtering.
     */
    public void refresh(Map < String, Object > filterCriteria) {
        refresh(filterCriteria, null);
    }

    /**
     * Creates the query.
     * @return the query to run.
     */
    protected abstract Query makeQuery();

    /**
     * See {@link org.sgodden.ui.mvc.models.SortableTableModel#sort(int, org.sgodden.ui.mvc.models.SortOrder)}.
     * @see org.sgodden.ui.mvc.models.SortableTableModel#sort(int, org.sgodden.ui.mvc.models.SortOrder)
     */
    public void sort(int columnIndex, boolean ascending) {
        refresh(filterCriteria, new SortData(columnIndex, ascending));
    }

}