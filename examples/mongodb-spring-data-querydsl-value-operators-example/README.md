_Querydsl Value operator_ SDK's *[example application](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/)* provides a simplistic Employee Registry API system and illustrates the use of _Spring Data Querydsl Value operators_ usage in a way that empowers this example API to offer rich _Employee Search_ interface. 

Full SDK documentation can be located [here](https://gt-tech.bitbucket.io/spring-data-querydsl-value-operators/README.html)

# Schematics

Application is developed using Spring Boot 2.x with embedded Tomcat server. It utilizes an embedded MongoDB server for repository. 

#### Resource Model
Resource model of this API can be found in [org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.model](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/src/main/java/org/bitbucket/gt_tech/spring/data/querydsl/value/operators/example/model/) package. **Note** the use of _Document_ annotation on top level resource, [Employee](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/src/main/java/org/bitbucket/gt_tech/spring/data/querydsl/value/operators/example/model/Employee.java). This annotation though is optional for *Spring Data MongoDB* in case of a single-tenant/datastore API but is used by the Maven Querdsl API code generation to identify candidates for which Q-Classes _(Q-Types)_ needs to be generated. 

#### Resource Model Q-Type classes generation
Project's [POM](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/pom.xml) file has following plugin defined which generates the Q-Type classes before compilation phase.

```xml
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>${plugin.mongodb.mysema.apt.version}</version>
    <dependencies>
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-apt</artifactId>
            <version>4.1.4</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <phase>generate-sources</phase>
            <configuration>
                <outputDirectory>${project.build.directory}/generated-sources/querydsl/java</outputDirectory>
                <!-- Works with Entity Annotation -->
                <!--<processor>com.querydsl.apt.morphia.MorphiaAnnotationProcessor</processor> -->
                <!-- Works with QueryEntity annotation -->
                <!--<processor>com.querydsl.apt.QuerydslAnnotationProcessor</processor> -->
                <processor>org.springframework.data.mongodb.repository.support.MongoAnnotationProcessor</processor>
                <options>
                    <querydsl.logInfo>true</querydsl.logInfo>
                    <querydsl.listAccessors>false</querydsl.listAccessors>
                    <querydsl.useGetters>true</querydsl.useGetters>
                    <querydsl.unknownAsEmbeddable>true</querydsl.unknownAsEmbeddable>
                </options>
            </configuration>
        </execution>
    </executions>
</plugin>
``` 

POM also pulls in _spring-boot-starter-data-mongodb_ artifact to provide run-time bootstrapping for satisfying the _provided_ scoped dependencies of core SDK library, this was anyway required to make Spring data work with MongoDB.

#### Test employee data
Application loads test data for handful of employees in embedded MongoDB at the time of startup. Test data can be found [here](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/src/main/resources/users/).
Data load is handled by [EmployeeDataBootstrap](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/src/main/java/org/bitbucket/gt_tech/spring/data/querydsl/value/operators/example/EmployeeDataBootstrap.java)

#### Repository
[EmployeeRepository](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/src/main/java/org/bitbucket/gt_tech/spring/data/querydsl/value/operators/example/dao/EmployeeRepository.java) is the main repository interface which as per guidance in core SDK library's documentation extends [QuerydslPredicateExecutor](https://docs.spring.io/spring-data/commons/docs/2.0.5.RELEASE/api/org/springframework/data/querydsl/QuerydslPredicateExecutor.html) and [QuerydslBinderCustomizer](https://docs.spring.io/spring-data/commons/docs/2.0.5.RELEASE/api/org/springframework/data/querydsl/binding/QuerydslBinderCustomizer.html).

To keep example API code small, this repository is also annotated with Spring MVC specific annotations so it also acts as a _RestController_ by utilizing _Spring Data Rest_ framework. 

Following two methods in repository provide the search capabilities:
* Generic search with value operator support
```java
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
```
* An example of frequently used searches which may change on API specific reasons but it's internal implementation delegation to generic search
```java
@RequestMapping(path = { "/emails/{emailAddress}" }, produces = { MediaType.APPLICATION_JSON_VALUE }, method = {
			RequestMethod.GET, RequestMethod.POST })
	default ResponseEntity<Iterable<Employee>> search(@PathVariable String emailAddress) {
		if (StringUtils.isBlank(emailAddress)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			return search(QEmployee.employee.emails.any().address.eq(emailAddress), PageRequest.of(0, 10));
		}
	}
```

**_customize(..)_** method's implementation illustrates the usage of [ExpressionProviderFactory](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/querydsl-value-operators/src/main/java/org/bitbucket/gt_tech/spring/data/querydsl/value/operators/ExpressionProviderFactory.java) to enable rich value operators on search fields.

#### Integration tests
Example application is equipped with e2e integration tests demonstrating various queries and how the result/response varies depending on different usage of value operators. Example queries mentioned below in this document can also be seen within [EmployeeSearchIT](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/src/test/groovy/org/bitbucket/gt_tech/spring/data/querydsl/value/operators/example/dao/EmployeeSearchIT.groovy) test class.

To run integration tests from Maven:
```cmd
mvn clean verify
```

Tests can also be executed from IDE directly after example application is imported

# How to start Maven application

Since example application uses [Embedded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo), the startup may take time as it downloads the required binaries. **Note** that MongoDB is started as a forked process so process running the example API must have administrative privelege on user's machine as otherwise startup may fail.  

By default application will start on port 8080 which can be overridden by providing a JVM property _server.port_ to a different value.

#### From Maven
```cmd
mvn clean spring-boot:run
```

#### From IDE
* Run as "Spring Boot Application" (main class - [SpringApplication](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/src/main/java/org/bitbucket/gt_tech/spring/data/querydsl/value/operators/example/SpringApplication.java))

### Search Queries for _Employee Search_ interface

* Find employee by username
```
http://localhost:8080/employees/search?userName=dgayle
```
* Find employee having one of the two userNames (OR clause)    
```
http://localhost:8080/employees/search?userName=dgayle&userName=ssmith
```
    
* Find employee(s) having a specific username or containing another text in username value    
```
http://localhost:8080/employees/search?userName=dgayle&userName=contains(smith)
```
* Find employees having a specific username or containing another text in username value but exclude users who username starts with 'k'
```
http://localhost:8080/employees/search?userName=dgayle&userName=contains(smith)&userName=and(not(startsWith(k)))
```

* Find employee by email address
```
http://localhost:8080/employees/search?emails.address=ssmith@company.com
```
* Find employee not having the supplied email address
```
http://localhost:8080/employees/search?emails.address=ne(ssmith@company.com)
```
* Find employees having a "company" email address
```
http://localhost:8080/employees/search?emails.address=endsWith(@company.com)
```

* Find employees with an email in "company" email domain but exclude those who also have an email address in "dummy.com" email provider
```
http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&emails.address=and(not(endsWith(@dummy.com)))
```

* Find employees with email either in "company" email domain or else "dummy" email domain
```
http://localhost:8080/employees/search?emails.address=endsWith(@example.com)&emails.address=or(endsWith(@dummy.com))
```

* Find employees having emails both in "company" and "dummy" email domain
```
http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&emails.address=and(endsWith(@dummy.com))
```
* Find employee in _SALES_ having a company's email
```
http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&jobData.department=and(SALES)
```

* Find employee outside _SALES_ having a company's email
```
http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&jobData.department=and(ne(SALES))
```

* Find all _LOCKED_ employees having a company email
```
http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&status=LOCKED
```

* Find all _ACTIVE_ employees having company's email
```

http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&status=ne(LOCKED)

http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&status=eq(ACTIVE)

http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&status=ACTIVE
```

* Find all employees having a company's email and are at minimum 41 years old.
```
http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&profile.age=gte(41)
```

* Find all employees having a company's email and are older than 41 years.
```
http://localhost:8080/employees/search?profile.age=gt(41)
```

* Find all employees having a company's email and are younger than 41 years.
```
http://localhost:8080/employees/search?profile.age=lt(41)
```

* Example for a search field focused REST uri for a frequently used search _(e.g. Email based search in this example application)_
```
http://localhost:8080/employees/emails/dgayle@company.com
```

### Exercise
What changes would need to be made to allow searches to find employees between certain age group? (e.g. Find employees in 40 and 50 years old age group)

Obviously that search will not work in out of the box example application and is intentionally done so to facilitate this exercise.

*Solution* for this exercise can be referred [here](https://bitbucket.org/snippets/gt_tech/5ezxqB)
