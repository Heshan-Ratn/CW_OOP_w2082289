//package com.hkrw2082289.ticketing_system.websocket;
//
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//public class WebSocketHandlerImpl extends TextWebSocketHandler {
//
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        // This will send a response back to the WebSocket client
//        session.sendMessage(new TextMessage("Message from server: " + message.getPayload()));
//    }
//}
