###Start application

###### From Maven
* mvn clean spring-boot:run

###### From IDE
* Run as "Spring Boot Application" (main class - [SpringApplication](https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators/src/master/examples/mongodb-spring-data-querydsl-value-operators-example/src/main/java/org/bitbucket/gt_tech/spring/data/querydsl/value/operators/example/SpringApplication.java))


#TODO
 * Example structure
 * Queries and expected results
 * Note on frequently used searches
 * Exercise for "age"
 * Note for admin privileges for embedded Mongo server 

    * http://localhost:8080/employees/search?userName=dgayle
    * http://localhost:8080/employees/search?userName=dgayle&userName=ssmith
    * http://localhost:8080/employees/search?userName=dgayle&userName=contains(smith)
    * http://localhost:8080/employees/search?userName=dgayle&userName=contains(smith)&userName=and(not(startsWith(k)))
    * http://localhost:8080/employees/search?emails.address=ssmith@company.com
    * http://localhost:8080/employees/search?emails.address=ne(ssmith@company.com)
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&emails.address=and(not(endsWith(@dummy.com)))
    * http://localhost:8080/employees/search?emails.address=endsWith(@example.com)&emails.address=or(endsWith(@dummy.com))
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&emails.address=and(endsWith(@dummy.com))
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&jobData.department=and(SALES)
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&jobData.department=and(ne(SALES))
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&status=LOCKED
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&status=ne(LOCKED)
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&status=eq(ACTIVE)
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&status=ACTIVE
    * http://localhost:8080/employees/search?emails.address=endsWith(@company.com)&profile.age=gte(41)
    * http://localhost:8080/employees/search?profile.age=gt(41)
    * http://localhost:8080/employees/search?profile.age=lt(41)
    * http://localhost:8080/employees/emails/dgayle@company.com

### Exercise
What will it take to make age comparison in-between certain age? \
_Create snippets for reference_

