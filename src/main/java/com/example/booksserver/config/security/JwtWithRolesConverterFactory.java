package com.example.booksserver.config.security;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JwtWithRolesConverterFactory {
    private final String clientId;

    public JwtAuthenticationConverter createConverter() {
        Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter = jwt -> {
            Map<String, Map<String, Collection<String>>> resources  = jwt.getClaim("resource_access");
            //get this value from config
            Map<String, Collection<String>> resourceAccess = resources.get(clientId);
            Collection<String> roles = resourceAccess.get("roles");

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toSet());
        };
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
