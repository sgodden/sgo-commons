package org.sgodden.query.service;

import org.sgodden.query.AndFilterCriterion;
import org.sgodden.query.Operator;
import org.sgodden.query.OrFilterCriterion;
import org.sgodden.query.Query;
import org.sgodden.query.SimpleFilterCriterion;
import org.testng.annotations.Test;

@Test
public class WhereClauseBuilderTest {
    
    /**
     * Basic test of building a where clause containing an or and a couple of ands.
     */
    public void testWhereClase() {
        Query query = new Query();
        query.setFilterCriterion(
            new OrFilterCriterion()
                .or(new AndFilterCriterion()
                    .and(new SimpleFilterCriterion("code", Operator.EQUALS, new Object[]{"ASDASD"}))
                    .and(new SimpleFilterCriterion("contact.code", Operator.EQUALS, new Object[]{"ASDASD"}))
                )
                .or(new AndFilterCriterion()
                    .and(new SimpleFilterCriterion("code", Operator.EQUALS, new Object[]{"ASDASD"}))
                    .and(new SimpleFilterCriterion("code", Operator.EQUALS, new Object[]{"ASDASD"}))
                )
            );

        StringBuffer sb = new WhereClauseBuilder().buildWhereClause(query);
        
        assert(sb.toString().equals("WHERE ( (obj.code = 'ASDASD' AND contact.code = 'ASDASD' ) OR  (obj.code = 'ASDASD' AND obj.code = 'ASDASD' ) )"));
    }

}
