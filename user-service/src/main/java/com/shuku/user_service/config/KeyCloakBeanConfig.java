package com.shuku.user_service.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakBeanConfig {
    private final KeycloakConfiguration keyCloakConfiguration;

    public KeyCloakBeanConfig(KeycloakConfiguration keyCloakConfiguration){
        this.keyCloakConfiguration = keyCloakConfiguration;
    }

    @Bean
    public Keycloak keycloak(){
        return KeycloakBuilder.builder().grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .serverUrl(keyCloakConfiguration.url())
                .realm(keyCloakConfiguration.realm())
                .clientId(keyCloakConfiguration.clientId())
                .clientSecret(keyCloakConfiguration.clientSecret())
                .build();
    }

}
