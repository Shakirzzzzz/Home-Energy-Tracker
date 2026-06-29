package com.shuku.user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceApiDocs() {

        return new OpenAPI().components(new Components().addSecuritySchemes("oauth2", new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows().authorizationCode(new OAuthFlow().authorizationUrl("http://localhost:8091/realms/het-security-realm/protocol/openid-connect/auth")
                                .tokenUrl("http://localhost:8091/realms/het-security-realm/protocol/openid-connect/token")
                        .scopes(new Scopes().addString("openid","OpenID Connect authentication"))))))
                .security(List.of(new SecurityRequirement().addList("oauth2")))
                .addServersItem(new Server().url("http://localhost:9000"))
                .info(new Info()
                        .title("User Service API")
                        .contact(getContact())
                        .license(getLicense())
                        .description("User service API for Home Energy Tracker")
                        .version("1.0.0"))

                ;

    }

    private static License getLicense() {
        License license = new License();
        license.setName("Creative Commons Attributions-NonCommercial 4.0 International License");
        license.setUrl("http://creativecommons.org/licenses/by-nc/4.0/");
        return license;
    }

    private static Contact getContact() {
        Contact contact = new Contact();
        contact.setEmail("s.shakirz26@gmail.com");
        return contact;
    }
}
