package com.future.apigateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;
    private final List<String> excludedUrls;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        // 排除不需要认证的路径
        this.excludedUrls = List.of(
                "/api/v1/account-service/auth/login",
                "/api/v1/account-service/auth/register",
                "/api/v1/item-service/public",
                "/api/v1/account-service/hello"
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 检查是否是需要排除的URL
        String path = request.getURI().getPath();
        for (String excludedUrl : excludedUrls) {
            if (path.startsWith(excludedUrl)) {
                return chain.filter(exchange);
            }
        }

        // 获取JWT令牌
        String token = getTokenFromRequest(request);

        // 检查令牌
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 令牌有效，继续处理请求
            return chain.filter(exchange);
        } else {
            // 令牌无效，返回未授权状态
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    @Override
    public int getOrder() {
        // 确保此过滤器在过滤器链中较早执行
        return -100;
    }
}