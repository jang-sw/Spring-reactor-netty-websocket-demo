package com.example.demo.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.example.demo.global.Sessions;
import com.example.demo.routers.WebSocketSessionRouter;
import com.example.demo.util.GlobalUtil;

import reactor.core.publisher.Mono;
import reactor.netty.channel.AbortedException;

public class DemoWebSocketHandler implements WebSocketHandler {
	
	private static WebSocketSessionRouter webSocketSessionRouter;

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public void setWebSocketSessionRouter(WebSocketSessionRouter webSocketSessionRouter) {
		DemoWebSocketHandler.webSocketSessionRouter = webSocketSessionRouter;
	}
	@Override
    public Mono<Void> handle(WebSocketSession session) {
		return session.send(
	        session.receive()
	            .flatMap(message -> webSocketSessionRouter.route(message.getPayloadAsText(), session))
				).onErrorResume(e->{ 
					if(!((e instanceof AbortedException && e.getMessage().contains("closed"))||
							e instanceof ArrayIndexOutOfBoundsException || e instanceof IndexOutOfBoundsException || e instanceof NullPointerException)) {
						e.printStackTrace();
					} 
 					return Mono.empty();
     				
         		}).doOnSubscribe(subscription -> afterConnectionEstablished(session))
		        .doOnTerminate(() -> afterConnectionClosed(session));
    }
	private void afterConnectionEstablished(WebSocketSession session) {
			Sessions.WS_LIST.add(session);
	    	logger.info(String.format("ip: %s에서 소켓에 연결함, 부여된 ID : %s", GlobalUtil.getIpFromWebSocketSession(session), session.getId()));
    }
	private void afterConnectionClosed(WebSocketSession session) {
    	String sessionId = new String(session.getId());
    	try {
    		Sessions.WS_LIST.remove(session);
    		
    		for(String key : Sessions.WS_LIST_BY_MAP.keySet()) {
    			Sessions.WS_LIST_BY_MAP.get(key).remove(session);
    		}
    			
    	}catch (Exception e) {
    		e.printStackTrace();
    		session = null;
		}
    	logger.info(String.format("ip: %s에서 %s의 소켓 연결이 종료됨", GlobalUtil.getIpFromWebSocketSession(session), sessionId));
    	

    }
}
