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

import org.apache.commons.lang3.Validate;

/**
 * {@link ThreadLocal} based implementation of
 * {@link QuerydslHttpRequestContextHolderStrategy}. Supports both
 * {@link ThreadLocal} as well as {@link InheritableThreadLocal} based on
 * initializing argument provided to it.
 * 
 * @author gt_tech
 *
 */
public class ThreadLocalQuerydslHttpRequestContextHolderStrategy implements QuerydslHttpRequestContextHolderStrategy {

	private final ThreadLocal<QuerydslHttpRequestContext> holder;

	/**
	 * Constructor
	 * 
	 * @param inheritable
	 *            dictates whether a {@link InheritableThreadLocal} will be used
	 *            if this argument value is <code>true</code> or else a
	 *            {@link ThreadLocal} is used in case of <code>false</code>
	 *            value.
	 */
	public ThreadLocalQuerydslHttpRequestContextHolderStrategy(boolean inheritable) {
		holder = inheritable ? new InheritableThreadLocal() : new ThreadLocal();
	}

	@Override
	public void clearContext() {
		this.holder.remove();
	}

	@Override
	public QuerydslHttpRequestContext getContext() {
		return this.holder.get();
	}

	@Override
	public void setContext(QuerydslHttpRequestContext context) {
		Validate.notNull(context, "Supplied context is null");
		this.holder.set(context);
	}
}
