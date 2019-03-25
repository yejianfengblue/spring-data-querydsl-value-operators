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

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Implementation of {@link BaseExpressionProvider} for supporting
 * {@link StringPath}
 * 
 * @author gt_tech
 *
 */
class StringPathExpressionProviderImpl extends BaseExpressionProvider<StringPath> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringPathExpressionProviderImpl.class);

	public StringPathExpressionProviderImpl() {
		super(Arrays.asList(Operator.EQUAL, Operator.NOT_EQUAL, Operator.CONTAINS, Operator.STARTS_WITH, Operator.STARTSWITH,
				Operator.ENDS_WITH, Operator.ENDSWITH, Operator.NOT, Operator.MATCHES, Operator.CASE_IGNORE));
	}

	@Override
	protected <S extends String> S getStringValue(StringPath path, Object value) {
		return (S) value.toString();
	}

	@Override
	protected BooleanExpression eq(StringPath path, String value, boolean ignoreCase) {
		return ignoreCase ? path.equalsIgnoreCase(value): path.eq(value);
	}

	@Override
	protected BooleanExpression ne(StringPath path, String value, boolean ignoreCase) {
		return ignoreCase ? path.notEqualsIgnoreCase(value): path.ne(value);
	}

	@Override
	protected BooleanExpression contains(StringPath path, String value, boolean ignoreCase) {
		return ignoreCase ? path.containsIgnoreCase(value) : path.contains(value);
	}

	@Override
	protected BooleanExpression startsWith(StringPath path, String value, boolean ignoreCase) {
		return ignoreCase ? path.startsWithIgnoreCase(value) : path.startsWith(value);
	}

	@Override
	protected BooleanExpression endsWith(StringPath path, String value, boolean ignoreCase) {
		return ignoreCase ? path.endsWithIgnoreCase(value) : path.endsWith(value);
	}

	@Override
	protected BooleanExpression matches(StringPath path, String value) {
		return path.matches(value);
	}

	@Override
	protected BooleanExpression gt(StringPath path, String value) {
		throw new UnsupportedOperationException("String value can't be searched using gt operator");
	}

	@Override
	protected BooleanExpression gte(StringPath path, String value) {
		throw new UnsupportedOperationException("String value can't be searched using gte operator");
	}

	@Override
	protected BooleanExpression lt(StringPath path, String value) {
		throw new UnsupportedOperationException("String value can't be searched using lt operator");
	}

	@Override
	protected BooleanExpression lte(StringPath path, String value) {
		throw new UnsupportedOperationException("String value can't be searched using lte operator");
	}
}
