/*******************************************************************************
 * Copyright (c) 2018 @gt_tech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators;

import org.apache.commons.lang.StringUtils;

/**
 * Defines the value-operators supported by this component.
 * @author gt_tech
 *
 */
public enum Operator {

    // Value comparison operators
    /**
     * Provides a operator value delimiter prefix for when explicit delimiter is provided.
     * for e.g. operator(value)
     */
    EQUAL("eq"),
    /**
     * Provides a operator value delimiter suffix for when explicit delimiter is provided.
     * for e.g. operator(value)
     */
    NOT_EQUAL("ne"),
    /**
     * The operator for contains clause.
     * This operator should perform case insensitive searches when possible.
     */
    CONTAINS("contains"),
    /**
     * The operator for starts-with clause.
     * This operator should perform case insensitive searches when possible.
     */
    STARTS_WITH("startsWith"),
    /**
     * The operator for ends-with clause.
     * This operator should perform case insensitive searches when possible.
     */
    ENDS_WITH("endsWith"),
    /**
     * The operator for regular expression clause.
     * This operator should perform case insensitive searches when possible.
     */
    MATCHES("matches"),

    // Logical Operators
    /**
     * Logical AND operator in case of multiple values are provided for same search parameter.
     * This attribute doesn't apply on single valued search attributes.
     */
    AND("and"),
    /**
     * Logical OR operator in case of multiple values are provided for same search parameter.
     * This attribute doesn't apply on single valued search attributes.This is default operator for multi-valued
     * search parameters if no explicit multi-valued ({@link #AND} or {@link #OR})
     * operator is defined.
     */
    OR("or"),
    /**
     * Logical NOT operator which can be used to negate the result of any search parameter/logic.
     */
    NOT("not"),
    /**
     * Greater than operator primarily to be used for numeric values.
     */
    GREATER_THAN("gt"),
    /**
     * Greater than or equal operator primarily to be used for numeric values.
     */
    GREATER_THAN_OR_EQUAL("gte"),
    /**
     * Less than operator primarily to be used for numeric values
     */
    LESS_THAN("lt"),
    /**
     * Less than or equal operator primarily to be used for numeric values
     */
    LESS_THAN_OR_EQUAL("lte"),
    /**
     * Unary operator to indicate a particular other value operator must execute its logic by ignoring case.
     */
    CASE_IGNORE("ci");


    private String value;

    Operator(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * @param text String form of Operator
     * @return Returns a valid Operator from provided text value if available, <code>null</code> otherwise.
     */
    public static Operator fromValue(String text) {
        if (StringUtils.isNotBlank(text)) {
            for (Operator b : Operator.values()) {
                if (String.valueOf(b.value)
                          .equalsIgnoreCase(text)) {
                    return b;
                }
            }
        }
        return null;
    }
}
