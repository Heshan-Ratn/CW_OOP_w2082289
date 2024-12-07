////package com.hkrw2082289.ticketing_system.websocket;
////
////import jakarta.websocket.OnMessage;
////import jakarta.websocket.Session;
////import jakarta.websocket.server.ServerEndpoint;
////
////@ServerEndpoint("/ws")  // WebSocket URL path
////public class WebSocketEndpoint {
////
////    @OnMessage
////    public String onMessage(Session session, String message) {
////        // This will just echo back the message for now
////        return "Echo: " + message;
////    }
////}
//
//package com.hkrw2082289.ticketing_system.websocket;
//
//import jakarta.websocket.OnMessage;
//import jakarta.websocket.Session;
//import jakarta.websocket.server.ServerEndpoint;
//import jakarta.websocket.server.ServerEndpointConfig;
//
//
//import java.util.List;
//
//@ServerEndpoint(value = "/ws", configurator = WebSocketEndpoint.CustomConfigurator.class)
//public class WebSocketEndpoint {
//
//    @OnMessage
//    public String onMessage(Session session, String message) {
//        // This will just echo back the message for now
//        return "Echo: " + message;
//    }
//
//    public static class CustomConfigurator extends ServerEndpointConfig.Configurator {
//        @Override
//        public void modifyHandshake(ServerEndpointConfig config,
//                                    jakarta.websocket.server.HandshakeRequest request,
//                                    jakarta.websocket.server.HandshakeResponse response) {
//            response.getHeaders().put("Access-Control-Allow-Origin", List.of("http://localhost:5173"));
//        }
//    }
//}
//
