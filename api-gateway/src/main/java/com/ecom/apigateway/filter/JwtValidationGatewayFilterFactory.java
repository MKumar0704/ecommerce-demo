package com.ecom.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webCLientBuilder,
                                             @Value("${auth.service.url}") String authServiceUrl ){
        this.webClient=webCLientBuilder.baseUrl(authServiceUrl).build();
    }


    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if(token==null || !token.startsWith("Bearer ")){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION,token)
                    .exchangeToMono(
                            clientResponse -> {
                                if(clientResponse.statusCode().is2xxSuccessful()){
                                    return chain.filter(exchange);
                                }else {
                                    ServerHttpResponse response=exchange.getResponse();
                                    response.setStatusCode(clientResponse.statusCode());
                                    response.getHeaders().putAll(clientResponse.headers().asHttpHeaders());
                                    return response.writeWith(clientResponse.bodyToFlux(DataBuffer.class));
                                }
                            }
                    );

        };
    }
}
