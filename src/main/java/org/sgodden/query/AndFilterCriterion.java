/* =================================================================
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#
# ================================================================= */
package org.sgodden.query;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite filter criterion, whose member criteria will be
 * appended to the query as an 'and' filter clause.
 * @author sgodden
 *
 */
public class AndFilterCriterion implements FilterCriterion {

    /**
     * The the list of criteria to be executed in an
     * 'and' relationship.
     */
    private List < FilterCriterion > criteria = new ArrayList < FilterCriterion >();
    
    /**
     * Adds a criterion to the list that will be executed in
     * an 'and' relationship.
     * @param criterion the criterion to add.
     */
    public AndFilterCriterion and(FilterCriterion criterion) {
        criteria.add(criterion);
        return this;
    }
    
    /**
     * Returns the list of criteria to be executed in an
     * 'and' relationship.
     * @return the list of criteria.
     */
    public List < FilterCriterion > getCriteria() {
        return criteria;
    }

}
