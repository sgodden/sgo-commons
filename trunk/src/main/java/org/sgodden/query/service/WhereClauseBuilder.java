package org.sgodden.query.service;

import org.sgodden.query.AndFilterCriterion;
import org.sgodden.query.FilterCriterion;
import org.sgodden.query.Operator;
import org.sgodden.query.OrFilterCriterion;
import org.sgodden.query.Query;
import org.sgodden.query.SimpleFilterCriterion;

/**
 * Builds the where clause for the query.
 * @author sgodden
 *
 */
class WhereClauseBuilder {
    
    public StringBuffer buildWhereClause(Query query) {
        StringBuffer buf = new StringBuffer();
        if (query.getFilterCriterion() != null) {
            buf.append("WHERE");
            append(query.getFilterCriterion(), buf);
        }
        return buf;
    }
    
    private void append(FilterCriterion crit, StringBuffer buf) {
        if (crit instanceof SimpleFilterCriterion) {
            appendSimple((SimpleFilterCriterion)crit, buf);
        }
        else if (crit instanceof OrFilterCriterion) {
            appendOr((OrFilterCriterion)crit, buf);
        }
        else if (crit instanceof AndFilterCriterion) {
            appendAnd((AndFilterCriterion)crit, buf);
        }
    }
    
    private void appendOr(OrFilterCriterion crit, StringBuffer buf) {
        if (crit.getCriteria().size() < 2) {
            throw new IllegalArgumentException("An or filter criterion must have at least two sub-criteria");
        }
        buf.append(" (");
        boolean first = true;
        for (FilterCriterion subcrit : crit.getCriteria()) {
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
    
    private void appendAnd(AndFilterCriterion crit, StringBuffer buf) {
        if (crit.getCriteria().size() < 2) {
            throw new IllegalArgumentException("An and filter criterion must have at least two sub-criteria");
        }
        buf.append(" (");
        boolean first = true;
        for (FilterCriterion subcrit : crit.getCriteria()) {
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

        buf.append(QueryUtil.getQualifiedAttributeIdentifier(crit
                .getAttributePath()));

        switch (crit.getOperator()) {
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
        case LIKE:
            buf.append(" LIKE ");
            break;
        }

        if (crit.getOperator() == Operator.BETWEEN
                || crit.getOperator() == Operator.NOT_BETWEEN) {
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[0]));
            buf.append(" AND ");
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[1]));
        }
        else if (crit.getOperator() == Operator.IN
                || crit.getOperator() == Operator.NOT_IN) {
            for (int i = 0; i < crit.getValues().length; i++) {
                if (i > 0) {
                    buf.append(',');
                }
                buf.append(QueryUtil.valueToString(crit.getAttributePath(),
                        crit.getValues()[i]));
            }
        }
        else {
            buf.append(QueryUtil.valueToString(crit.getAttributePath(), crit
                    .getValues()[0]));
        }

        if (crit.getOperator() == Operator.IN
                || crit.getOperator() == Operator.NOT_IN) {
            buf.append(')');
        }
    }

}
