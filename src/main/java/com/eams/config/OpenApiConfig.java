package com.eams.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;



@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("我的 API")
                        .version("1.0.0")
                        .description("API 文檔描述 ")
                        .contact(new Contact()
                                .name("開發團隊")
                                .email("stanshy666@gmail.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("開發環境"),
                        new Server().url("https://api.example.com").description("生產環境")
                ));
    }
}