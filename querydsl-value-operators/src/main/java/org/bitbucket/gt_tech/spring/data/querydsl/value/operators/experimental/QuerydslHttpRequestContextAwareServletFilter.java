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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.EnumPath;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.ExpressionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * Advanced experimental feature which allows request values to be stripped of
 * their value-operators and thus allowing
 * {@link QuerydslPredicateArgumentResolver} to perform type-conversion on
 * non-String paths. Original values decorated with value operator are managed
 * in a {@link QuerydslHttpRequestContext} which is available to
 * {@link ExpressionProvider} thru {@link QuerydslHttpRequestContextHolder}
 * though that's dependent on threading. See -
 * {@link QuerydslHttpRequestContextHolderStrategy}.
 * 
 * <p>
 * Note that this will still require request to only contain certain
 * value-operators, as the true value of search input (which is value wrapped
 * within operators) must still suffice type-conversion needs. For example, if
 * an {@link EnumPath} can have values - LOCKED, ACTIVE, the search input can
 * have ne(LOCKED) but can not have startsWith(LOC) as latter would fail
 * type-conversion. For advanced needs to support maximum possible range of
 * value-operators on individual path, it may be desired to disable the
 * type-conversion of these search input values which can be accomplished using
 * {@link QuerydslPredicateArgumentResolverBeanPostProcessor} in which case this
 * filter can be disabled by consuming application.
 * </p>
 * 
 * @author gt_tech
 *
 */
public class QuerydslHttpRequestContextAwareServletFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(QuerydslHttpRequestContextAwareServletFilter.class);

	private static final EntityPathResolver entityPathResolver = SimpleEntityPathResolver.INSTANCE;

	Map<String, Class<?>> URI_SEARCH_RESOURCE_TYPE_MAPPINGS = new TreeMap(new Comparator<String>() {
		@Override
		public int compare(String s1, String s2) {
			if (s1.length() > s2.length()) {
				return -1;
			} else if (s1.length() < s2.length()) {
				return 1;
			} else {
				/*
				 * Equal length's so lexicographically comparison
				 */
				return s1.compareTo(s2);
			}
		}
	});

	static LoadingCache<Class<?>, EntityPath<?>> loadingCache = CacheBuilder.newBuilder()
			.build(new CacheLoader<Class<?>, EntityPath<?>>() {
				@Override
				public EntityPath<?> load(Class<?> domainClass) throws Exception {
					return entityPathResolver.createPath(domainClass);
				}
			});

	/**
	 * Constructor
	 * 
	 * @param URI_SEARCH_RESOURCE_TYPE_MAPPINGS
	 *            - A mapping of URI to corresponding search resource class.
	 */
	public QuerydslHttpRequestContextAwareServletFilter(Map<String, Class<?>> URI_SEARCH_RESOURCE_TYPE_MAPPINGS) {
		if (URI_SEARCH_RESOURCE_TYPE_MAPPINGS != null) {
			this.URI_SEARCH_RESOURCE_TYPE_MAPPINGS.putAll(URI_SEARCH_RESOURCE_TYPE_MAPPINGS);
		}

		try {
			loadingCache.getAll(this.URI_SEARCH_RESOURCE_TYPE_MAPPINGS.values());
		} catch (ExecutionException ex) {
			throw new RuntimeException("Failed to instantiate filter, possible mis-configurations?", ex); // TODO:
																											// to
																											// have
																											// library
																											// specific
																											// exceptions.
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			HttpServletRequest req = (HttpServletRequest) request;
			final String request_uri = req.getRequestURI();

			Optional<EntityPath<?>> optionalPath = URI_SEARCH_RESOURCE_TYPE_MAPPINGS.keySet()
					.stream()
					.filter(k -> k.equalsIgnoreCase(request_uri))
					.findFirst()
					.map(k -> URI_SEARCH_RESOURCE_TYPE_MAPPINGS.get(k))
					.map(k -> {
						try {
							return loadingCache.get(k);
						} catch (Exception ex) {
							throw new RuntimeException("Failed to load Path for " + "request uri: " + request_uri);
						}
					});

			if (optionalPath.isPresent()) {
				logger.debug("Processing {} on URI: {} for EntityPath: {}",
						new Object[] { QuerydslHttpRequestContext.class, request_uri, optionalPath.get()
								.getClass()
								.getCanonicalName() });
				QuerydslHttpRequestContext context = new QuerydslHttpRequestContext(optionalPath.get(), req);
				QuerydslHttpRequestContextHolder.setContext(context);
				chain.doFilter(context.getWrappedHttpServletRequest(), response);
			} else {
				logger.error(
						"No EntityPath found on request_uri: {}, bad filter configurations (check filter url pattern and also the injected mappings), filter is turning into a no-op for this request",
						request_uri);
				chain.doFilter(req, response);
			}
		} finally {
			QuerydslHttpRequestContextHolder.clearContext();
		}
	}

	@Override
	public void destroy() {

	}
}
