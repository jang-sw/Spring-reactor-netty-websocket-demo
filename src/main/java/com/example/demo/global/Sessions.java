package com.example.demo.global;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.reactive.socket.WebSocketSession;


public class Sessions {
	
	public static CopyOnWriteArrayList<WebSocketSession> WS_LIST = new CopyOnWriteArrayList<>(); 	
	
	public static Map<String, CopyOnWriteArrayList<WebSocketSession>> WS_LIST_BY_MAP = new HashMap<>();
	// mapId : ws_list

}
