package com.ecom.apigateway.filter;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class InjectUserIdGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger log = LoggerFactory.getLogger(InjectUserIdGatewayFilterFactory.class);
    private final WebClient webClient;

    public InjectUserIdGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                            @Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    private String idGenerator() {
        return UUID.randomUUID().toString();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                HttpCookie guestCookie = exchange.getRequest().getCookies().getFirst("X-Guest-Id");
                log.info("---------------------------GUEST COOKIE IS_---------------{}",guestCookie);
                String guestId;
                if (guestCookie != null) {
                    guestId = guestCookie.getValue();
                } else {
                    guestId = idGenerator();
                    ServerHttpResponse response = exchange.getResponse();
                    response.addCookie(ResponseCookie.from("X-Guest-Id", guestId).httpOnly(true).maxAge(Duration.ofDays(2)).path("/").build());
                    log.info("cookie is " + response.getCookies().getFirst("X-Guest-Id"));
                }

                ServerHttpRequest request = exchange.getRequest().mutate().header("X-Guest-Id", guestId).build();
                log.info("--------------------{}------------------------", request.getHeaders());
                log.info("Headers after mutating {}", exchange.getRequest().getHeaders());
                return chain.filter(exchange.mutate().request(request).build());
            }

//            return webClient.get()
//                    .uri("/extract-user")
//                    .header("Authorization", token)
//                    .exchangeToMono(clientResponse -> {
//                        if (clientResponse.statusCode().is2xxSuccessful()) {
//                            return clientResponse.bodyToMono(UserResponse.class)
//                                    .flatMap(userResponse -> {
//                                        String userId = userResponse.getUserId();
//                                        log.info("|||||||||||||||==========================IDD========================||||||||||||||||{}", userId);
//                                        ServerHttpRequest mutatedRequest = exchange.getRequest()
//                                                .mutate()
//                                                .header("X-User-Id", userId)
//                                                .build();
//
//                                        ServerWebExchange mutatedExchange = exchange.mutate()
//                                                .request(mutatedRequest)
//                                                .build();
//
//                                        return chain.filter(mutatedExchange);
//                                    });
//                        } else {
//                            // Log or handle non-2xx status
//                            return chain.filter(exchange);
//                        }
//                    });
//        };
//    }
//
//    // JSON DTO
//    public static class UserResponse {
//        private String userId;
//
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//    }
//}


            return webClient.get()
                    .uri("/extract-user")
                    .header("Authorization", token)
                    .exchangeToMono(
                            clientResponse -> {
                                if (clientResponse.statusCode().is2xxSuccessful()){
                                    return clientResponse.bodyToMono(UserResponse.class)
                                            .flatMap(
                                                    userResponse -> {
                                                        if (userResponse.getUserId()!=null) {
                                                            String userId =userResponse.getUserId();
                                                            ServerHttpRequest request= exchange.getRequest().mutate().header("X-User-Id",userId).build();
                                                            return chain.filter(exchange.mutate().request(request).build());
                                                        } else {
                                                            log.error("User ID not found in response");
                                                        }

                                                        return chain.filter(exchange);
                                                    }
                                            ).switchIfEmpty(Mono.defer(
                                                    () -> chain.filter(exchange)
                                            ))
                                    ;
                                } else {
                                    ServerHttpResponse response = exchange.getResponse();
                                    response.setStatusCode(clientResponse.statusCode());
                                    response.getHeaders().putAll(clientResponse.headers().asHttpHeaders());
                                    return response.writeWith(clientResponse.bodyToFlux(DataBuffer.class));
                                }
                            }
                    );
        };


    }

    public static class UserResponse {
        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
