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
package org.sgodden.query;

import java.io.Serializable;

/**
 * A single criterion in the filter of a query.
 * @author sgodden
 */
public class SimpleFilterCriterion implements Serializable, FilterCriterion {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 20080909L;

    private String attributePath;
    private Operator operator;
    private Object[] values;

    /**
     * Constructs a new empty filter criterion.
     */
    public SimpleFilterCriterion() {
    }

    /**
     * Constructs a new filter criterion, initialised with the passed values.
     * @param attributePath the path of the attribute on which filtering will
     *            occur.
     * @param operator the operator to apply.
     * @param values the value(s) by which to filter.
     */
    public SimpleFilterCriterion(String attributePath, Operator operator,
            Object[] values) {
        this.attributePath = attributePath;
        this.operator = operator;
        this.values = values;
    }

    /**
     * Returns the path of the attribute on which filtering will occur.
     * @return
     */
    public String getAttributePath() {
        return attributePath;
    }

    /**
     * @see #getAttributePath()
     * @param attributePath the attribute path on which to filter.
     */
    public void setAttributePath(String attributePath) {
        this.attributePath = attributePath;
    }

    /**
     * Returns the operator of the filter criterion.
     * @return the operator.
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * @see #getOperator()
     * @param operator the operator to apply.
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /**
     * Returns the value(s) by which to filter.
     * @return the values.
     */
    public Object[] getValues() {
        return values;
    }

    /**
     * @see #getValues()
     * @param values the values by which to filter.
     */
    public void setValues(Object[] values) {
        this.values = values;
    }

    /**
     * Convenience method when filtering by a single value.
     * @param value the value by which to filter.
     * @see #setValues(Object[])
     */
    public void setValue(Object value) {
        setValues(new Object[] { value });
    }

}
