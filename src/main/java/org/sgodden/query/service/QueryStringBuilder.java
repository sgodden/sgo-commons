package org.sgodden.query.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sgodden.query.AggregateFunction;
import org.sgodden.query.CompositeRestriction;
import org.sgodden.query.LocaleUtils;
import org.sgodden.query.Query;
import org.sgodden.query.QueryColumn;
import org.sgodden.query.Restriction;
import org.sgodden.query.SimpleRestriction;
import org.sgodden.ui.models.SortData;

/**
 * Builds the HQL query string for a query.
 * 
 * @author sgodden
 */
public class QueryStringBuilder {

    private static Logger LOG = Logger.getLogger(QueryStringBuilder.class);

    /**
     * Builds a HQL query string to determine the number of matching rows of the
     * passed query.
     * 
     * @param query
     *            the query.
     * @return An HQL query string to determine the number of matching rows.
     */
    public String buildCountQuery(Query query) {
        StringBuffer buf = new StringBuffer("SELECT COUNT(distinct obj.id) ");

        buf.append(" FROM " + query.getObjectClassName() + " AS obj");
        Set<String> aliases = new HashSet<String>();
        aliases.add("obj");

        appendFromClause(query, buf);
        if (query.getFilterCriterion() != null) {
            appendFromClauseForFilterCriterion(query.getFilterCriterion(), buf,
                    aliases);
        } else {
            LOG.debug("No filter criteria specified for the query");
        }

        appendWhereClause(query, buf);

        return buf.toString();
    }

    public String buildQuery(Query query) {

        StringBuffer buf = getSelectClause(query);

        buf.append(" FROM " + query.getObjectClassName() + " AS obj");

        Set<String> aliases = appendFromClause(query, buf);

        if (query.getFilterCriterion() != null) {
            appendFromClauseForFilterCriterion(query.getFilterCriterion(), buf,
                    aliases);
        } else {
            LOG.debug("No filter criteria specified for the query");
        }

        appendWhereClause(query, buf);

        appendGroupByClause(query, buf);

        appendOrderByClause(query, buf);

        return buf.toString();
    }

    /**
     * Returns the FROM clause for the passed query.
     * 
     * @param query
     * @return the set of aliases that have already been placed into the left
     *         outer join clauses.
     */
    private Set<String> appendFromClause(Query query, StringBuffer buf) {

        /*
         * Go through all the columns and left outer joins as necessary.
         */
        Set<String> aliases = new HashSet<String>();
        aliases.add("obj");

        for (QueryColumn col : query.getColumns()) {
            if (QueryUtil.isRelatedColumn(col.getAttributePath())) {
                for (int i = 0; i < QueryUtil.getRelationDepth(col.getAttributePath()) ;i++) {
                    
                    String[] pathElements = col.getAttributePath().split("\\.");
                    String currentPath = "";
                    for (int j = 0; j <=i;j++) {
                        if (j != 0)
                            currentPath += ".";
                        currentPath += pathElements[j];
                    }
                    
                    String alias = QueryUtil.getClassAlias(currentPath);
                    if (!aliases.contains(alias)) {
                        buf.append(" LEFT OUTER JOIN");
                        buf
                                .append(" obj."
                                        + QueryUtil.getRelationName(currentPath));
                        buf.append(" AS "
                                + QueryUtil.getClassAlias(currentPath));
                        aliases.add(alias);
                    }
                }
            }
        }

        return aliases;
    }

    /**
     * Returns the FROM clause for the passed query, but only looking at the
     * where clause.
     * 
     * @param query
     * @return
     */
    private void appendFromClauseForFilterCriterion(Restriction crit,
            StringBuffer buf, Set<String> aliases) {
        if (crit instanceof SimpleRestriction) {
            appendFromClauseForSimpleFilterCriterion((SimpleRestriction) crit,
                    buf, aliases);
        } else {
            CompositeRestriction comp = (CompositeRestriction) crit;
            for (Restriction subcrit : comp.getRestrictions()) {
                if (subcrit != null)
                    appendFromClauseForFilterCriterion(subcrit, buf, aliases);
                else
                    LOG.warn("Composite Restriction where one sub-restriction is null");
            }
        }
    }

    private void appendFromClauseForSimpleFilterCriterion(
            SimpleRestriction crit, StringBuffer buf, Set<String> aliases) {
        if (QueryUtil.isRelatedColumn(crit.getAttributePath())) {
            if (!aliases.contains(QueryUtil.getClassAlias(crit
                    .getAttributePath()))) {
                buf.append(" LEFT OUTER JOIN");
                buf.append(" obj."
                        + QueryUtil.getRelationName(crit.getAttributePath()));
                buf.append(" AS "
                        + QueryUtil.getClassAlias(crit.getAttributePath()));
                // ensure we don't put this one in again
                aliases.add(QueryUtil.getClassAlias(crit.getAttributePath()));
            }
        }
    }

    private void appendGroupByClause(Query query, StringBuffer buf) {
        /*
         * If there are any aggregate functions, then we need to group by all
         * non-aggregated selected attributes
         */
        boolean anyAggregateFunctions = false;
        for (QueryColumn col : query.getColumns()) {
            if (col.getAggregateFunction() != null) {
                anyAggregateFunctions = true;
                break;
            }
        }

        if (anyAggregateFunctions) {
            buf.append(" GROUP BY obj.id ");
            for (QueryColumn col : query.getColumns()) {
                if (col.getAggregateFunction() == null) {

                    buf.append(", ");
                    buf.append(QueryUtil.getClassAlias(col.getAttributePath()));
                    buf.append("."
                            + QueryUtil.getFinalAttributeName(col
                                    .getAttributePath()));

                }
            }
        }
    }

    private void appendLocaleWhereClause(Query query, StringBuffer buf) {

        boolean whereAppended = false;
        if (query.getFilterCriterion() != null) {
            whereAppended = true;
        }

        Locale[] locales = LocaleUtils.getLocaleHierarchy(query.getLocale());

        for (QueryColumn col : query.getColumns()) {
            if (col.getAggregateFunction() == AggregateFunction.LOCALE) {

                if (!whereAppended) {
                    buf.append(" WHERE (");
                    whereAppended = true;
                } else {
                    buf.append(" AND (");
                }

                String qualifiedAttributeIdentifier = getQualifiedLocaleIdentifier(QueryUtil
                        .getQualifiedAttributeIdentifier(col.getAttributePath()));

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

    /**
     * Appends the order by clause to the query string.
     * 
     * @param query
     *            the query.
     * @param buf
     *            the buffer containing the query string.
     */
    private void appendOrderByClause(Query query, StringBuffer buf) {
        /*
         * We'll just order by the selection columns for the moment
         */
        buf.append(" ORDER BY ");
        
        StringBuffer orderByBuf = new StringBuffer();

        if (query.getSortData() != null && query.getSortData().length > 0) {
            if (query.getSortData().length == 1) {

                Integer primarySortColumn = null;

                /*
                 * FIXME - if the query has order by specified, use it.
                 */

                /*
                 * Record the index used as primary sort so that we don't
                 * include it again later. We have to add 2, since the sort
                 * column is zero-indexed, whereas queries are 1-indexed, and we
                 * always select the id as an extra column to whatever the
                 * incoming query selected.
                 */
                if (query.getSortData()[0] == null)
                    throw new IllegalStateException("Sort Datas may not be null!");
                
                primarySortColumn = query.getSortData()[0].getColumnIndex() + 2;
                LOG.debug("Primary sort column is: " + primarySortColumn);
                orderByBuf.append(" " + primarySortColumn);
                orderByBuf.append(" "
                        + (query.getSortData()[0].getAscending() ? "ASC"
                                : "DESC"));

                for (int i = 0; i < query.getColumns().size(); i++) {

                    int orderColumnIndex = i + 2;

                    /*
                     * Ensure that we don't include the primary sort column
                     * again.
                     */
                    if (primarySortColumn == null
                            || !(primarySortColumn == orderColumnIndex)) {

                        if (orderByBuf.toString().trim().length() > 0)
                            orderByBuf.append(", ");

                        orderByBuf.append(orderColumnIndex);

                    }

                }
            } else {
                for (int i = 0; i < query.getSortData().length; i++) {
                    SortData thisSort = query.getSortData()[i];
                    Integer sortColumn = thisSort.getColumnIndex() + 2;

                    LOG.debug("Adding sort column " + sortColumn);
                    orderByBuf.append(" " + sortColumn);
                    orderByBuf
                            .append(" "
                                    + (thisSort.getAscending() ? "ASC" : "DESC"));

                    if (i != query.getSortData().length - 1)
                        orderByBuf.append(", ");
                }
            }
        } else {

            for (int i = 0; i < query.getColumns().size(); i++) {

                int orderColumnIndex = i + 2;

                /*
                 * Ensure that we don't include the primary sort column again.
                 */

                if (orderByBuf.toString().trim().length() > 0)
                    orderByBuf.append(", ");

                orderByBuf.append(orderColumnIndex);

            }
        }

        /*
         * And we always have the id as the last sort column.
         */
        if (orderByBuf.toString().trim().length() > 0)
            orderByBuf.append(", 1");
        else
            orderByBuf.append("1");
        buf.append(orderByBuf.toString());

    }

    private void appendWhereClause(Query query, StringBuffer buf) {
        buf.append(new WhereClauseBuilder().buildWhereClause(query));
        // if any of the columns had the LOCALE aggregate function then we need
        // to select only the valid locales for the locale in the query
        appendLocaleWhereClause(query, buf);
    }

    private String getQualifiedLocaleIdentifier(
            String qualifiedAttributeIdentifier) {
        return qualifiedAttributeIdentifier.substring(0,
                qualifiedAttributeIdentifier.indexOf('.') + 1)
                + "locale";
    }

    /**
     * Constructs the select clause for the query.
     * 
     * @param query
     *            the query.
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
            } else {

                if (func != null) {
                    if (func == AggregateFunction.MAXIMUM) {
                        ret.append("MAX(");
                    } else if (func == AggregateFunction.MINIMUM) {
                        ret.append("MIN(");
                    } else if (func == AggregateFunction.AVERAGE) {
                        ret.append("AVG(");
                    } else if (func == AggregateFunction.SUM) {
                        ret.append("SUM(");
                    } else {
                        throw new UnsupportedOperationException("" + func);
                    }
                }

                ret.append(QueryUtil.getQualifiedAttributeIdentifier(col
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
     * 
     * @param query
     *            the query.
     * @param col
     *            the locale-dependent column.
     * @return the locale select fragment.
     */
    private StringBuffer makeLocaleAggregateSelect(Query query, QueryColumn col) {
        StringBuffer ret = new StringBuffer("max( concat ("
                + "substring (concat(coalesce("
                + QueryUtil.getClassAlias(col.getAttributePath())
                + ".locale, ''), '          '),1,10),"
                + QueryUtil.getQualifiedAttributeIdentifier(col
                        .getAttributePath()) + ") )");
        return ret;
    }

}
