package com.vedant.demo.config;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // ✅ Forward Authorization header
                String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

                if (authHeader != null) {
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, authHeader);
                }

                // ✅ Forward custom headers
                String user = request.getHeader("X-User");
                String role = request.getHeader("X-Role");

                if (user != null) {
                    requestTemplate.header("X-User", user);
                }

                if (role != null) {
                    requestTemplate.header("X-Role", role);
                }
            }
        };
    }
}
