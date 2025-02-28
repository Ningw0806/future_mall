package com.future.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/v1/account-service/auth/**").permitAll()
                        .pathMatchers("/api/v1/account-service/hello").permitAll()
                        .pathMatchers("/api/v1/*/hello").permitAll() // 允许所有服务的hello接口
                        .pathMatchers("/api/v1/item-service/public/**").permitAll()
                        // 我们将在过滤器中处理Authentication，这里先允许所有请求通过
                        .anyExchange().permitAll()
                )
                .build();
    }
}