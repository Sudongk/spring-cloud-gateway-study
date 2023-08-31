package com.example.gateway.filter;

import com.example.gateway.dto.CustomDto;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class CacheFilter extends AbstractGatewayFilterFactory<CustomDto> {

    private final Map<String, CacheData> map = new HashMap();

    public CacheFilter() {
        super(CustomDto.class);
    }

    @Override
    public GatewayFilter apply(CustomDto config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            map.put(request.getId(), new CacheData(request.getPath().toString(), request.getMethod()));

            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        if (config.getLogging()) {
                            System.out.println(
                                    "res" + request.getId() +
                                    ", " + response.getStatusCode() +
                                    ", " + response.getHeaders() +
                                    ", " + response.getCookies() +
                                    ", " + response.getHeaders().get("Authorization")
                            );
                        }
                    })
            );
        });
    }

    static class CacheData {
        private Integer count;
        private final Date date;
        private final String api;
        private final HttpMethod method;

        public CacheData(String api, HttpMethod method) {
            this.count = 1;
            this.date = new Date();
            this.api = api;
            this.method = method;
        }

        public void addCount() {
            this.count++;
        }
    }
}
