package com.example.demo.routers;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.example.demo.dto.RouteDto;
import com.example.demo.global.Sessions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.netty.channel.AbortedException;

@Component
public class WebSocketSessionRouter {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 
	 * @param
	 * @return
	 * */
	public Mono<WebSocketMessage> route(String payload, WebSocketSession webSocketSession) {
		RouteDto routeDto = msgMapping(payload);
		JSONObject resultJson = new JSONObject();
		resultJson.put("method", routeDto.getMethod());
		if("ROOM_CHANGE".equals(routeDto.getMethod())) {
			Sessions.WS_LIST_BY_MAP.get(routeDto.getRoom()).remove(webSocketSession);
			Sessions.WS_LIST_BY_MAP.get(routeDto.getData()).add(webSocketSession);
			resultJson.put("result", 1 );
			resultJson.put("data", JSONObject.NULL);
		} else if("MSG_SEND".equals(routeDto.getMethod())){
			resultJson.put("result", 1 );
			resultJson.put("data", routeDto.getData());
			
			JSONObject dataJson = new JSONObject();
			dataJson.put("method", "MSG_RECIEVE");
			dataJson.put("data", routeDto.getData());
			if("-1".equals(routeDto.getRoom())) {
				Thread thread = new Thread(()->{
		    		Sessions.WS_LIST
		    			.parallelStream()
		    			.forEach(wsSession -> wsSession.send(Mono.just(webSocketSession.textMessage(dataJson.toString())))
							.onErrorResume(e->{ 
								if((e instanceof AbortedException && e.getMessage().contains("closed"))||
										e instanceof ArrayIndexOutOfBoundsException || e instanceof IndexOutOfBoundsException || e instanceof NullPointerException) {
									return Mono.empty();
								} else {
			     					e.printStackTrace();
			     					return Mono.empty();
			     				}
			         		}).subscribe());
		    		});
				thread.start();
			} else {
				Thread thread = new Thread(()->{
		    		Sessions.WS_LIST_BY_MAP.get(dataJson.get("room"))
		    			.parallelStream()
		    			.forEach(wsSession -> wsSession.send(Mono.just(webSocketSession.textMessage(dataJson.toString())))
							.onErrorResume(e->{ 
								if((e instanceof AbortedException && e.getMessage().contains("closed"))||
										e instanceof ArrayIndexOutOfBoundsException || e instanceof IndexOutOfBoundsException || e instanceof NullPointerException) {
									return Mono.empty();
								} else {
			     					e.printStackTrace();
			     					return Mono.empty();
			     				}
			         		}).subscribe());
		    		});
				thread.start();
			}
			
		}else {
			resultJson.put("result", -1);
			resultJson.put("data", JSONObject.NULL);
		}
		return Mono.just(webSocketSession.textMessage(resultJson.toString()));
	}
	
	/**
	 * - 들어온 메세지를 Map 형태로 매핑해서 DTO로 묶음
	 * 
	 * @param
	 * @return
	 * */
	private RouteDto msgMapping(String payload) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> tmpParams = new HashMap<String, String>();
		try {
			tmpParams = mapper.readValue(payload, new TypeReference<Map<String, String>>() {});
		} catch (Exception e) {
			logger.error(String.format("옳지 않은 메세지 : %s ...", payload.length() > 20 ?  payload.substring(0, 20) : payload).replaceAll(System.getProperty("line.separator"), " "));
		}
		Map<String, String> params = Map.copyOf(tmpParams);
		
		String room = params.get("room");
		String data = params.get("data");
		String method = params.get("method");
		
		return RouteDto.builder()
			.room(room)
			.data(data)
			.method(method)
			.build();
	}
	
	
}
