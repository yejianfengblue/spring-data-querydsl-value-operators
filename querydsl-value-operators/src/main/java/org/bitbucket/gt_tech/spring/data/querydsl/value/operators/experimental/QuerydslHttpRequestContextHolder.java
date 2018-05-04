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

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;

/**
 * Provides access to users of this component to persist, retrieve, unset
 * {@link QuerydslHttpRequestContext}. It delegates to a specific
 * {@link QuerydslHttpRequestContextHolderStrategy} based on runtime
 * configurations. Default strategy used is
 * {@link QuerydslHttpRequestContextHolder#MODE_THREADLOCAL} which can be used
 * by configuring JVM property -
 * {@link QuerydslHttpRequestContextHolder#SYSTEM_PROPERTY}.
 * 
 * The property can contain either of
 * {@link QuerydslHttpRequestContextHolder#MODE_THREADLOCAL} or
 * {@link QuerydslHttpRequestContextHolder#MODE_INHERITABLETHREADLOCAL} but if
 * it contains any other value, it is assumed to be a cannonical classname for
 * strategy to be used by this holder.
 * 
 * 
 * @author gt_tech
 *
 */
public final class QuerydslHttpRequestContextHolder {

	/**
	 * {@link ThreadLocal} based QuerydslHttpRequestContextHolderStrategy to be
	 * used.
	 */
	public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
	/**
	 * {@link InheritableThreadLocal} based
	 * QuerydslHttpRequestContextHolderStrategy to be used.
	 */
	public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
	/**
	 * System property to configure/override the
	 * QuerydslHttpRequestContextHolderStrategy to be used.
	 * 
	 * <p>
	 * The property can contain either of
	 * {@link QuerydslHttpRequestContextHolder#MODE_THREADLOCAL} or
	 * {@link QuerydslHttpRequestContextHolder#MODE_INHERITABLETHREADLOCAL} but
	 * if it contains any other value, it is assumed to be a cannonical
	 * classname for strategy to be used by this holder.
	 * </p>
	 */
	public static final String SYSTEM_PROPERTY = "querydsl.experimental.operator.web.context.strategy";
	private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
	private static QuerydslHttpRequestContextHolderStrategy strategy;

	static {
		initialize();
	}

	// ~ Methods
	// ========================================================================================================

	/**
	 * Explicitly clears the context value from the current thread.
	 */
	public static void clearContext() {
		strategy.clearContext();
	}

	/**
	 * Obtain the current <code>QuerydslHttpRequestContext</code>.
	 *
	 * @return the context if available, <code>null</code> otherwise
	 */
	public static QuerydslHttpRequestContext getContext() {
		return strategy.getContext();
	}

	private static void initialize() {
		if (!StringUtils.hasText(strategyName)) {
			// Set default
			strategyName = MODE_THREADLOCAL;
		}

		if (strategyName.equals(MODE_THREADLOCAL)) {
			strategy = new ThreadLocalQuerydslHttpRequestContextHolderStrategy(false);
		} else if (strategyName.equals(MODE_INHERITABLETHREADLOCAL)) {
			strategy = new ThreadLocalQuerydslHttpRequestContextHolderStrategy(true);
		} else {
			// Try to load a custom strategy
			try {
				Class<?> clazz = Class.forName(strategyName);
				Constructor<?> customStrategy = clazz.getConstructor();
				strategy = (QuerydslHttpRequestContextHolderStrategy) customStrategy.newInstance();
			} catch (Exception ex) {
				ReflectionUtils.handleReflectionException(ex);
			}
		}
	}

	/**
	 * Associates a new <code>QuerydslHttpRequestContext</code> with the holder
	 * strategy
	 *
	 * @param context
	 *            the new <code>QuerydslHttpRequestContext</code> (may not be
	 *            <code>null</code>)
	 */
	public static void setContext(QuerydslHttpRequestContext context) {
		strategy.setContext(context);
	}

	/**
	 * Changes the preferred strategy. Do <em>NOT</em> call this method more
	 * than once for a given JVM, as it will re-initialize the strategy and
	 * adversely affect any existing threads using the old strategy.
	 *
	 * @param strategyName
	 *            the fully qualified class name of the strategy that should be
	 *            used.
	 */
	public static void setStrategyName(String strategyName) {
		QuerydslHttpRequestContextHolder.strategyName = strategyName;
		initialize();
	}

	/**
	 * Allows retrieval of the context strategy.
	 *
	 * @return the configured strategy for storing the security context.
	 */
	public static QuerydslHttpRequestContextHolderStrategy getContextHolderStrategy() {
		return strategy;
	}
}
