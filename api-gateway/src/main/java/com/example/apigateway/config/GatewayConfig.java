package com.example.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("board-service", r -> r
                        .path("/board/**")
                        .filters(f -> f
                                .rewritePath("/board/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Token", "22ccd954-75-4b303a16f548"))
                        .uri("http://board-service:8084"))
                .route("task-service", r -> r
                        .path("/task/**")
                        .filters(f -> f
                                .rewritePath("/task/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Token", "22ccd954-7c73-43f7-a5f5-4b303a16f588"))
                        .uri("http://task-service:8081"))
                .route("user-service", r -> r
                        .path("/user/**")
                        .filters(f -> f
                                .rewritePath("/user/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Token", "22ccd954-7c73-43f7-a5f5-4b303a16f588"))
                        .uri("http://user-service:8082"))
                .route("reminder-service", r -> r
                        .path("/reminder/**")
                        .filters(f -> f
                                .rewritePath("/reminder/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Token", "22ccd954-7c73-43f7-a5f5-4b303a16f588"))
                        .uri("http://reminder-service:8083"))
                .route("search-service", r -> r
                        .path("/search/**")
                        .filters(f -> f
                                .rewritePath("/search/(?<segment>.*)", "/${segment}")
                                .addRequestHeader("X-Gateway-Token", "22ccd954-7c73-43f7-a5f5-4b303a16f588"))
                        .uri("http://search-service:8082"))
                .build();
    }
}
