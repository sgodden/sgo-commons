package org.sgodden.query.ui;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.query.service.QueryService;

/**
 * Controller for lists that use the query service.
 * 
 * @author sgodden
 *
 */
public class QueryListController {
	
	private static final transient Log log = LogFactory.getLog(QueryListController.class);
	
	private Class entityClass;
	private String[] attributePaths;
	private Map<String, Object> filterValues = new HashMap<String, Object>();
	
	private DefaultQueryTableModel model;
	
	private QueryService queryService;
	
	//private Context context;
	
	public QueryListController(){
		//this.context = Context.getCurrentContext();
	}
	
	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}
	
	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}
	
	public void setAttributePaths(String[] attributePaths) {
		this.attributePaths = attributePaths;
	}
	
	public Map<String, Object> getFilterValues() {
		return filterValues;
	}
	
	public void setFilterValues(Map<String, Object> filterValues) {
		this.filterValues = filterValues;
	}
	
	public QueryTableModel getModel() {
		if (model == null) {
			log.debug("Creating table model");
			model = new DefaultQueryTableModel(
					Locale.getDefault(), // FIXME - retrieve the correct locale
					entityClass,
					attributePaths, // FIXME - retrieve the column headers
					attributePaths,
					queryService
				);
			model.setMaxRows(50);
		}
		
		return model;
	}
	
	public void refresh() {
		model.refresh(filterValues);
	}
	
	/**
	 * Returns the where clause for the query based on the
	 * passed map of values.
	 * @param filterValues the map of filter values.
	 * @return the where clause as a string.
	 */
	protected String makeWhereClause(Map<String, Object> filterValues) {
		return null;
	}

}
