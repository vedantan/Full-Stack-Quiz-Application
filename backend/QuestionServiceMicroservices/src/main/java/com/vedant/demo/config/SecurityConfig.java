package com.vedant.demo.config;

import com.vedant.demo.filter.JwtHeaderFilter;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtHeaderFilter jwtHeaderFilter;

    public SecurityConfig(JwtHeaderFilter jwtHeaderFilter) {
        this.jwtHeaderFilter = jwtHeaderFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // ✅ enable CORS
                .addFilterBefore(jwtHeaderFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth

                        // ✅ allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔥 ADMIN ONLY
                        .requestMatchers("/api/questions/admin/**").hasRole("ADMIN")

                        // 🔥 USER + ADMIN
                        .requestMatchers("/api/questions/**").hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}