package org.sgodden.query;

import java.util.List;

/**
 * A filter criterion composed of multiple sub-criteria.
 * @author sgodden
 */
public interface CompositeFilterCriterion extends FilterCriterion {
    
    /**
     * Returns the list of sub-criteria.
     * @return the list of sub-criteria.
     */
    public List < FilterCriterion > getCriteria();

}
