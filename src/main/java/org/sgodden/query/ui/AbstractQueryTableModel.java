/* =================================================================
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#
# ================================================================= */
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
import org.sgodden.ui.mvc.messages.Message;
import org.sgodden.ui.mvc.messages.MessageModel;

/**
 * FIXME - need to notify listeners of change.
 * 
 * @author goddens
 *
 */
public abstract class AbstractQueryTableModel extends AbstractTableModel 
		implements QueryTableModel {

	private QueryService service;

	/**
	 * Sets the query service to be used to run the queries.
	 * @param queryService the query service.
	 */
	public void setQueryService(QueryService queryService) {
		this.service = queryService;
	}
	
	private List<ModelListener> listeners = new ArrayList<ModelListener>();
	
	private ResultSet rs;
	
	public AbstractQueryTableModel(){
		super();
	}
	
	public AbstractQueryTableModel(QueryService service) {
		this();
		this.service = service;
	}
	
	public void addModelListener(ModelListener listener) {
		listeners.add(listener);
	}
	
	public void removeModelListener(ModelListener listener) {
		listeners.remove(listener);
	}
	
	protected void doRefresh(Query query){
		rs = service.executeQuery(query);
		fireTableDataChanged();
	}
	
	protected abstract Object[] getColumnIdentifiers();
	
	public String getColumnName(int column){
		return getColumnIdentifiers()[column].toString();
	}
	
	protected ResultSet getResultSet(){
		return rs;
	}
	
	public Object getValueAt(int rowIndex, int colIndex){
		ResultSetRow row = rs.getRow(rowIndex);
		return row.getColumns()[colIndex].getValue();
	}
	
	public boolean isCellEditable(int row, int column){
		return false;
	}
	
	public String getIdForRow(int row){
		return rs.getRow(row).getId();
	}
	
	public int getRowCount(){
		if (rs == null){
			refresh(null);
		}
		if (!rs.getQueryBailedOut()){
			return rs.getRowCount();			
		} else {
			return 0;
		}
	}
	
	public int getColumnCount(){
		return getColumnIdentifiers().length;
	}
	
	/**
	 * Returns the number of matches for the query.
	 * @return
	 */
	public int getQueryMatchCount(){
		return rs.getRowCount();
	}
	
	/**
	 * Returns whether the query bailed out and did
	 * not retrieve any rows, due to their being
	 * too many matches.
	 * 
	 * @return
	 */
	public boolean getQueryBailedOut(){
		return rs.getQueryBailedOut();
	}
	
	/**
	 * Refreshes the model based on the specified filter criteria, which may be
	 * null to retrieve all rows.
	 * <p/>
	 * This implementation just uses the LIKE operator for all
	 * string criteria and EQUALS for all other types - override this method if you need to change that.
	 * <p/>
	 * FIXME - a map paradigm is not sufficient for complex filtering 
	 * requirements.
	 * 
	 * @param filterCriteria the filter criteria to put in the query, or <code>null</code> to perform no filtering.
	 */
	public void refresh(Map<String, Object> filterCriteria){ 
		Query query = makeQuery();
		
		if (filterCriteria != null) {
			for (String s : filterCriteria.keySet()){
				Object o = filterCriteria.get(s);
				if (o != null) {
					if (o instanceof String) {
						query.addFilterCriterion(s, Operator.LIKE, (String)o + '%');
					} else {
						query.addFilterCriterion(s, Operator.EQUALS, o);
					}
				}
			}
		}
		
		doRefresh(query);
	}
	
	/**
	 * Refreshes the model based on the specified filter criteria, and adds messages
	 * to the passed message model indicating the results.
	 * 
	 * @param filterCriteria the filter criteria to put in the query, or <code>null</code> to perform no filtering.
	 * @param messageModel the model to which result messages should be added.
	 * 
	 * @see #refresh(Map)
	 */
	public void refresh(Map<String, Object> filterCriteria, MessageModel messageModel) {
		refresh(filterCriteria);

        if (messageModel != null) {

            if (getRowCount() == 0 && getQueryBailedOut()){
                messageModel.addMessage(
                        new Message(
                                "too.many.matches",
                                "There were too many matches for the query: " + getQueryMatchCount(),
                                null)
                        );
            } else {
                messageModel.addMessage(
                        new Message(
                                "total.rows.returned",
                                "Total rows returned: " + getRowCount(),
                                null)
                        );

            }

        }

    }

	protected abstract Query makeQuery();

}
