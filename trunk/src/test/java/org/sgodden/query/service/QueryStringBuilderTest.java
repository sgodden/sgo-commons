package org.sgodden.query.service;

import static org.testng.Assert.assertEquals;

import org.sgodden.query.AndRestriction;
import org.sgodden.query.Operator;
import org.sgodden.query.OrRestriction;
import org.sgodden.query.Query;
import org.sgodden.query.SimpleFilterCriterion;
import org.testng.annotations.Test;

@Test
public class QueryStringBuilderTest {

    /**
     * Basic test of building a query string.
     */
    public void testBasic() {
        Query query = new Query().setObjectClassName(String.class.getName())
                // just silly example
                .addColumn("code")
                .setFilterCriterion(
                        new OrRestriction()
                                .or(
                                        new AndRestriction()
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
                                        new AndRestriction()
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
        assertEquals(
                queryString,
                "SELECT obj.id, obj.code FROM java.lang.String AS obj LEFT OUTER JOIN obj.contact AS contact WHERE ( ( obj.code = 'ASDASD' AND contact.code = 'ASDASD' ) OR ( obj.code = 'ASDASD' AND obj.code = 'ASDASD' ) ) ORDER BY 2, 1");

        queryString = new QueryStringBuilder().buildCountQuery(query);
        assertEquals(
                queryString,
                "SELECT COUNT(distinct obj.id)  FROM java.lang.String AS obj LEFT OUTER JOIN obj.contact AS contact WHERE ( ( obj.code = 'ASDASD' AND contact.code = 'ASDASD' ) OR ( obj.code = 'ASDASD' AND obj.code = 'ASDASD' ) )");
    }

    public void testStartsWithIgnoreCase() {
        Query query = new Query().setObjectClassName(String.class.getName())
                .addColumn("code").setFilterCriterion(
                        new SimpleFilterCriterion("code",
                                Operator.STARTS_WITH_IGNORE_CASE, "AsdAsd"));

        String queryString = new QueryStringBuilder().buildQuery(query);
        assertEquals(
                queryString,
                "SELECT obj.id, obj.code FROM java.lang.String AS obj WHERE UPPER(obj.code) LIKE 'ASDASD%' ORDER BY 2, 1");

    }

    /**
     * Ensures that a null pointer does not occur when a null filter criterion
     * is passed.
     */
    public void testNullFilterCriterion() {
        Query query = new Query().setObjectClassName(String.class.getName())
                .addColumn("code");
        String queryString = new QueryStringBuilder().buildQuery(query);
        assertEquals(queryString,
                "SELECT obj.id, obj.code FROM java.lang.String AS obj ORDER BY 2, 1");
    }

}