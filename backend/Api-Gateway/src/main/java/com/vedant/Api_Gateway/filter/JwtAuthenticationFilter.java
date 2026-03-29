//package com.vedant.Api_Gateway.filter;
//
//import com.vedant.Api_Gateway.utils.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.annotation.Order;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import org.springframework.http.HttpHeaders;
//
//
//import java.nio.charset.StandardCharsets;
//
//@Component
//@Order(-1)
//public class JwtAuthenticationFilter implements GlobalFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
//            return chain.filter(exchange);
//        }
//
//        String path = exchange.getRequest().getURI().getPath();
//
//        if (path.contains("/api/auth")) {
//            return chain.filter(exchange);
//        }
//
//        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
//        }
//
//        String token = authHeader.substring(7);
//
//        if (!jwtUtil.validateToken(token)) {
//            return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
//        }
//
//        String username = jwtUtil.extractUsername(token);
//        String role = jwtUtil.extractRole(token);
//        System.out.println("Incoming Headers: " + exchange.getRequest().getHeaders());
//
//        ServerHttpRequest mutatedRequest = exchange.getRequest()
//                .mutate()
//                .headers(headers -> {
//                    headers.add("X-User-Name", username);
//                    headers.add("X-User-Role", role);
//                })
//                .build();
//
//        ServerWebExchange mutatedExchange = exchange.mutate()
//                .request(mutatedRequest)
//                .build();
//
//        return chain.filter(mutatedExchange);
//    }
//
//    // ✅ Common error response method
//    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
//
//        exchange.getResponse().setStatusCode(status);
//
//        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
//        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
//
//        return exchange.getResponse().writeWith(Mono.just(buffer));
//    }
//
//}


package com.vedant.Api_Gateway.filter;

import com.vedant.Api_Gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;

@Component
@Order(-1)
public class JwtAuthenticationFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();

        if (path.contains("/api/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        System.out.println("Incoming Headers: " + exchange.getRequest().getHeaders());

        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.putAll(super.getHeaders());

                // ✅ FORWARD ORIGINAL TOKEN
                headers.set(HttpHeaders.AUTHORIZATION, authHeader);

                // ✅ SAFE — this is a NEW mutable object
                headers.add("X-User", username);
                headers.add("X-Role", role);

                return headers;
            }
        };

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {

        exchange.getResponse().setStatusCode(status);

        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
