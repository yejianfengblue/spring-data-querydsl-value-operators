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

/**
 * Strategy interface for persistence and retrieval of
 * {@link QuerydslHttpRequestContext}. It is not recommended to use this
 * directly by users. Users are advised to use
 * {@link QuerydslHttpRequestContextHolder} for storage if needed as
 * {@link QuerydslHttpRequestContextHolder} is internally aware of which
 * strategy to use based on runtime configurations
 * 
 * @author gt_tech
 *
 */
public interface QuerydslHttpRequestContextHolderStrategy {

	/**
	 * Clears the current context.
	 */
	void clearContext();

	/**
	 * Obtains the current context.
	 *
	 * @return a context if available, <code>null</code> otherwise.
	 */
	QuerydslHttpRequestContext getContext();

	/**
	 * Sets the current context.
	 *
	 * @param context
	 *            to the new argument (should never be <code>null</code>,
	 *            although implementations must check if <code>null</code> has
	 *            been passed and throw an <code>IllegalArgumentException</code>
	 *            in such cases)
	 */
	void setContext(QuerydslHttpRequestContext context);

}
