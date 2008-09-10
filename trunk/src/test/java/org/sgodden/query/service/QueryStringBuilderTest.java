package org.sgodden.query.service;

import org.sgodden.query.AndFilterCriterion;
import org.sgodden.query.Operator;
import org.sgodden.query.OrFilterCriterion;
import org.sgodden.query.Query;
import org.sgodden.query.SimpleFilterCriterion;
import org.testng.annotations.Test;

@Test
public class QueryStringBuilderTest {
    
    /**
     * Basic test of building a query string.
     */
    public void testWhereClase() {
        Query query = new Query().setObjectClass(String.class.getName())
                // just silly example
                .addColumn("code")
                .setFilterCriterion(
                        new OrFilterCriterion()
                                .or(
                                        new AndFilterCriterion()
                                                .and(
                                                        new SimpleFilterCriterion(
                                                                "code",
                                                                Operator.EQUALS,
                                                                new Object[] { "ASDASD" }))
                                                .and(
                                                        new SimpleFilterCriterion(
                                                                "contact.code",
                                                                Operator.EQUALS,
                                                                new Object[] { "ASDASD" })))
                                .or(
                                        new AndFilterCriterion()
                                                .and(
                                                        new SimpleFilterCriterion(
                                                                "code",
                                                                Operator.EQUALS,
                                                                new Object[] { "ASDASD" }))
                                                .and(
                                                        new SimpleFilterCriterion(
                                                                "code",
                                                                Operator.EQUALS,
                                                                new Object[] { "ASDASD" }))));

        String queryString = new QueryStringBuilder().buildQuery(query);
        assert (queryString
                .equals("SELECT obj.id, obj.code FROM java.lang.String AS obj LEFT OUTER JOIN obj.contact AS contact WHERE ( (obj.code = 'ASDASD' AND contact.code = 'ASDASD' ) OR  (obj.code = 'ASDASD' AND obj.code = 'ASDASD' ) )  ORDER BY 2, 1"));
        
        queryString = new QueryStringBuilder().buildCountQuery(query);
        assert(queryString.equals("SELECT COUNT(distinct obj.id)  FROM java.lang.String AS obj LEFT OUTER JOIN obj.contact AS contact WHERE ( (obj.code = 'ASDASD' AND contact.code = 'ASDASD' ) OR  (obj.code = 'ASDASD' AND obj.code = 'ASDASD' ) )"));
    }

}