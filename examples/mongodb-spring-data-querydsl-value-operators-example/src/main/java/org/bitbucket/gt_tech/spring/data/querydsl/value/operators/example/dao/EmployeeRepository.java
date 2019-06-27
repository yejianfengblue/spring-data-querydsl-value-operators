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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.ExpressionProviderFactory;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.model.QEmployee;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.model.Employee;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import io.swagger.annotations.Api;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

/**
 * Implementation of {@link Repository} which demonstrates the customization of
 * {@link QuerydslBindings} to take advantage of rich value operators offered by
 * the locally offered SDK library that this example is built to demonstrate.s
 * 
 * <p>
 * For simplicity of example application, this {@link Repository} also acts as
 * {@link RestController} by capitalizing on Spring data rest, however it is
 * anticipated that a production grade application may choose to seperate the
 * DAO and controller concerns.
 * </p>
 * 
 * @author gt_tech
 *
 */
/*
 * Important:
 *
 * Though standard Repository function would still work with
 * just @RepositoryRestResource annotation but it's important to
 * apply @Repository annotation to this since otherwise {@link Repositories}
 * won't be able to resolve this repository during formation of
 * QuerydslBindings. That resolution is required by {@link
 * QuerydslPredicateArgumentResolver} internals to obtain the appropriate {@link
 * QuerydslBinderCustomizer}.
 *
 * @author gt_tech
 *
 */
@Repository
@RepositoryRestResource(collectionResourceRel = "employees", path = "/employees")
@RequestMapping(value = "/employees")
@Api(tags = "Employees API")
public interface EmployeeRepository
		extends MongoRepository<Employee, String>, QuerydslPredicateExecutor<Employee>, QuerydslBinderCustomizer<QEmployee> {

	/**
	 * Method is an example of providing friendlier direct search URLs for more
	 * frequent type of searches to end-consumers but internal implementation
	 * still delegating to QuerydslPredicate based search.
	 * 
	 * @param emailAddress
	 *            String email address to search on
	 * @return {@link Iterable} of {@link Employee} having same email address as
	 *         supplied to this method
	 */
	@RequestMapping(path = { "/emails/{emailAddress}" }, produces = { MediaType.APPLICATION_JSON_VALUE }, method = {
			RequestMethod.GET, RequestMethod.POST })
	default ResponseEntity<Iterable<Employee>> search(@PathVariable String emailAddress) {
		if (StringUtils.isBlank(emailAddress)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			return search(QEmployee.employee.emails.any().address.eq(emailAddress), PageRequest.of(0, 10));
		}
	}

	/**
	 * Generic Search interface implementation using QueryDSL integration.
	 * 
	 * @param predicate
	 *            {@link Predicate} to be used to perform search.
	 * @param pageable
	 *            {@link Pageable} instance
	 * @return {@link Iterable} of {@link Employee} satisfied the provided
	 *         predicate.
	 */
	@RequestMapping(path = { "/search" }, produces = { MediaType.APPLICATION_JSON_VALUE }, method = { RequestMethod.GET,
			RequestMethod.POST })
	default ResponseEntity<Iterable<Employee>> search(
			@ApiIgnore @QuerydslPredicate(root = Employee.class) Predicate predicate, @PageableDefault Pageable pageable) {
		if (predicate == null || (BooleanBuilder.class.isAssignableFrom(predicate.getClass())
				&& !((BooleanBuilder) predicate).hasValue())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			return ResponseEntity.ok(this.findAll(predicate, pageable));
		}
	}

	/**
	 * Customizes the supplied default {@link QuerydslBindings} This method's
	 * implementation demonstrates the usage of
	 * {@link ExpressionProviderFactory} which is the entry-point on Spring data
	 * "Querydsl value operators" SDK library for which this example application
	 * is developed.
	 */
	@Override
	default void customize(QuerydslBindings bindings, QEmployee root) {

		bindings.bind(root.userName)
				.all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));
		bindings.bind(root._id)
				.all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));

		bindings.bind(root.profile.firstName)
				.all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));
		bindings.bind(root.profile.lastName)
				.all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));

		// Demonstration of how a certain attribute or search parameter can be
		// compared on single-value level for
		// indicating the fact that search interface doesn't expect API consumer
		// to provide multiple values for this
		// parameter and if that happens, it will use only the first value for
		// querying.
		// Since in case of using single-valued the value passed to it is
		// wrapped in Optional monad and
		// ExpressionProvider has a contract that value passed to it must not be
		// Optional so it is unwrapped here.
		bindings.bind(root.profile.age)
				.firstOptional((path, optionalValue) -> ExpressionProviderFactory.getPredicate(path,
						optionalValue.orElseGet(() -> null)));

		bindings.bind(root.emails.any().address)
				.as("emails.address")
				.all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));
		// Not Required for StringPath values but it's recommended to register
		// alias(es) explicitly.
		// ListPath won't work without alias anyway easily or as elegantly..
		ExpressionProviderFactory.registerAlias(root.emails.any().address, "emails.address");

		bindings.bind(root.status)
				.all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));

		bindings.bind(root.jobData.department)
				.all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));
		bindings.bind(root.jobData.location)
				.all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));

		/*
		 * Demonstration of black-listing non-searchable parameters/fields
		 */
		bindings.excluding(root.profile.middleName);

		/*
		 * Another mechanism for black-listing unlisted properties. Note this
		 * may require explicit addition of including(..) paths for other than
		 * where an explicit alias is provided.
		 */
		// bindings.excludeUnlistedProperties(true);


		/**
		 * Demonstrating how to use DateTimePath in searches
		 *
		 * if only one value is specified - then exact date natch is performed
		 * if two values are provided in input, its considered a date range check (to make it convenient for end user, the binding is smart enough to figure from and to dates regardless of order received from user.
		 * if more than two values are provided, then all records matching to either of those date (Logical OR) is returned.
		 */
		bindings.bind(root.profile.dob).all((path, values) -> {
			Iterator<? extends Date> it = values.iterator();
			if ( values.size() == 1 )
				return Optional.ofNullable(path.eq(it.next()));
			else if ( values.size() == 2 ) {
				Date first = it.next();
				Date second = it.next();
				return Optional.ofNullable(path.between( first.before(second) ? first : second, first.after(second) ? first : second));
			}
			else {
				BooleanExpression e = path.eq(it.next());
				while ( it.hasNext() && e != null ) {
					e = e.or(path.eq(it.next()));
				}
				return Optional.ofNullable(e);
			}
		});

	}

}
