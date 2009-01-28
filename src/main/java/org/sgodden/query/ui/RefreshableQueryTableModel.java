package org.sgodden.query.ui;

import org.sgodden.query.Restriction;
import org.sgodden.ui.models.SortData;

public interface RefreshableQueryTableModel extends QueryTableModel {
    public void refresh(Restriction criterion);
    public void refresh(Restriction criterion,
            SortData[] sortData);
}
