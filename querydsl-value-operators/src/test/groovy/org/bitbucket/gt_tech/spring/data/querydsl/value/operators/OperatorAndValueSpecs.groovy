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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators

import spock.lang.Specification
import spock.lang.Unroll

import static org.bitbucket.gt_tech.spring.data.querydsl.value.operators.Operator.*

/**
 * Specification tests OperatorAndValue
 *
 * @author gt_tech
 */
class OperatorAndValueSpecs extends Specification {

    @Unroll
    def '''it should return valid operator as '#result_operator' and value as '#result_value'
        for input '#input_value' with input operators as - #operators and default operator: #default_operator'''() {
        given:
        OperatorAndValue ov = new OperatorAndValue(input_value, operators, default_operator)
        expect:
        ov.getOperator() == result_operator
        ov.getValue() == result_value
        where:
        // test data table
        input_value                | operators         | result_operator       | result_value   | default_operator
        "search_value"             | values().toList() | EQUAL                 | "search_value" | EQUAL
        "ne(search_value)"         | values().toList() | NOT_EQUAL             | "search_value" | EQUAL
        "contains(value)"          | values().toList() | CONTAINS              | "value"        | EQUAL
        "startsWith(search_value)" | values().toList() | STARTS_WITH           | "search_value" | EQUAL
        "starts-with(search_value)" | values().toList() | STARTSWITH           | "search_value" | EQUAL
        "endsWith(search_value)"   | values().toList() | ENDS_WITH             | "search_value" | EQUAL
        "ends-with(search_value)"   | values().toList() | ENDSWITH             | "search_value" | EQUAL
        "matches(search_value)"    | values().toList() | MATCHES               | "search_value" | EQUAL
        "or(search_value)"         | values().toList() | OR                    | "search_value" | EQUAL
        "and(search_value)"        | values().toList() | AND                   | "search_value" | EQUAL
        "not(search_value)"        | values().toList() | NOT                   | "search_value" | EQUAL
        "gt(1)"                    | values().toList() | GREATER_THAN          | "1"            | EQUAL
        "gte(2)"                   | values().toList() | GREATER_THAN_OR_EQUAL | "2"            | EQUAL
        "lt(1)"                    | values().toList() | LESS_THAN             | "1"            | EQUAL
        "lte(1)"                   | values().toList() | LESS_THAN_OR_EQUAL    | "1"            | EQUAL
        // Check for composed operators
        "not(eq(abc))"             | values().toList() | NOT                   | "eq(abc)"      | OR
        //  check with incomplete operator list
        "and(eq(abc)"              | [OR, NOT]         | OR                    | "and(eq(abc)"  | OR
    }
}
