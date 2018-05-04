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
package org.bitbucket.gt_tech.spring.data.querydsl.value.operators.example.spring;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;

/**
 * Configuration for Swagger using SpringFox.
 * @author gt_tech
 *
 */
/*
 * To enable after SpringFox 2.9.0 release #2298
 * https://github.com/springfox/springfox/issues/2372 fixes a breaking issue in
 * SpringFox with Spring boot 2x
 *
 * Still an issue with 2.9.0 so disabling swagger
 *
 * https://github.com/springfox/springfox/issues/2298
 */
//@Configuration
//@EnableSwagger2
//@Import(SpringDataRestConfiguration.class)
public class SwaggerConfig {

    //@Bean
    public Docket userApiDocket() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                                                      .apis(RequestHandlerSelectors.any())
                                                      .paths(PathSelectors.regex("/users.*"))
                                                      .build()
                                                      .groupName("Employees API")
                                                      .apiInfo(
                                                              apiInfo("Spring data Querydsl value operators example " +
                                                                              "application",
                                                                      "API definition for example application " +
                                                                              "demonstrating usage of Spring data " +
                                                                              "querydsl value operators SDK library"));
    }

    ApiInfo apiInfo(String title, String description) {
        return new ApiInfoBuilder().title(title)
                                   .description(description)
                                   .license("")
                                   .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                                   .termsOfServiceUrl("")
                                   .version("1.0.0")
                                   .contact(new Contact("gt_tech",
                                                        "https://bitbucket.org/gt_tech/spring-data-querydsl-value-operators",
                                                        ""))
                                   .build();
    }
}
