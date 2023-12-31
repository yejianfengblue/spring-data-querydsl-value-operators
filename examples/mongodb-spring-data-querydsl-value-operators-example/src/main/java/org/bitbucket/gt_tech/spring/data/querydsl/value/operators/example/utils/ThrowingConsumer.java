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

/**
 * Functional interface which accepts an argument and can throw an exception
 * during acceptance phase
 * 
 * @author gt_tech
 *
 * @param <T>
 *            type of value accepted
 * @param <E>
 *            type of exception thrown
 * 
 * @see LambdaUtils#handlingConsumerWrapper(ThrowingConsumer, Class,
 *      java.util.Optional)
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
	void accept(T t) throws E;
}
