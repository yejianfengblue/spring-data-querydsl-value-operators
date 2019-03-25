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

import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Specification tests BaseExpressionProvider
 *
 * @author gt_tech
 */
class BaseExpressionProviderSpecs extends Specification {

    final def expression = Mock(BooleanExpression)

    def path = Stub(Path)

    def setup() {
        path.toString() >> 'root.this.that.property'
        path.getType() >> Some.class
    }

    @Unroll
    def "it should provide valid expression invocations for SingleValueExpressionBuilder using value - #test_value"() {
        given:
        def provider = new TestBaseExpressionProvider()
        when:
        def result = provider.getExpression(path, test_value)
        then:
        result.isPresent()
        provider.invocations == invocations
        // return same expression for verification, its ok as we aren't doing integration tests
        not * expression.not() >> expression
        and * expression.and(_) >> expression
        or * expression.or(_) >> expression
        where:
        test_value                   | invocations                                                            | not | and | or
        "eq(johnDoe)"                | ["getStringValue": "eq(johnDoe)", "eq": "johnDoe"]                     | 0   | 0   | 0
        "ne(johnDoe)"                | ["getStringValue": "ne(johnDoe)", "ne": "johnDoe"]                     | 0   | 0   | 0
        "startsWith(johnDoe)"        | ["getStringValue": "startsWith(johnDoe)", "startsWith": "johnDoe"]     | 0   | 0   | 0
        "starts-with(johnDoe)"        | ["getStringValue": "starts-with(johnDoe)", "startsWith": "johnDoe"]     | 0   | 0   | 0
        "endsWith(johnDoe)"          | ["getStringValue": "endsWith(johnDoe)", "endsWith": "johnDoe"]         | 0   | 0   | 0
        "ends-with(johnDoe)"          | ["getStringValue": "ends-with(johnDoe)", "endsWith": "johnDoe"]         | 0   | 0   | 0
        "contains(johnDoe)"          | ["getStringValue": "contains(johnDoe)", "contains": "johnDoe"]         | 0   | 0   | 0
        "matches(john*Doe)"          | ["getStringValue": "matches(john*Doe)", "matches": "john*Doe"]         | 0   | 0   | 0
        "gt(10)"                     | ["getStringValue": "gt(10)", "gt": "10"]                               | 0   | 0   | 0
        "gte(10)"                    | ["getStringValue": "gte(10)", "gte": "10"]                             | 0   | 0   | 0
        "lt(10)"                     | ["getStringValue": "lt(10)", "lt": "10"]                               | 0   | 0   | 0
        "lte(10)"                    | ["getStringValue": "lte(10)", "lte": "10"]                             | 0   | 0   | 0
        "not(startsWith(john))"      | ["getStringValue": "not(startsWith(john))", "startsWith": "john"]      | 1   | 0   | 0
        "and(not(startsWith(john)))" | ["getStringValue": "and(not(startsWith(john)))", "startsWith": "john"] | 1   | 0   | 0
        "not(startsWith(john))"      | ["getStringValue": "not(startsWith(john))", "startsWith": "john"]      | 1   | 0   | 0
        "or(endsWith(john))"         | ["getStringValue": "or(endsWith(john))", "endsWith": "john"]           | 0   | 0   | 0
    }

    def "it should throw exception on a single malformed expression value"() {
        given:
        def provider = new TestBaseExpressionProvider()
        when:
        provider.getExpression(path, "or(endsWith(john)") // missing a closing parenthesis
        then:
        thrown IllegalArgumentException
    }

    @Unroll
    def "it should provide valid expression invocations for MultiValueExpressionBuilder using value - #test_value"() {
        given:
        def provider = new TestBaseExpressionProvider()
        when:
        def result = provider.getExpression(path, test_value)
        then:
        result.isPresent()
        provider.invocations == invocations
        // return same expression for verification, its ok as we aren't doing integration tests
        not * expression.not() >> expression
        and * expression.and(_) >> expression
        or * expression.or(_) >> expression
        where:
        test_value                                                     | invocations                                                                                                                                                | not | and | or
        ["endsWith(@company.com)", "endsWith(@example.com)"]           | ["getStringValue": "endsWith(@company.com)", "endsWith": "@company.com", "getStringValue": "endsWith(@example.com)", "endsWith": "@example.com"]           | 0   | 0   | 1
        ["endsWith(@company.com)", "ends-with(@example.com)"]           | ["getStringValue": "endsWith(@company.com)", "endsWith": "@company.com", "getStringValue": "ends-with(@example.com)", "endsWith": "@example.com"]           | 0   | 0   | 1
        ["endsWith(@company.com)", "and(endsWith(@example.com))"]      | ["getStringValue": "endsWith(@company.com)", "endsWith": "@company.com", "getStringValue": "and(endsWith(@example.com))", "endsWith": "@example.com"]      | 0   | 1   | 0
        ["endsWith(@company.com)", "and(not(endsWith(@example.com)))"] | ["getStringValue": "endsWith(@company.com)", "endsWith": "@company.com", "getStringValue": "and(not(endsWith(@example.com)))", "endsWith": "@example.com"] | 1   | 1   | 0
    }

    // ============== START: Test/Stub classes ==============
    class TestBaseExpressionProvider extends BaseExpressionProvider<Path> {

        def invocations = [:]

        TestBaseExpressionProvider() {
            super(Operator.values())
        }


        @Override
        protected <S extends String> S getStringValue(Path path, Object value) {
            invocations << ["getStringValue": value]
            value
        }

        @Override
        protected BooleanExpression eq(Path path, String value, boolean ignoreCase) {
            invocations << ["eq": value]
            expression
        }

        @Override
        protected BooleanExpression ne(Path path, String value, boolean ignoreCase) {
            invocations << ["ne": value]
            expression
        }

        @Override
        protected BooleanExpression contains(Path path, String value, boolean ignoreCase) {
            invocations << ["contains": value]
            expression
        }

        @Override
        protected BooleanExpression startsWith(Path path, String value, boolean ignoreCase) {
            invocations << ["startsWith": value]
            expression
        }

        @Override
        protected BooleanExpression endsWith(Path path, String value, boolean ignoreCase) {
            invocations << ["endsWith": value]
            expression
        }

        @Override
        protected BooleanExpression matches(Path path, String value) {
            invocations << ["matches": value]
            expression
        }

        @Override
        protected BooleanExpression gt(Path path, String value) {
            invocations << ["gt": value]
            expression
        }

        @Override
        protected BooleanExpression gte(Path path, String value) {
            invocations << ["gte": value]
            expression
        }

        @Override
        protected BooleanExpression lt(Path path, String value) {
            invocations << ["lt": value]
            expression
        }

        @Override
        protected BooleanExpression lte(Path path, String value) {
            invocations << ["lte": value]
            expression
        }
    }


    static class Some {}

    // ============== STOP: Test/Stub classes ==============
}
