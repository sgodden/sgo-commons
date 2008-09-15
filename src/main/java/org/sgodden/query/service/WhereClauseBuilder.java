package org.sgodden.query.service;

import org.sgodden.query.AndRestriction;
import org.sgodden.query.Restriction;
import org.sgodden.query.Operator;
import org.sgodden.query.OrRestriction;
import org.sgodden.query.Query;
import org.sgodden.query.SimpleFilterCriterion;

/**
 * Builds the where clause for the query.
 * <p>
 * This class is not thread-safe.
 * <p/>
 * @author sgodden
 *
 */
class WhereClauseBuilder {
    
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
        if (crit instanceof SimpleFilterCriterion) {
            appendSimple((SimpleFilterCriterion)crit, buf);
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
        buf.append("( ");
        boolean first = true;
        for (Restriction subcrit : crit.getRestrictions()) {
            if (!first) {
                buf.append(" OR ");
            }
            else {
                first = false;
            }
            append(subcrit, buf);
        }
        buf.append(" )");
    }
    
    private void appendAnd(AndRestriction crit, StringBuffer buf) {
        if (crit.getRestrictions().size() < 2) {
            throw new IllegalArgumentException("An and filter criterion must have at least two sub-criteria");
        }
        buf.append("( ");
        boolean first = true;
        for (Restriction subcrit : crit.getRestrictions()) {
            if (!first) {
                buf.append(" AND ");
            }
            else {
                first = false;
            }
            append(subcrit, buf);
        }
        buf.append(" )");
    }
    
    private void appendSimple(SimpleFilterCriterion crit, StringBuffer buf) {
        
        if (crit.getOperator() == Operator.STARTS_WITH_IGNORE_CASE) {
            buf.append("UPPER(");
        }

        buf.append(QueryUtil.getQualifiedAttributeIdentifier(crit
                .getAttributePath()));
        
        if (crit.getOperator() == Operator.STARTS_WITH_IGNORE_CASE) {
            buf.append(")");
        }

        switch (crit.getOperator()) {
        case CONTAINS:
            buf.append(" LIKE ");
            break;
        case ENDS_WITH:
            buf.append(" LIKE ");
            break;
        case EQUALS:
            if (crit.getValues() != null) {
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
            if (crit.getValues() != null) {
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
        case STARTS_WITH_IGNORE_CASE:
            buf.append(" LIKE ");
            break;
        default:
            throw new IllegalArgumentException("Unsupported operator: " + crit.getOperator());
        }

        if (crit.getOperator() == Operator.BETWEEN
                || crit.getOperator() == Operator.NOT_BETWEEN) {
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[0], crit.getOperator(), query.getLocale()));
            buf.append(" AND ");
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[1], crit.getOperator(), query.getLocale()));
        }
        else if (crit.getOperator() == Operator.IN
                || crit.getOperator() == Operator.NOT_IN) {
            for (int i = 0; i < crit.getValues().length; i++) {
                if (i > 0) {
                    buf.append(',');
                }
                buf.append(QueryUtil.valueToString(crit.getAttributePath(),
                        crit.getValues()[i], crit.getOperator(), query.getLocale()));
            }
        }
        else {
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[0], crit.getOperator(), query.getLocale()).toString());
        }

        if (crit.getOperator() == Operator.IN
                || crit.getOperator() == Operator.NOT_IN) {
            buf.append(')');
        }
    }

}