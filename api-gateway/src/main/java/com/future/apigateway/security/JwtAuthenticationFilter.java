package com.future.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final List<String> excludedUrls;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        // 排除不需要认证的路径
        this.excludedUrls = List.of(
                "/api/v1/account-service/auth/login",
                "/api/v1/account-service/auth/register",
                "/api/v1/account-service/hello",
                "/api/v1/item-service/hello",
                "/api/v1/order-service/hello",
                "/api/v1/payment-service/hello",
                "/api/v1/shopping-cart-service/hello",
                "/api/v1/item-service/public"
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        logger.debug("正在处理请求: {}", path);

        // 检查是否是需要排除的URL
        for (String excludedUrl : excludedUrls) {
            if (path.startsWith(excludedUrl)) {
                logger.debug("跳过认证的路径: {}", path);
                return chain.filter(exchange);
            }
        }

        // 获取JWT令牌
        String token = getTokenFromRequest(request);

        // 检查令牌
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            logger.debug("有效的令牌，允许请求通过: {}", path);

            // 提取用户信息并添加到请求头中，以便转发给微服务
            try {
                String username = jwtTokenProvider.getUsername(token);
                // 可以从令牌中提取更多信息并添加到请求头
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Name", username)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                logger.error("处理令牌时出错: {}", e.getMessage());
                return onError(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logger.debug("无效的令牌，拒绝请求: {}", path);
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
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