package org.sgodden.query.service;

/**
 * Utility package for the query service.
 * @author sgodden
 *
 */
class QueryUtil {
    

    static String getUnQualifiedAttributeIdentifier(String attributePath) {
        return attributePath.substring(attributePath.lastIndexOf('.') + 1);
    }

    static String getClassAlias(String attributePath) {
        String ret;
        if (isRelatedColumn(attributePath)) {
            ret = getRelationName(attributePath).replaceAll("\\.", "");
        }
        else {
            ret = "obj";
        }
        return ret;
    }

    /**
     * Returns the final attribute name in a potentially nested attribute path.
     * @param attributePath the attribute path.
     * @return the name of the final attribute in the path.
     */
    static String getFinalAttributeName(String attributePath) {
        String ret;
        if (isRelatedColumn(attributePath)) {
            ret = attributePath.substring(attributePath.lastIndexOf('.') + 1,
                    attributePath.length());
        }
        else {
            ret = attributePath;
        }
        return ret;
    }

    /**
     * Returns the part of a compound attribute path up to but not including the
     * last dot.
     * @param col
     * @return
     */
    static String getRelationName(String attributePath) {
        return attributePath.substring(0, attributePath.lastIndexOf('.'));
    }

    static String getQualifiedAttributeIdentifier(String attributePath) {
        return getClassAlias(attributePath) + '.'
                + getFinalAttributeName(attributePath);
    }

    static StringBuffer valueToString(String attributePath, Object object) {
        StringBuffer ret = new StringBuffer();

        if (object instanceof String
                && !("id"
                        .equals(getUnQualifiedAttributeIdentifier(attributePath))) // the id is always numeric
        ) {
            ret.append("'" + object.toString() + "'");
        }
        else {
            ret.append(object.toString());
        }

        return ret;
    }

    /**
     * Determines if the requested column comes from a related entity.
     * @param col
     * @return
     */
    static boolean isRelatedColumn(String attributePath) {
        return attributePath.indexOf('.') != -1;
    }


}
