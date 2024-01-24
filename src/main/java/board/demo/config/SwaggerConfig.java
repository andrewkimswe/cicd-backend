package board.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rapid Repertoire Chat API")
                        .version("1.0")
                        .description("This is the API documentation for the Rapid Repertoire Chat application.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    // Group for public APIs
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/public/**")
                .build();
    }

    // Group for admin APIs
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin-apis")
                .pathsToMatch("/admin/**")
                .build();
    }

    // Additional groups can be added as needed
}
