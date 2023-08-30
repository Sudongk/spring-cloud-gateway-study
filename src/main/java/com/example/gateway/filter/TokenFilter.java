package com.example.gateway.filter;

import com.example.gateway.config.JwtService;
import com.example.gateway.dto.CustomDto;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TokenFilter extends AbstractGatewayFilterFactory<CustomDto> {

    private final JwtService jwtService;

    public TokenFilter(JwtService jwtService) {
        super(CustomDto.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(CustomDto config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (config.getLogging()) {
                // HTTP 요청 정보 로깅
                System.out.println(
                        "req" + request.getId() +
                                ", " + request.getMethod() +
                                ", " + request.getPath() +
                                ", " + request.getRemoteAddress()
                );
            }

            if (request.getMethod() == HttpMethod.POST && isValidToken(request)) {
                return onError(response, "401 Invalid token", HttpStatus.UNAUTHORIZED);
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

    public boolean isValidToken(ServerHttpRequest request) {
        List<String> authorization = request.getHeaders().get("Authorization");

        if (authorization == null || authorization.isEmpty()) {
            return false;
        }

        String bearerToken = authorization.get(0);

        if (bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.replace("Bearer ", "");
            return jwtService.parseToken(token);
        }

        return false;
    }

    public Mono<Void> onError(ServerHttpResponse response,
                              String msg,
                              HttpStatusCode code) {
        response.setStatusCode(code);
        return response.setComplete();
    }

}
