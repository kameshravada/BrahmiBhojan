package com.brahmibhojan.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI brahmiBhojanOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("BrahmiBhojan API")
                        .version("v1")
                        .description("Core API contracts for auth, cart, orders, payments, and admin"));
    }
}

