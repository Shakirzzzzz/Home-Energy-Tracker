package com.shuku.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Converter<Jwt, AbstractAuthenticationToken> authenticationConverter) throws Exception{
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers(HttpMethod.POST, "/api/v1/user").permitAll()
                        .requestMatchers("/actuator/**","/swagger-ui.html","/swagger-ui/**","/docs/**","/v3/api-docs/**","/swagger-resources/**","/api-docs/**").permitAll()
                        .anyRequest().authenticated())

                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwtDecoder -> jwtDecoder.jwtAuthenticationConverter(authenticationConverter)))
                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .build();
    }
    // custom converter to map the roles in the jwt from the keycloak to authorities in the spring boot
    @Bean
    public AuthoritiesConverter realmRolesAuthoritiesConverter(){
        return claims -> {
            Map<String,Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
            if (realmAccess == null){
                return List.of();
            }

            List<String> roles = (List<String>) realmAccess.get("roles");
            if(roles == null){
                return List.of();
            }

            List<GrantedAuthority> authorities = new ArrayList<>();
            for(String role : roles){
                // SimpleGrantedAuthority is implementation of GrantedAuthority
                authorities.add(new SimpleGrantedAuthority(role));
            }
            return authorities;

        };
    }

    @Bean
    public JwtAuthenticationConverter authenticationConverter(AuthoritiesConverter authoritiesConverter){
        var authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt ->{
            return authoritiesConverter.convert(jwt.getClaims());
        });
        return authenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:9000")); // wherever Swagger UI is actually served from — for testing 8080
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public JwtDecoder jwtDecoder(){
        return JwtDecoders.fromIssuerLocation(issuerUri);

    }
}
