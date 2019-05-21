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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators.integration.spring;

import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.experimental
        .QuerydslHttpRequestContextAwareServletFilter;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.experimental
        .QuerydslPredicateArgumentResolverBeanPostProcessor;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.integration.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring configuration for enabling experimental features from this SDK library.
 *
 * @author gt_tech
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
public class QueryDslValueOperatorsConfig {

	@Bean
	public FilterRegistrationBean querydslHttpRequestContextAwareServletFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new QuerydslHttpRequestContextAwareServletFilter(
				querydslHttpRequestContextAwareServletFilterMappings()));
		bean.setAsyncSupported(false);
		bean.setEnabled(true);
		bean.setName("querydslHttpRequestContextAwareServletFilter");
		bean.setUrlPatterns(Arrays.asList(new String[] { "/users/*" }));
		bean.setOrder(Ordered.LOWEST_PRECEDENCE);
		return bean;
	}

	private Map<String, Class<?>> querydslHttpRequestContextAwareServletFilterMappings() {
		Map<String, Class<?>> mappings = new HashMap<>();
		mappings.put("/users/search", User.class);
		return mappings;
	}

	@Bean
	public QuerydslPredicateArgumentResolverBeanPostProcessor querydslPredicateArgumentResolverBeanPostProcessor(
			QuerydslBindingsFactory factory, @Qualifier("defaultConversionService")  ConversionService conversionService) {
		return new QuerydslPredicateArgumentResolverBeanPostProcessor(factory, conversionService,
				new Class[]{Date.class, LocalDate.class, Timestamp.class, Boolean.class, boolean.class});
	}
}
