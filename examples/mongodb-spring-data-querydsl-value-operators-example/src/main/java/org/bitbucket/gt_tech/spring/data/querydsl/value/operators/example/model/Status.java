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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines types of status a {@link Employee} can have
 * @author gt_tech
 *
 */
public enum Status {

	ACTIVE("ACTIVE"),

	LOCKED("LOCKED");

	private String value;

	Status(String value) {
		this.value = value;
	}

	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static Status fromValue(String text) {
		for (Status b : Status.values()) {
			if (String.valueOf(b.value)
					.equals(text)) {
				return b;
			}
		}
		return null;
	}
}
