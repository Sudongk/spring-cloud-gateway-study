package com.example.gateway.filter;

import com.example.gateway.dto.CustomDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class CustomGlobalFilter extends AbstractGatewayFilterFactory<CustomDto> {

    @Value("${jwt.secret}")
    private String secret;

    public CustomGlobalFilter() {
        super(CustomDto.class);
    }

    @Override
    public GatewayFilter apply(CustomDto config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            System.out.println("secret: " + secret);

            if (config.getLogging()) {
                // HTTP 요청 정보 로깅
                System.out.println(
                        "req" + request.getId() +
                        ", " + request.getMethod() +
                        ", " + request.getPath() +
                        ", " + request.getRemoteAddress()
                );
            }

            // 다음 필터 또는 라우팅에 요청을 전달하고 응답을 기다림
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.getLogging()) {
                    // HTTP 응답 정보 로깅
                    System.out.println(
                            "res" + request.getId() +
                            ", " + response.getStatusCode() +
                            ", " + response.getHeaders() +
                            ", " + response.getCookies() +
                            ", " + response.getHeaders().get("Authorization")
                    );
                }
            }));
        };
    }

}
