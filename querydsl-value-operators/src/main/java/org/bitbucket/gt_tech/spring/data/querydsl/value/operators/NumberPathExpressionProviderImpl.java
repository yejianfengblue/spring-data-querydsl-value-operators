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

import com.querydsl.core.Tuple;
import com.querydsl.core.support.NumberConversions;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Implementation of {@link BaseExpressionProvider} for supporting
 * {@link NumberPath}
 *
 * @author gt_tech
 */
class NumberPathExpressionProviderImpl extends BaseExpressionProvider<NumberPath> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NumberPathExpressionProviderImpl.class);

	public NumberPathExpressionProviderImpl() {
		super(Arrays.asList(Operator.EQUAL, Operator.NOT_EQUAL, Operator.GREATER_THAN, Operator.GREATER_THAN_OR_EQUAL,
				Operator.LESS_THAN, Operator.NOT, Operator.LESS_THAN_OR_EQUAL));
	}

	@Override protected <S extends String> S getStringValue(NumberPath path, Object value) {
		return (S) String.valueOf(value);
	}

	@Override protected BooleanExpression eq(NumberPath path, String value, boolean ignoreCase) {
		Validate.isTrue(StringUtils.isNumeric(value), "Invalid numeric value");
		//return path.eq(Integer.parseInt(StringUtils.trim(value)));
		return path.eq(new NumberConversions<Tuple>(Projections.tuple(path)).newInstance(
				NumberUtils.createNumber(StringUtils.trim(value))).get(path));
	}

	@Override protected BooleanExpression ne(NumberPath path, String value, boolean ignoreCase) {
		Validate.isTrue(StringUtils.isNumeric(value), "Invalid numeric value");
		//return path.ne(Integer.parseInt(StringUtils.trim(value)));
		return path.ne(new NumberConversions<Tuple>(Projections.tuple(path)).newInstance(
				NumberUtils.createNumber(StringUtils.trim(value))).get(path));
	}

	@Override protected BooleanExpression contains(NumberPath path, String value, boolean ignoreCase) {
		throw new UnsupportedOperationException("Number can't be searched using contains operator");
	}

	@Override protected BooleanExpression startsWith(NumberPath path, String value, boolean ignoreCase) {
		throw new UnsupportedOperationException("Number can't be searched using startsWith operator");
	}

	@Override protected BooleanExpression endsWith(NumberPath path, String value, boolean ignoreCase) {
		throw new UnsupportedOperationException("Number can't be searched using endsWith operator");
	}

	@Override protected BooleanExpression matches(NumberPath path, String value) {
		throw new UnsupportedOperationException("Number can't be searched using matches operator");
	}

	@Override protected BooleanExpression gt(NumberPath path, String value) {
		Validate.isTrue(StringUtils.isNumeric(value), "Invalid numeric value");
		//return path.gt(Integer.parseInt(StringUtils.trim(value)));
		return path.gt((Number) new NumberConversions<Tuple>(Projections.tuple(path)).newInstance(
				NumberUtils.createNumber(StringUtils.trim(value))).get(path));
	}

	@Override protected BooleanExpression gte(NumberPath path, String value) {
		Validate.isTrue(StringUtils.isNumeric(value), "Invalid numeric value");
		//return path.goe(Integer.parseInt(StringUtils.trim(value)));
		return path.goe((Number) new NumberConversions<Tuple>(Projections.tuple(path)).newInstance(
				NumberUtils.createNumber(StringUtils.trim(value))).get(path));
	}

	@Override protected BooleanExpression lt(NumberPath path, String value) {
		Validate.isTrue(StringUtils.isNumeric(value), "Invalid numeric value");
		//return path.lt(Integer.parseInt(StringUtils.trim(value)));
		return path.lt((Number) new NumberConversions<Tuple>(Projections.tuple(path)).newInstance(
				NumberUtils.createNumber(StringUtils.trim(value))).get(path));
	}

	@Override protected BooleanExpression lte(NumberPath path, String value) {
		Validate.isTrue(StringUtils.isNumeric(value), "Invalid numeric value");
		//return path.loe(Integer.parseInt(StringUtils.trim(value)));
		return path.loe((Number) new NumberConversions<Tuple>(Projections.tuple(path)).newInstance(
				NumberUtils.createNumber(StringUtils.trim(value))).get(path));
	}
}
