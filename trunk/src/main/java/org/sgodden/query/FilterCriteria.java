package org.sgodden.query;

/**
 * Provides a DSL-like API for creating filter criteria.
 * @author sgodden
 */
public class FilterCriteria {
    
    public SimpleFilterCriterion criterion(String attributePath, Operator operator,
            Object[] values) {
        return new SimpleFilterCriterion(attributePath, operator, values);
    }
    
    public OrFilterCriterion or() {
        return new OrFilterCriterion();
    }

}
