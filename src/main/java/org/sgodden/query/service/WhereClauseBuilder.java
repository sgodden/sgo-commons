package org.sgodden.query.service;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.query.AndRestriction;
import org.sgodden.query.Restriction;
import org.sgodden.query.Operator;
import org.sgodden.query.OrRestriction;
import org.sgodden.query.Query;
import org.sgodden.query.SimpleRestriction;

/**
 * Builds the where clause for the query.
 * <p>
 * This class is not thread-safe.
 * <p/>
 * @author sgodden
 *
 */
public class WhereClauseBuilder {
    
	private final transient static Log log = LogFactory.getLog(WhereClauseBuilder.class);
	
    private Query query;
    
    public StringBuffer buildWhereClause(Query query) {
        this.query = query;
        StringBuffer buf = new StringBuffer();
        if (query.getFilterCriterion() != null) {
            buf.append(" WHERE ");
            append(query.getFilterCriterion(), buf);
        }
        return buf;
    }
    
    private void append(Restriction crit, StringBuffer buf) {
        if (crit instanceof SimpleRestriction) {
            appendSimple((SimpleRestriction)crit, buf);
        }
        else if (crit instanceof OrRestriction) {
            appendOr((OrRestriction)crit, buf);
        }
        else if (crit instanceof AndRestriction) {
            appendAnd((AndRestriction)crit, buf);
        }
    }
    
    private void appendOr(OrRestriction crit, StringBuffer buf) {
        if (crit.getRestrictions().size() < 2) {
            throw new IllegalArgumentException("An or filter criterion must have at least two sub-criteria");
        }
        StringBuffer clauseBuf = new StringBuffer();
        clauseBuf.append("( ");
        for (Restriction subcrit : crit.getRestrictions()) {
            if (!"( ".equals(clauseBuf.toString())) {
                clauseBuf.append(" OR ");
            }
            append(subcrit, clauseBuf);
        }
        clauseBuf.append(" )");
        buf.append(clauseBuf);
    }
    
    private void appendAnd(AndRestriction crit, StringBuffer buf) {
        if (crit.getRestrictions().size() < 2) {
            throw new IllegalArgumentException("An and filter criterion must have at least two sub-criteria");
        }
        StringBuffer clauseBuf = new StringBuffer();
        clauseBuf.append("( ");
        for (Restriction subcrit : crit.getRestrictions()) {
            if (!"( ".equals(clauseBuf.toString())) {
                clauseBuf.append(" AND ");
            }
            append(subcrit, clauseBuf);
        }
        clauseBuf.append(" )");
        buf.append(clauseBuf);
    }
    
    /**
     * Renders an operator into a stringbuffer
     * @param crit
     * @param buf
     */
    public static void renderOperator(SimpleRestriction crit, StringBuffer buf) {
        switch (crit.getOperator()) {
        case CONTAINS:
            buf.append(" LIKE ");
            break;
        case ENDS_WITH:
            buf.append(" LIKE ");
            break;
        case EQUALS:
            if (crit.getValues()[0] != null) {
                buf.append(" = ");
            }
            else {
                buf.append(" IS NULL");
            }
            break;
        case GREATER_THAN:
            buf.append(" > ");
            break;
        case GREATER_THAN_OR_EQUALS:
            buf.append(" >= ");
            break;
        case LESS_THAN:
            buf.append(" < ");
            break;
        case LESS_THAN_OR_EQUALS:
            buf.append(" <= ");
            break;
        case NOT_EQUALS:
            if (crit.getValues()[0] != null) {
                buf.append(" <> ");
            }
            else {
                buf.append(" IS NOT NULL ");
            }
            break;
        case BETWEEN:
            buf.append(" BETWEEN ");
            break;
        case NOT_BETWEEN:
            buf.append(" NOT BETWEEN ");
            break;
        case IN:
            buf.append(" IN (");
            break;
        case NOT_IN:
            buf.append(" NOT IN (");
            break;
        case STARTS_WITH:
            buf.append(" LIKE ");
            break;
        default:
            throw new IllegalArgumentException("Unsupported operator: " + crit.getOperator());
        }
    }
    
    /**
     * Renders a simple restriction's values into the buffer
     * @param crit
     * @param buf
     */
    public static void renderValues(SimpleRestriction crit, StringBuffer buf, Locale locale) {
        if (crit.getOperator() == Operator.BETWEEN
                || crit.getOperator() == Operator.NOT_BETWEEN) {
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[0], crit.getOperator(), locale, crit.getIgnoreCase()));
            buf.append(" AND ");
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[1], crit.getOperator(), locale, crit.getIgnoreCase()));
        }
        else if (crit.getOperator() == Operator.IN
                || crit.getOperator() == Operator.NOT_IN) {
            for (int i = 0; i < crit.getValues().length; i++) {
                if (i > 0) {
                    buf.append(',');
                }
                buf.append(QueryUtil.valueToString(crit.getAttributePath(),
                        crit.getValues()[i], crit.getOperator(), locale, crit.getIgnoreCase()));
            }
        }
        else {
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[0], crit.getOperator(), locale, crit.getIgnoreCase()).toString());
        }

        if (crit.getOperator() == Operator.IN
                || crit.getOperator() == Operator.NOT_IN) {
            buf.append(')');
        }
    }
    
    private void appendSimple(SimpleRestriction crit, StringBuffer buf) {

    	//We would never attempt to upper case a search for a null value
        if (crit.getValues()[0] !=null && crit.getIgnoreCase()) {
            buf.append("UPPER(");
        }

        buf.append(QueryUtil.getQualifiedAttributeIdentifier(crit
                .getAttributePath()));
        
        if (crit.getValues()[0] !=null &&  crit.getIgnoreCase()) {
            buf.append(")");
        }

        renderOperator(crit, buf);
        renderValues(crit, buf, query.getLocale());
    }

}
