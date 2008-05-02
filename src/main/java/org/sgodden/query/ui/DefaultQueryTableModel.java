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

import java.util.Locale;

import org.sgodden.query.AggregateFunction;
import org.sgodden.query.Query;
import org.sgodden.query.service.QueryService;

/**
 * A default implementation of a query table model, which allows simple
 * queries to be run.
 * <p/>
 * If you wish to use advanced features such as aggregate functions, then
 * you will need to extend {@link AbstractQueryTableModel} and make the
 * query yourself.
 * 
 * @author goddens
 *
 */
public class DefaultQueryTableModel 
		extends AbstractQueryTableModel 
		{

	private int fetchSize = 100;
	private int bailOutSize = 10000;
	private int maxRows = -1;
	
	private Object[] columnIdentifiers;
	private String[] attributePaths;
	private Class entityClass;
	private Locale locale;
	
	/**
	 * Creates a new DefaultQueryTableModel instance
	 * with the specified parameters.
	 * 
	 * @param entityClass the class for which instance values are to be retrieved.
	 * @param columnIdentifiers the column identifiers.
	 * @param attributePaths the attribute paths for each column.
	 */
	public DefaultQueryTableModel(
			Locale locale,
			Class entityClass,
			Object[] columnIdentifiers,
			String[] attributePaths,
			QueryService queryService){
		
		super(queryService);

		this.entityClass = entityClass;
		this.columnIdentifiers = columnIdentifiers;
		this.attributePaths = attributePaths;
	}
	
	protected final Query makeQuery(){
    	Query query = new Query();
    	
    	for (String attributePath : attributePaths){
    		if (attributePath.endsWith("localeData.description")) { // FIXME - this kind of assumption reduces the flexibility of this framework
    			query.addColumn(attributePath, AggregateFunction.LOCALE);
    		} else {
    			query.addColumn(attributePath);
    		}
    	}
    	
		query.setLocale(locale); 
		query.setObjectClass(entityClass.getName());
		query.setFetchSize(fetchSize);
		query.setBailOutSize(bailOutSize);
		
		if (maxRows != -1) {
			query.setMaxRows(maxRows);
		}
		
		return query;
	}

	@Override
	protected Object[] getColumnIdentifiers() {
		return columnIdentifiers;
	}
	
	/**
	 * Sets the number of rows that should be fetched in one call
	 * to the query service.
	 * 
	 * @param fetchSize
	 */
	public void setFetchSize(int fetchSize){
		this.fetchSize = fetchSize;
	}
	
	/**
	 * Sets the row count at which the query service will bail out (that is, not retrieve
	 * any rows at all).
	 * 
	 * @param bailOutSize
	 */
	public void setBailOutSize(int bailOutSize){
		this.bailOutSize = bailOutSize;
	}
	
	/**
	 * Sets the maximum number of rows to retrieve.
	 * @param maxRows the maximum number of rows to retrieve.
	 */
	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public Class getEntityClass() {
		return entityClass;
	}

}
