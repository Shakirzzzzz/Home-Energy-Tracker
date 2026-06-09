package com.shuku.user_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceApiDocs(){

        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("User Service API")
                        .contact(getContact())
                        .license(getLicense())
                        .description("User service API for Home Energy Tracker")
                        .version("1.0.0"));

    }

    private static License getLicense(){
        License license = new License();
        license.setName("Creative Commons Attributions-NonCommercial 4.0 International License");
        license.setUrl("http://creativecommons.org/licenses/by-nc/4.0/");
        return license;
    }

    private static Contact getContact(){
        Contact contact = new Contact();
        contact.setEmail("s.shakirz26@gmail.com");
        return contact;
    }
}
