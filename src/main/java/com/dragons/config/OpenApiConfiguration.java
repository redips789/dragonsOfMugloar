package com.dragons.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    private final Environment environment;

    public OpenApiConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(apiServer()))
                .tags(List.of(apiTag()));
    }

    private Server apiServer() {
        return new Server().url("/");
    }

    private Info apiInfo() {
        return new Info()
                .title("Solution to Dragons of Mugloar")
                .description("Solution to Dragons of Mugloar")
                .version(environment.getProperty("dragons-of-mugloar.version_major"))
                .contact(apiContact())
                .license(apiLicence());
    }

    private Tag apiTag() {
        return new Tag()
                .name("Homework")
                .description("For BigBank");
    }

    private License apiLicence() {
        return new License()
                .name("LICENSE")
                .url("LICENSE URL");
    }

    private Contact apiContact() {
        return new Contact()
                .name("Povilas Jegelevičius")
                .email("povilas.jegelevicius@gmail.com");
    }
}
