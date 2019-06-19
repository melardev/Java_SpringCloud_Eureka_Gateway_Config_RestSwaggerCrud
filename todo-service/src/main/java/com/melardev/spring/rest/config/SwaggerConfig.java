package com.melardev.spring.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /*
        private Predicate<String> postPaths() {
            return or(regex("/api/posts.*"), regex("/api/javainuse.*"));
        }
    */
    @Bean
    public Docket workforceManagementApi() {
        return new Docket(SWAGGER_2)
                .groupName("public")
                .select()
                .apis(withClassAnnotation(RestController.class))
                .paths(or(regex("/todos/.*")))
                .build()
                .tags(new Tag("v1", "Todo API v1"))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Rest Api microservice")
                .description("Microservice that handles todos")
                .termsOfServiceUrl("http://melardev.com")
                .contact(new Contact("Melardev",
                        "http://melardev.com",
                        "melardev@microservices_mail.com")) // email does not exist obviously
                .license("MIT License")
                .licenseUrl("https://raw.githubusercontent.com/melardev/KotlinSpringBootRxApiRxMongoCrud/master/LICENSE")
                .version("1.0")
                .build();
    }


}
