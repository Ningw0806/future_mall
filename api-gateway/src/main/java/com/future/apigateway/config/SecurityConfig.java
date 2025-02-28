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
        http
                // 使用新的API替代过时的csrf()方法
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/v1/account-service/auth/**").permitAll()
                        .pathMatchers("/api/v1/account-service/hello").permitAll()
                        .pathMatchers("/api/v1/item-service/public/**").permitAll()
                        .anyExchange().authenticated()
                );

        return http.build();
    }
}