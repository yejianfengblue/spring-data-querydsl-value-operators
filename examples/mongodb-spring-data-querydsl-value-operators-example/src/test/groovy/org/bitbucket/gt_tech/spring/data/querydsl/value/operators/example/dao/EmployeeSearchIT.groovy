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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.dao

import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.Matchers.*
import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.beans.HasPropertyWithValue.*
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty
import static org.junit.Assert.assertEquals;

import java.util.List

import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.SpringApplication;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.model.Employee;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import spock.lang.Specification
import spock.lang.Unroll

import static spock.util.matcher.HamcrestSupport.that

import static org.junit.Assert.assertThat;

/**
 * Tests various employees searches by invoking actual API requests
 *
 * @author gt_tech
 */
//@RunWith(SpringRunner)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(loader = SpringBootContextLoader.class, classes = SpringApplication.class)
class EmployeeSearchIT extends Specification {

    @Autowired
    TestRestTemplate client

    @Unroll
    def "it should return valid expected search response for query - #query"() {
        given:
        def result = employees(query)
        expect:
        expected_size == result.size()
        that(result, verifiedBy)
        where:
        query                                                                                                   | expected_size | verifiedBy
        "/employees/search?userName=dgayle"                                                                     | 1             | userNamesMatcher(["dgayle"])
        "/employees/search?userName=dgayle&userName=ssmith"                                                     | 2             | userNamesMatcher(["dgayle", "ssmith"])
        "/employees/search?userName=dgayle&userName=contains(smith)"                                            | 3             | userNamesMatcher(["ssmith", "dgayle", "ksmith"])
        "/employees/search?userName=dgayle&userName=contains(smith)&userName=and(not(startsWith(k)))"           | 2             | userNamesMatcher(["ssmith", "dgayle"])
        "/employees/search?emails.address=ssmith@company.com"                                                   | 1             | userNamesMatcher(["ssmith"])
        "/employees/search?emails.address=ne(ssmith@company.com)"                                               | 3             | userNamesMatcher(["ksmith", "bsummers", "dgayle"])
        "/employees/search?emails.address=endsWith(@company.com)"                                               | 4             | userNamesMatcher(["ssmith", "ksmith", "bsummers", "dgayle"])
        "/employees/search?emails.address=endsWith(@company.com)&emails.address=and(not(endsWith(@dummy.com)))" | 3             | userNamesMatcher(["ssmith", "ksmith", "bsummers"])
        "/employees/search?emails.address=endsWith(@example.com)&emails.address=or(endsWith(@dummy.com))"       | 4             | userNamesMatcher(["ssmith", "ksmith", "bsummers", "dgayle"])
        "/employees/search?emails.address=endsWith(@company.com)&emails.address=and(endsWith(@dummy.com))"      | 1             | userNamesMatcher(["dgayle"])
        "/employees/search?emails.address=endsWith(@company.com)&jobData.department=and(SALES)"                 | 1             | userNamesMatcher(["ksmith"])
        "/employees/search?emails.address=endsWith(@company.com)&jobData.department=and(ne(SALES))"             | 3             | userNamesMatcher(["ssmith", "dgayle", "bsummers"])
        "/employees/search?emails.address=endsWith(@company.com)&status=LOCKED"                                 | 1             | userNamesMatcher(["ksmith"])
        "/employees/search?emails.address=endsWith(@company.com)&status=ne(LOCKED)"                             | 3             | userNamesMatcher(["ssmith", "dgayle", "bsummers"])
        "/employees/search?emails.address=endsWith(@company.com)&status=eq(ACTIVE)"                             | 3             | userNamesMatcher(["ssmith", "dgayle", "bsummers"])
        "/employees/search?emails.address=endsWith(@company.com)&status=ACTIVE"                                 | 3             | userNamesMatcher(["ssmith", "dgayle", "bsummers"])
        "/employees/search?emails.address=endsWith(@company.com)&profile.age=gte(41)"                           | 2             | userNamesMatcher(["dgayle", "bsummers"])
        "/employees/search?profile.age=gt(41)"                                                                  | 1             | userNamesMatcher(["bsummers"])
        "/employees/search?profile.age=lt(41)"                                                                  | 2             | userNamesMatcher(["ksmith", "ssmith"])
        "/employees/emails/dgayle@company.com"                                                                  | 1             | userNamesMatcher(["dgayle"])
    }


    def employees(def query) {
        client.exchange(query.trim(), HttpMethod.GET, null, new ParameterizedTypeReference<Resource<List<Employee>>>() {
        }).getBody().getContent()
    }

    def userNamesMatcher(Collection<String> values) {
        unorderedMatcher("userName", values)
    }

    def unorderedMatcher(String propertyName, Collection<String> values) {
        containsInAnyOrder(*(values.collect { v -> hasProperty(propertyName, is(v)) }))
    }


}
