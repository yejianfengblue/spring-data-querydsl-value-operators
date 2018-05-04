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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.utils;

import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines some utility functions to gracefully define/invoke lambda functions
 * that perform operations which throws checked exception.
 * 
 * @author gt_tech
 *
 */
public final class LambdaUtils {

	private static final Logger LOG = LoggerFactory.getLogger(LambdaUtils.class);

	/**
	 * Invokes the provided lambda function wrapped in {@link ThrowingConsumer}.
	 * On exception, attempts to case/wrap the exception and if a errorHandler
	 * runnable was provided, it's also executed in case of exception.
	 * 
	 * @param throwingConsumer
	 *            Consumer function to be executed
	 * @param exceptionClass
	 *            Type of exception Consumer can throw
	 * @param errorHandler
	 *            a error handler - {@link Optional} of {@link Runnable} that
	 *            should be invoked in case of exception from
	 *            {@link ThrowingConsumer}
	 * @return
	 */
	public static <T, E extends Exception> Consumer<T> handlingConsumerWrapper(ThrowingConsumer<T, E> throwingConsumer,
			Class<E> exceptionClass, Optional<Runnable> errorHandler) {

		return i -> {
			try {
				throwingConsumer.accept(i);
			} catch (Exception ex) {
				try {
					E exCast = exceptionClass.cast(ex);
					LOG.error("Exception occured : {}", exCast.getMessage());
				} catch (ClassCastException ccEx) {
					throw new RuntimeException(ex);
				} finally {
					if (errorHandler.isPresent()) {
						try {
							errorHandler.get()
									.run();
						} catch (Throwable t) {

						}
					}
				}
			}
		};
	}
}
