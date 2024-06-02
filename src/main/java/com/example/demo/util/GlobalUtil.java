package com.example.demo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.WebSocketSession;

import io.netty.util.internal.StringUtil;

public class GlobalUtil {

	/**
	 * 파라메터 null값 체크
	 * 
	 * @param
	 * @return
	 * */
	public static List<String> getNullParams(Map<String, String> params, String... names){
		List<String> nullParams = new ArrayList<>();
		for(int i = 0; i < names.length; i++) {
			if(StringUtil.isNullOrEmpty(params.get(names[i]))) nullParams.add(names[i]);
		}
		return nullParams;
	}
	
	/**
	 * ip 가져오기(websocket)
	 * 
	 * @param
	 * @return
	 * */
	public static String getIpFromWebSocketSession(WebSocketSession webSocketSession){
		HttpHeaders httpHeaders = webSocketSession.getHandshakeInfo().getHeaders();
    	return httpHeaders.get("X-Real-IP") != null &&  httpHeaders.get("X-Real-IP").size() > 0 ? httpHeaders.get("X-Real-IP").get(0) : 
    		 httpHeaders.get("X-Forwarded-For") != null &&  httpHeaders.get("X-Forwarded-For").size() > 0 ? httpHeaders.get("X-Forwarded-For").get(0) : 
    			 webSocketSession.getHandshakeInfo().getRemoteAddress().getAddress().toString();
		
	}
	
}
