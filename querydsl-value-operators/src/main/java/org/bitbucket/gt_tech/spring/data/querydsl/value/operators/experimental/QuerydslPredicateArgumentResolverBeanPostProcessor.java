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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators.experimental;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.ExpressionProviderFactory;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.Operator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

/**
 * Advanced experimental feature of this component - an implementation of
 * {@link BeanPostProcessor} that overrides the default
 * {@link QuerydslPredicateArgumentResolver} by providing it a no-op
 * {@link ConversionService} and in-turn disabling it's strong type-conversion.
 * This allows for String values decorated with value-operators by client to
 * reach expression-provided even for non StringPath.
 *
 * <p>
 *     Note that by providing a delegate ConversionService and explicit Class
 *     types for delegated conversions, a high degree of control can be achieved
 *     when users are using direct bindings for certain fields (for e.g. Date)
 * </p>
 * 
 * <p>
 * If this isn't available, then the {@link QuerydslPredicateArgumentResolver}
 * will attempt to perform type-conversion which will fail for non-StringPath
 * (for. e.g. EnumPath) when values are decorated with value-operators -
 * {@link Operator}
 * </p>
 * 
 * @author gt_tech
 *
 */
public class QuerydslPredicateArgumentResolverBeanPostProcessor implements BeanPostProcessor {

	private final QuerydslBindingsFactory querydslBindingsFactory;

	private final ConversionService conversionServiceDelegate;

	private final Class[] delegatedConversions;

	/*
	 * No-op conversion service
	 */
	private final ConversionService delegationAwareConversionService = new ConversionService() {

		@Override
		public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
			if ( isDelegatedConversion(sourceType) || isDelegatedConversion(targetType))
				return conversionServiceDelegate.canConvert(sourceType, targetType);

			return false;
		}

		@Override
		public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
			if ( isDelegatedConversion(sourceType.getType()) || isDelegatedConversion(targetType.getType()))
				return conversionServiceDelegate.canConvert(sourceType, targetType);

			return false;
		}

		@Override
		public <T> T convert(Object source, Class<T> targetType) {

			if ( isDelegatedConversion(source.getClass()) || isDelegatedConversion(targetType))
				return conversionServiceDelegate.convert(source, targetType);

			throw new UnsupportedOperationException("Overridden ConversionService in "
					+ "QuerydslPredicateArgumentResolver does not " + "support conversion");
		}

		@Override
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

			if ( isDelegatedConversion(sourceType.getType()) || isDelegatedConversion(targetType.getType()))
				return conversionServiceDelegate.convert(source, sourceType, targetType);

			throw new UnsupportedOperationException("Overridden ConversionService in "
					+ "QuerydslPredicateArgumentResolver does not " + "support conversion");
		}

		private boolean isDelegatedConversion(Class<?> type) {
			boolean result = false;

			if ( conversionServiceDelegate != null && type != null && delegatedConversions != null ) {
				for (Class c : delegatedConversions) {
					if (c.equals(type)) {
						result = true;
						break;
					}
				}
			}
			return result;
		}
	};

	/**
	 * Constructor: Replaces {@link QuerydslPredicateArgumentResolver} with a no-op conversion service
	 * @param querydslBindingsFactory
	 */
	public QuerydslPredicateArgumentResolverBeanPostProcessor(QuerydslBindingsFactory querydslBindingsFactory) {
		this(querydslBindingsFactory, null, new Class[]{});
	}


	/**
	 * Constructor: Replaces {@link QuerydslPredicateArgumentResolver} with a no-op conversion service with the exception of following types
	 * conversion that would be handed over to provided delegated service - {@link Date}, {@link LocalDate},
	 * {@link Timestamp}
	 *
	 * @param querydslBindingsFactory
	 */
	public QuerydslPredicateArgumentResolverBeanPostProcessor(QuerydslBindingsFactory querydslBindingsFactory, ConversionService conversionServiceDelegate) {
		this(querydslBindingsFactory, conversionServiceDelegate, new Class[]{Date.class, LocalDate.class, Timestamp.class});
	}

	public QuerydslPredicateArgumentResolverBeanPostProcessor(QuerydslBindingsFactory querydslBindingsFactory, ConversionService conversionServiceDelegate, Class[] delegatedConversions) {
		Validate.notNull(querydslBindingsFactory, "QuerydslBindingsFactory must not be null");
		this.querydslBindingsFactory = querydslBindingsFactory;
		this.conversionServiceDelegate = conversionServiceDelegate;
		this.delegatedConversions = delegatedConversions;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Object target = bean;
		if (target != null && QuerydslPredicateArgumentResolver.class.isAssignableFrom(target.getClass())) {
			ExpressionProviderFactory.setSupportsUnTypedValues(true);
			try {
				try {
					// Spring Boot 2.x
					return ConstructorUtils.invokeConstructor(QuerydslPredicateArgumentResolver.class,
							new Object[] { querydslBindingsFactory, Optional.of(delegationAwareConversionService) });
				} catch (NoSuchMethodException | NoSuchMethodError e) {
					// Spring boot 1.5.x
					return ConstructorUtils.invokeConstructor(QuerydslPredicateArgumentResolver.class,
							new Object[] { querydslBindingsFactory, delegationAwareConversionService });
				}
			} catch (Throwable t) {
				// phew
				throw new RuntimeException("Failed to post-process QuerydslPredicateArgumentResolver", t);
			}
		}
		return target;
	}

	/**
	 * Implementing default method as-is since Spring Boot 1.5.x specific
	 * dependencies don't have default methods so if library users use this with
	 * an older spring, the runtime would fail. This is implemented as a
	 * fail-safe mechanism.
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
