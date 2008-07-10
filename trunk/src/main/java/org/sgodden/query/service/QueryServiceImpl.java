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
package org.sgodden.query.service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.type.CalendarType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.sgodden.query.AggregateFunction;
import org.sgodden.query.DataType;
import org.sgodden.query.FilterCriterion;
import org.sgodden.query.LocaleUtils;
import org.sgodden.query.ObjectUtils;
import org.sgodden.query.Operator;
import org.sgodden.query.Query;
import org.sgodden.query.QueryColumn;
import org.sgodden.query.ResultSet;
import org.sgodden.query.ResultSetColumn;
import org.sgodden.query.ResultSetRow;
import org.sgodden.ui.models.SortOrder;

/**
 * An implementation of the query service which uses hibernate.
 * @author goddens
 */
public class QueryServiceImpl implements QueryService {

    /**
     * The log.
     */
    private static final transient Log log = LogFactory
            .getLog(QueryServiceImpl.class);

    /**
     * The hibernate session factory.
     */
    private SessionFactory sessionFactory;

    /**
     * See
     * {@link org.sgodden.query.service.QueryService#executeQuery(org.sgodden.query.Query}.
     * @param query the query to execute.
     */
    public ResultSet executeQuery(Query query) {

        Date startTime = null;

        if (log.isDebugEnabled()) {
            startTime = new Date();
        }

        ResultSet ret = new ResultSet();
        ret.setQueryService(this);
        ret.setQuery(query);

        // calculate the result set size, and total size
        if (query.getCalculateRowCount() || query.getBailOutSize() > 0) {
            int totalRowCount = (int) getRowCount(query);
            ret.setRowCount(totalRowCount);
            if (query.getMaxRows() > 0 && query.getMaxRows() < totalRowCount) {
                ret.setRowCount(query.getMaxRows());
            }
            if (ret.getRowCount() == 0) {
                /*
                 * The count query was run and there were no rows, so there is no
                 * point running the full query.
                 */
                log.debug("Count query says no rows, " +
                        "so not running the main query");
                return ret;
            }
        }

        /*
         * Check to see if there is a max number of rows over which the client
         * wishes the query to bail out.
         */
        if (query.getBailOutSize() > 0
                && ret.getRowCount() > query.getBailOutSize()) {
            ret.setQueryBailedOut(true);
            log.debug("Bailing out as there were too many matches - "
                    + ret.getRowCount() + " compared to maximum "
                    + query.getBailOutSize() + " set by client");
            return ret;
        }

        StringBuffer buf = getSelectClause(query);

        buf.append(" FROM " + query.getObjectClass() + " AS obj");

        Set < String > aliases = appendFromClause(query, buf);

        appendFromClauseForFilterCriteria(query, buf, aliases);

        appendWhereClause(query, buf);

        appendGroupByClause(query, buf);

        appendOrderByClause(query, buf);

        String queryString = buf.toString();
        log.debug(queryString);

        if (query.getFetchSize() > 0 && query.getMaxRows() > 0) {
            throw new IllegalArgumentException(
                    "Setting fetch size and max rows is contradictory");
        }

        org.hibernate.Query hq = sessionFactory.getCurrentSession()
                .createQuery(queryString);

        if (query.getRowOffset() > 0) {
            log.debug("Setting offset: " + query.getRowOffset());
            hq.setFirstResult(query.getRowOffset());
        }
        if (query.getFetchSize() > 0) {
            log.debug("Setting fetch limit to " + query.getFetchSize());
            hq.setMaxResults(query.getFetchSize() + 1);
        }
        else if (query.getMaxRows() > 0) {
            log.debug("Setting max rows to " + query.getMaxRows());
            hq.setMaxResults(query.getMaxRows());
        }

        List < ResultSetRow > rows = new ArrayList < ResultSetRow >();

        Date hqStartTime = new Date();

        Iterator it = hq.iterate();

        if (log.isDebugEnabled()) {
            log.debug("Hibernate query took "
                    + (new Date().getTime() - hqStartTime.getTime()) + " ms");
        }

        Locale locale = null;
        if (query.getLocale() != null) {
            locale = query.getLocale();
        }
        else {
            locale = Locale.getDefault();
        }

        DateFormat dateformat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT, locale);

        while (it.hasNext()) {
            Object[] row = (Object[]) it.next();
            ResultSetRow rsRow = new ResultSetRow();
            rsRow.setId(row[0].toString());

            ResultSetColumn[] remainingColumns = new ResultSetColumn[row.length - 1];
            for (int i = 1; i < row.length; i++) {
                ResultSetColumn col = new ResultSetColumn();
                QueryColumn queryCol = query.getColumns().get(i - 1);
                Object value = row[i];

                if (value != null
                        && AggregateFunction.LOCALE == query.getColumns().get(
                                i - 1).getAggregateFunction()) {
                    String theString = (String) value;
                    value = theString.substring(10, theString.length());
                }

                Type propertyType = ObjectUtils.getPropertyClass(query
                        .getObjectClass(), queryCol.getAttributePath(),
                        sessionFactory);

                if (propertyType instanceof StringType) {
                    col.setDataType(DataType.STRING);
                    if (value != null) {
                        col.setValue(value);
                    }
                }
                else if (propertyType instanceof IntegerType) {
                    col.setDataType(DataType.INTEGER);
                    if (value != null) {
                        col.setValue(value);
                    }
                }
                else if (propertyType instanceof LongType) {
                    col.setDataType(DataType.LONG);
                    if (value != null) {
                        col.setValue(value);
                    }
                }
                else if (propertyType instanceof CalendarType) { // FIXME -
                                                                    // should
                                                                    // transfer
                                                                    // in a
                                                                    // standard
                                                                    // format
                                                                    // such as
                                                                    // internet
                                                                    // format
                    col.setDataType(DataType.TIMESTAMP);
                    if (value != null) {
                        Calendar cal = (Calendar) value;
                        col.setValue(dateformat.format(cal.getTime()));
                    }
                }
                else {
                    throw new IllegalArgumentException("Cannot handle type: "
                            + value.getClass().getName());
                }

                remainingColumns[i - 1] = col;
            }

            rsRow.setColumns(remainingColumns);
            rows.add(rsRow);
        }

        ret.setCachedPageRows(rows);

        if (log.isDebugEnabled()) {
            log.debug("Query took "
                    + (new Date().getTime() - startTime.getTime()) + " ms");
        }

        return ret;
    }

    /**
     * Executes a count query for the specified query and returns the result.
     * @param query the query.
     * @return the total number of matches (the count) for the query.
     */
    private long getRowCount(Query query) {

        long ret = 0;

        StringBuffer buf = new StringBuffer("SELECT COUNT(distinct obj.id) ");

        buf.append(" FROM " + query.getObjectClass() + " AS obj");
        Set < String > aliases = new HashSet < String >();
        aliases.add("obj");

        appendFromClause(query, buf);
        appendFromClauseForFilterCriteria(query, buf, aliases);

        appendWhereClause(query, buf);

        String queryString = buf.toString();

        log.debug("Calculating total rows with query: " + queryString);
        org.hibernate.Query hq = sessionFactory.getCurrentSession()
                .createQuery(queryString);

        Date start = null;
        if (log.isDebugEnabled()) {
            start = new Date();
        }

        ret = (Long) hq.uniqueResult();

        log.debug("There are " + ret + " total rows");
        if (log.isDebugEnabled()) {
            log.debug("Count query took: "
                    + (new Date().getTime() - start.getTime()) + " ms");
        }

        return ret;
    }

    /**
     * Constructs the select clause for the query.
     * @param query the query.
     * @return the select clause.
     */
    private StringBuffer getSelectClause(Query query) {
        StringBuffer ret = new StringBuffer("SELECT obj.id");

        for (QueryColumn col : query.getColumns()) {

            ret.append(", ");

            AggregateFunction func = col.getAggregateFunction();

            if (AggregateFunction.LOCALE == func) { // LOCALE is a really
                                                    // special case
                ret.append(makeLocaleAggregateSelect(query, col));
            }
            else {

                if (func != null) {
                    if (func == AggregateFunction.MAXIMUM) {
                        ret.append("MAX(");
                    }
                    else if (func == AggregateFunction.MINIMUM) {
                        ret.append("MIN(");
                    }
                    else if (func == AggregateFunction.AVERAGE) {
                        ret.append("AVG(");
                    }
                    else if (func == AggregateFunction.SUM) {
                        ret.append("SUM(");
                    }
                    else {
                        throw new UnsupportedOperationException("" + func);
                    }
                }

                ret.append(getQualifiedAttributeIdentifier(col
                        .getAttributePath()));

                if (func != null) {
                    ret.append(")");
                }

            }

        }

        return ret;
    }

    /**
     * Constructs a locale select fragemnt for the specified query column.
     * @param query the query.
     * @param col the locale-dependent column.
     * @return the locale select fragment.
     */
    private StringBuffer makeLocaleAggregateSelect(Query query, QueryColumn col) {
        StringBuffer ret = new StringBuffer("max( concat ("
                + "substring (concat(coalesce("
                + getClassAlias(col.getAttributePath())
                + ".locale, ''), '          '),1,10),"
                + getQualifiedAttributeIdentifier(col.getAttributePath())
                + ") )");
        return ret;
    }

    /**
     * Returns the final attribute name in a potentially nested attribute path.
     * @param attributePath the attribute path.
     * @return the name of the final attribute in the path.
     */
    private String getFinalAttributeName(String attributePath) {
        String ret;
        if (isRelatedColumn(attributePath)) {
            ret = attributePath.substring(attributePath.lastIndexOf('.') + 1,
                    attributePath.length());
        }
        else {
            ret = attributePath;
        }
        return ret;
    }

    private String getQualifiedAttributeIdentifier(String attributePath) {
        return getClassAlias(attributePath) + '.'
                + getFinalAttributeName(attributePath);
    }

    private String getUnQualifiedAttributeIdentifier(String attributePath) {
        return attributePath.substring(attributePath.lastIndexOf('.') + 1);
    }

    private String getClassAlias(String attributePath) {
        String ret;
        if (isRelatedColumn(attributePath)) {
            ret = getRelationName(attributePath).replaceAll("\\.", "");
        }
        else {
            ret = "obj";
        }
        return ret;
    }

    /**
     * Determines if the requested column comes from a related entity.
     * @param col
     * @return
     */
    private boolean isRelatedColumn(String attributePath) {
        return attributePath.indexOf('.') != -1;
    }

    /**
     * Returns the FROM clause for the passed query.
     * @param query
     * @return the set of aliases that have already been placed into the left
     *         outer join clauses.
     */
    private Set < String > appendFromClause(Query query, StringBuffer buf) {

        /*
         * Go through all the columns and left outer joins as necessary.
         */
        Set < String > aliases = new HashSet < String >();
        aliases.add("obj");

        for (QueryColumn col : query.getColumns()) {
            if (isRelatedColumn(col.getAttributePath())) {
                String alias = getClassAlias(col.getAttributePath());
                if (!aliases.contains(alias)) {
                    buf.append(" LEFT OUTER JOIN");
                    buf.append(" obj."
                            + getRelationName(col.getAttributePath()));
                    buf.append(" AS " + getClassAlias(col.getAttributePath()));
                    aliases.add(alias);
                }
            }
        }

        return aliases;
    }

    /**
     * Returns the FROM clause for the passed query, but only looking at the
     * where clause.
     * @param query
     * @return
     */
    private void appendFromClauseForFilterCriteria(Query query,
            StringBuffer buf, Set < String > aliases) {

        /*
         * Go through all the columns and left outer joins as necessary.
         */

        for (FilterCriterion crit : query.getFilterCriteria()) {
            if (isRelatedColumn(crit.getAttributePath())) {
                if (!aliases.contains(getClassAlias(crit.getAttributePath()))) {
                    buf.append(" LEFT OUTER JOIN");
                    buf.append(" obj."
                            + getRelationName(crit.getAttributePath()));
                    buf.append(" AS " + getClassAlias(crit.getAttributePath()));
                }
            }
        }
    }

    /**
     * Returns the part of a compound attribute path up to but not including the
     * last dot.
     * @param col
     * @return
     */
    private String getRelationName(String attributePath) {
        return attributePath.substring(0, attributePath.lastIndexOf('.'));
    }

    private void appendWhereClause(Query query, StringBuffer buf) {

        buf.append(' ');

        if (query.getFilterCriteria().size() > 0) {
            buf.append("WHERE ");
        }

        boolean first = true;

        for (FilterCriterion crit : query.getFilterCriteria()) {

            if (!first) {
                buf.append(" AND ");
            }
            else {
                first = false;
            }

            buf
                    .append(getQualifiedAttributeIdentifier(crit
                            .getAttributePath() + ' '));

            if (crit.getOperator() == Operator.EQUALS) {
                if (crit.getValues() != null) {
                    buf.append("=");
                }
                else {
                    buf.append("IS NULL");
                }
            }
            else if (crit.getOperator() == Operator.GREATER_THAN) {
                buf.append(">");
            }
            else if (crit.getOperator() == Operator.GREATER_THAN_OR_EQUALS) {
                buf.append(">=");
            }
            else if (crit.getOperator() == Operator.LESS_THAN) {
                buf.append("<");
            }
            else if (crit.getOperator() == Operator.LESS_THAN_OR_EQUALS) {
                buf.append("<=");
            }
            else if (crit.getOperator() == Operator.NOT_EQUALS) {
                if (crit.getValues() != null) {
                    buf.append("<>");
                }
                else {
                    buf.append("IS NOT NULL");
                }
            }
            else if (crit.getOperator() == Operator.BETWEEN) {
                buf.append("BETWEEN ");
            }
            else if (crit.getOperator() == Operator.NOT_BETWEEN) {
                buf.append("NOT BETWEEN ");
            }
            else if (crit.getOperator() == Operator.IN) {
                buf.append("IN (");
            }
            else if (crit.getOperator() == Operator.NOT_IN) {
                buf.append("NOT IN (");
            }
            else if (crit.getOperator() == Operator.LIKE) {
                buf.append("LIKE ");
            }

            if (crit.getOperator() == Operator.BETWEEN
                    || crit.getOperator() == Operator.NOT_BETWEEN) {
                buf.append(valueToString(crit.getAttributePath(), crit
                        .getValues()[0]));
                buf.append(" AND ");
                buf.append(valueToString(crit.getAttributePath(), crit
                        .getValues()[1]));
            }
            else if (crit.getOperator() == Operator.IN
                    || crit.getOperator() == Operator.NOT_IN) {
                for (int i = 0; i < crit.getValues().length; i++) {
                    if (i > 0) {
                        buf.append(',');
                    }
                    buf.append(valueToString(crit.getAttributePath(), crit
                            .getValues()[i]));
                }
            }
            else {
                buf.append(valueToString(crit.getAttributePath(), crit
                        .getValues()[0]));
            }

            if (crit.getOperator() == Operator.IN
                    || crit.getOperator() == Operator.NOT_IN) {
                buf.append(')');
            }

        }

        // if any of the columns had the LOCALE aggregate function then we need
        // to select only the valid locales for the locale in the query
        appendLocaleWhereClause(query, buf);
    }

    private void appendLocaleWhereClause(Query query, StringBuffer buf) {

        boolean whereAppended = false;
        if (query.getFilterCriteria().size() > 0) {
            whereAppended = true;
        }

        Locale[] locales = LocaleUtils.getLocaleHierarchy(query.getLocale());

        for (QueryColumn col : query.getColumns()) {
            if (col.getAggregateFunction() == AggregateFunction.LOCALE) {

                if (!whereAppended) {
                    buf.append("WHERE (");
                    whereAppended = true;
                }
                else {
                    buf.append("AND (");
                }

                String qualifiedAttributeIdentifier = getQualifiedLocaleIdentifier(getQualifiedAttributeIdentifier(col
                        .getAttributePath()));

                buf.append(qualifiedAttributeIdentifier);
                buf.append(" IN(");

                int index = 0;
                for (Locale locale : locales) {
                    if (locale != null) {
                        if (index > 0) {
                            buf.append(',');
                        }
                        index++;

                        buf.append('\'');
                        buf.append(locale.toString());
                        buf.append('\'');
                    }
                }

                buf.append(") ");

                buf.append(" OR ");
                buf.append(qualifiedAttributeIdentifier);
                buf.append(" IS NULL) ");
            }
        }
    }

    private String getQualifiedLocaleIdentifier(
            String qualifiedAttributeIdentifier) {
        return qualifiedAttributeIdentifier.substring(0,
                qualifiedAttributeIdentifier.indexOf('.') + 1)
                + "locale";
    }

    private StringBuffer valueToString(String attributePath, Object object) {
        StringBuffer ret = new StringBuffer();

        if (object instanceof String
                && !("id"
                        .equals(getUnQualifiedAttributeIdentifier(attributePath))) // the id is always numeric
        ) {
            ret.append("'" + object.toString() + "'");
        }
        else {
            ret.append(object.toString());
        }

        return ret;
    }

    private void appendGroupByClause(Query query, StringBuffer buf) {
        buf.append(' ');
        /*
         * If there are any aggregate functions, then we need to group 
         * by all non-aggregated selected attributes
         */
        boolean anyAggregateFunctions = false;
        for (QueryColumn col : query.getColumns()) {
            if (col.getAggregateFunction() != null) {
                anyAggregateFunctions = true;
                break;
            }
        }

        if (anyAggregateFunctions) {
            buf.append("GROUP BY obj.id ");
            for (QueryColumn col : query.getColumns()) {
                if (col.getAggregateFunction() == null) {

                    buf.append(", ");
                    buf.append(getClassAlias(col.getAttributePath()));
                    buf.append("."
                            + getFinalAttributeName(col.getAttributePath()));

                }
            }
        }
    }

    /**
     * Appends the order by clause to the query string.
     * @param query the query.
     * @param buf the buffer containing the query string.
     */
    private void appendOrderByClause(Query query, StringBuffer buf){
		/*
		 * We'll just order by the selection columns for the moment
		 */
		buf.append(" ORDER BY ");
		
		/*
		 * FIXME - if the query has order by specified, use it.
		 */
		boolean first = true;
		
		Integer primarySortColumn = null;
		
		if (query.getSortData() != null) {
		    /*
		     * Record the index used as primary sort so that we don't include it again
		     * later.
		     * We have to add 2, since the sort column is zero-indexed, whereas
             * queries are 1-indexed, and we always select the id as an extra column
		     * to whatever the incoming query selected.
		     */
		    primarySortColumn = query.getSortData().getColumnIndex() + 2;
		    buf.append(primarySortColumn);
		    buf.append(" " + (query.getSortData().getAscending() ? "ASC" : "DESC") );
            first = false;
		}
		
		for (int i = 0; i < query.getColumns().size(); i++) {
			
		    int orderColumnIndex = i +2;

		    /*
		     * Ensure that we don't include the primary sort column again.
		     */
		    if (primarySortColumn == null || !(primarySortColumn == orderColumnIndex)) {
	            
	            if (!first){
	                buf.append(", ");
	            } else {
	                first = false;
	            }
	            
	            buf.append(orderColumnIndex);
		        
		    }
			
		}
		
		/*
		 * And we always have the id as the last sort column.
		 */
		buf.append(", 1");
		
	}

    /**
     * Sets the hibernate session factory.
     * @param sessionFactory the session factory.
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
