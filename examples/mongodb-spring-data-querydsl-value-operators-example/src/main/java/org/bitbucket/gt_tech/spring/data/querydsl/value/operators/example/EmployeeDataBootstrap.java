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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example;

import static org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.utils.LambdaUtils.handlingConsumerWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.dao.EmployeeRepository;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class which bootstraps the test employee-data for this example
 * application into underlying {@link EmployeeRepository}
 * 
 * @author gt_tech
 *
 */
@Component
public class EmployeeDataBootstrap implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	private final String pattern = "classpath:users/*.json";

	private final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	MongoTemplate mongoTemplate;

	/**
	 * Method loads the data from JSON files into {@link EmployeeRepository}
	 * @throws Exception if any error occurs in bootstrapping user data.
	 */
	@PostConstruct
	public void bootstrap() throws Exception {
		Resource[] usersResources = loadResources(pattern);
		if (usersResources != null) {
			Arrays.stream(usersResources)
					.forEach(handlingConsumerWrapper(
							us -> mongoTemplate.save(mapper.readValue(us.getInputStream(), Employee.class)),
							IOException.class, Optional.empty()));
		}
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Utility method to load all matching resources from provided path
	 * @param pattern Path pattern
	 * @return array of {@link Resource} found with provided path
	 * @throws IOException if any exception occurs 
	 */
	Resource[] loadResources(String pattern) throws IOException {
		return ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
				.getResources(pattern);
	}
}
