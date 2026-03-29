package com.vedant.Api_Gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(csrf -> csrf.disable())

                // ❌ DISABLE CORS HERE (VERY IMPORTANT)
                .cors(cors -> cors.disable())

                // ❌ disable default header writing (THIS IS THE FIX)
                .headers(headers -> headers.disable())

                // ❌ disable security context (optional but safe for gateway)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                // ❌ disable request cache
                .requestCache(requestCache -> requestCache.disable())

                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .build();
    }
}
