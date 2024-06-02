package com.example.demo.routers;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class StaticResourceRouter {
	@Bean
    protected RouterFunction<ServerResponse> staticRoutes(@Value("classpath:/robots.txt") Resource resource) {
        return RouterFunctions.route(
            GET("/robots.txt"),
            request -> ok()
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(resource)
        ).andRoute(GET("/"), request -> ok()
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Running"));
    }
}
