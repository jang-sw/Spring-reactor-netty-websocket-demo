package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.example.demo.handler.DemoWebSocketHandler;

@Configuration
@EnableAsync
public class WebSocketConfig {
	@Bean
    protected SimpleUrlHandlerMapping webSocketHandlerMapping(DemoWebSocketHandler webSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws", webSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);

        return handlerMapping;
    }

    @Bean
    protected WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    protected DemoWebSocketHandler webSocketHandler() {
        return new DemoWebSocketHandler();
    }

}
