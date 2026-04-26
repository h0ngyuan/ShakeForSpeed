package com.sfs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShakeForSpeed API")
                        .version("2.0.0")
                        .description("ShakeForSpeed v2.0 - 摇一摇拼手速活动平台")
                        .contact(new Contact()
                                .name("SFS Team")
                                .email("sfs@example.com")));
    }
}
