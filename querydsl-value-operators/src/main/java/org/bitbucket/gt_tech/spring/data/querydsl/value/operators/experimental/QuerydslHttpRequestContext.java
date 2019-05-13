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

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.ExpressionProvider;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.ExpressionProviderFactory;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.Operator;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.OperatorAndValue;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Context is core to experimental features using
 * {@link QuerydslHttpRequestContextAwareServletFilter} as it decorates the
 * default {@link HttpServletRequest} by extracting all the value operators and
 * maintaining them locally against their respective {@link Path} in this
 * context. This allows Spring Web {@link QuerydslPredicateArgumentResolver} to
 * continue with its strong type-conversion hiding the fact that there were
 * value operators. Value operators enabled actual value is provided to
 * {@link ExpressionProvider} during later phases for their usage in actual
 * {@link Expression} for querying.
 * 
 * @author gt_tech
 *
 */
public final class QuerydslHttpRequestContext {

	private final EntityPath<?> root;
	private final HttpServletRequest servletRequest;
	private final Map<String, Collection<String>> original_parameters = new LinkedHashMap<>();
	private final Map<String, Collection<String>> transformed_parameters;

	/**
	 * Constructor
	 * 
	 * <p>
	 * This constructor extracts the original request parameters and also
	 * creates a local transformed parameter list devoid of any value operators
	 * this component supports.
	 * </p>
	 * 
	 * @param root
	 *            Root {@link EntityPath} for this context
	 * @param servletRequest
	 *            {@link HttpServletRequest}
	 */
	public QuerydslHttpRequestContext(EntityPath<?> root, HttpServletRequest servletRequest) {
		Validate.notNull(root, "EntityPath must not be null");
		Validate.notNull(servletRequest, "HttpServletRequest must not be null");
		this.root = root;
		this.servletRequest = servletRequest;

		this.servletRequest.getParameterMap()
				.keySet()
				.stream()
				.forEach(k -> original_parameters.put(String.valueOf(k),
						Arrays.asList(this.servletRequest.getParameterValues(String.valueOf(k)))));

		transformed_parameters = this.original_parameters.keySet()
				.stream()
				.collect(Collectors.toMap(key -> key, key -> original_parameters.get(key)
						.stream()
						.map(s -> extractTrueValue(s))
						.collect(Collectors.toList()), (e1, e2) -> e1, LinkedHashMap::new));
	}

	/**
	 * @return decorated {@link HttpServletRequest} object containing search
	 *         request parameters devoid of any value operators.
	 */
	HttpServletRequest getWrappedHttpServletRequest() {
		if (this.transformed_parameters == null || this.transformed_parameters.size() <= 0) {
			return getOriginalHttpServletRequest();
		}
		return new HttpServletRequestWrapper(this.servletRequest) {
			@Override
			public String getParameter(String name) {
				Collection<String> values = getParameterValuesAsList(name);
				if (CollectionUtils.isNotEmpty(values)) {
					return values.iterator()
							.next();
				}
				return super.getParameter(name);
			}

			@Override
			public Map<String, String[]> getParameterMap() {
				return transformed_parameters.keySet()
						.stream()
						.collect(Collectors.toMap(key -> key, key -> transformed_parameters.get(key)
								.toArray(new String[transformed_parameters.get(key)
										.size()]),
								(e1, e2) -> e1, LinkedHashMap::new));

			}

			@Override
			public Enumeration<String> getParameterNames() {
				return super.getParameterNames();
			}

			@Override
			public String[] getParameterValues(String name) {
				Collection<String> values = getParameterValuesAsList(name);
				return values.toArray(new String[values.size()]);
			}

			private Collection<String> getParameterValuesAsList(String name) {
				Validate.notNull(name, "Parameter name must not be blank");
				Collection<String> result = transformed_parameters.get(name);
				return result != null ? result : new ArrayList<>(0);
			}
		};
	}

	/**
	 * @return original {@link HttpServletRequest} containing inputs from client
	 *         request.
	 */
	HttpServletRequest getOriginalHttpServletRequest() {
		return this.servletRequest;
	}

	/**
	 * @param inPath
	 *            {@link Path} for which original search request single value is
	 *            required.
	 * @return Original value from original HttpServletRequest for given
	 *         {@link Path} if available, <code>null</code> otherwise
	 */
	public String getSingleValue(Path inPath) {
		return Optional.ofNullable(Optional.ofNullable(inPath)
				.map(p -> this.servletRequest.getParameter(findRequestParameterNameFromPath(inPath)))
				.orElseGet(() -> this.servletRequest.getParameter(inPath.toString())))
				.orElseGet(() -> ExpressionProviderFactory.findAlias(inPath)
						.map(s -> this.servletRequest.getParameter(s))
						.orElseGet(() -> null));

	}

	/**
	 * @param inPath
	 *            {@link Path} for which original search request all values are
	 *            required.
	 * @return Original values as {@link Array} of String from original
	 *         HttpServletRequest for given {@link Path} if available,
	 *         <code>null</code> otherwise
	 */
	public String[] getAllValues(Path inPath) {
		return Optional.ofNullable(Optional.ofNullable(inPath)
				.map(p -> this.servletRequest.getParameterValues(findRequestParameterNameFromPath(inPath)))
				.orElseGet(() -> this.servletRequest.getParameterValues(inPath.toString())))
				.orElseGet(() -> ExpressionProviderFactory.findAlias(inPath)
						.map(s -> this.servletRequest.getParameterValues(s))
						.orElseGet(() -> null));

	}

	/*
	 * Internal utility function to create actual search parameter name in
	 * request originating from request since provided path starts from root.
	 * notation unlike request parameter path which starts after root.
	 */
	private String findRequestParameterNameFromPath(Path inPath) {
		Validate.notNull(inPath, "Input path must not be null to lookup original request parameter value");
		String name = null;
		Validate.isTrue(inPath.getRoot()
				.getType()
				.equals(this.root.getType()), "Mismatch in type root in path and current context");
		return StringUtils.replace(inPath.toString(), this.root + ".", StringUtils.EMPTY, 1);
	}

	/*
	 * Utility function that strips the provided input from all operators and
	 * returns true value of input
	 */
	private String extractTrueValue(String input) {
		if (StringUtils.isNotBlank(input)) {
			while (true) {
				if (ExpressionProvider.isOperator(input)
						.isPresent()) {
					return extractTrueValue(
							new OperatorAndValue(input, Arrays.asList(Operator.values()), null).getValue());
				} else {
					return input;
				}
			}
		}
		return StringUtils.EMPTY;
	}
}
