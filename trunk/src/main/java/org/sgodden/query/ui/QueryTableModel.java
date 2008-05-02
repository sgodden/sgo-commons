package org.sgodden.query.ui;

import org.sgodden.ui.mvc.models.FilterableTableModel;

public interface QueryTableModel 
		extends FilterableTableModel {
	
	public String getIdForRow(int row);
	
	public Class getEntityClass();

}
