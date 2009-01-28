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

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.type.CalendarType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.sgodden.query.AggregateFunction;
import org.sgodden.query.DataType;
import org.sgodden.query.ObjectUtils;
import org.sgodden.query.Query;
import org.sgodden.query.QueryColumn;
import org.sgodden.query.ResultSet;
import org.sgodden.query.ResultSetColumn;
import org.sgodden.query.ResultSetRow;

import com.google.inject.Provider;

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
     * The provider of hibernate sessions.
     */
    private Provider < Session > sessionProvider;

    /**
     * See
     * {@link org.sgodden.query.service.QueryService#executeQuery(org.sgodden.query.Query}.
     * @param query the query to execute.
     */
    @SuppressWarnings("unchecked")
    public ResultSet executeQuery(Query query) {
    	
    	if (sessionProvider == null) {
    		throw new NullPointerException("The session provider is null - did you forget to set it?");
    	}

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

        String queryString = new QueryStringBuilder().buildQuery(query);
        log.debug(queryString);
        
        if (query.getFetchSize() > 0 && query.getMaxRows() > 0) {
            throw new IllegalArgumentException(
                    "Setting fetch size and max rows is contradictory");
        }

        org.hibernate.Query hq = sessionProvider.get()
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
                        .getObjectClassName(), queryCol.getAttributePath(),
                        sessionProvider.get().getSessionFactory());

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
                    log.warn("Unknown type: " + propertyType.getClass().getName());
                    col.setDataType(DataType.UNKNOWN);
                    if (value != null) {
                        Serializable s = (Serializable)value;
                        col.setValue(s);
                    }
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
        
        String queryString = new QueryStringBuilder().buildCountQuery(query);

        log.debug("Calculating total rows with query: " + queryString);
        org.hibernate.Query hq = sessionProvider.get()
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

    public void setSessionProvider(Provider < Session > sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

}
